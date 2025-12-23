package com.albertoosdev.similar_products.infrastructure.out.rest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Objects;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "external.products-api.url=http://localhost:${wiremock.server.port}"
        }
)
@AutoConfigureWireMock(port = 0)
@ActiveProfiles("test")
class ProductRestAdapterCacheTest {
    @Autowired
    private ProductRestAdapter adapter;

    @Autowired
    private CacheManager cacheManager;


    @Test
    void shouldCacheSimilarIdsRequest() {
        // GIVEN:
        String productId = "1";
        stubFor(get(urlEqualTo("/product/" + productId + "/similarids"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("[\"100\", \"200\"]")
                        .withStatus(200)));

        // WHEN:
        List<String> firstCall = adapter.getSimilarIds(productId);

        // THEN 1:
        assertThat(firstCall).containsExactly("100", "200");
        verify(1, getRequestedFor(urlEqualTo("/product/" + productId + "/similarids")));

        // WHEN:
        List<String> secondCall = adapter.getSimilarIds(productId);

        // THEN 2:
        assertThat(secondCall).containsExactly("100", "200");
        verify(1, getRequestedFor(urlEqualTo("/product/" + productId + "/similarids")));

        assertThat(Objects.requireNonNull(cacheManager.getCache("similarIds")).get(productId)).isNotNull();
    }

    @Test
    void shouldCacheProductDetailRequest() {
        // GIVEN
        String similarId = "100";
        stubFor(get(urlEqualTo("/product/" + similarId))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                            {
                                "id": "100",
                                "name": "Dress",
                                "price": 19.99,
                                "availability": true
                            }
                        """)
                        .withStatus(200)));

        // WHEN 1:
        adapter.getProductDetail(similarId);

        // WHEN 2:
        adapter.getProductDetail(similarId);
        adapter.getProductDetail(similarId);

        // THEN:
        verify(1, getRequestedFor(urlEqualTo("/product/" + similarId)));

        assertThat(Objects.requireNonNull(cacheManager.getCache("productDetails")).get(similarId)).isNotNull();
    }
}
