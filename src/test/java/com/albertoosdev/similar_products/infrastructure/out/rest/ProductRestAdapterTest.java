package com.albertoosdev.similar_products.infrastructure.out.rest;

import com.albertoosdev.similar_products.domain.exception.ProductNotFoundException;
import com.albertoosdev.similar_products.domain.model.ProductDetail;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;

import java.util.List;
import java.util.Optional;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "external.products-api.url=http://localhost:${wiremock.server.port}"
        }
)
@AutoConfigureWireMock(port = 0)
class ProductRestAdapterTest {

    @Autowired
    private ProductRestAdapter adapter;

    @Test
    void getSimilarIds_shouldReturnList_whenApiReturns200() {
        // GIVEN:
        String productId = "1";
        stubFor(get(urlEqualTo("/product/" + productId + "/similarids"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("[\"2\", \"3\", \"4\"]")));

        // WHEN:
        List<String> result = adapter.getSimilarIds(productId);

        // THEN:
        assertThat(result).hasSize(3).contains("2", "3", "4");
    }

    @Test
    void getSimilarIds_shouldThrowProductNotFound_whenApiReturns404() {
        // GIVEN
        String productId = "999";
        stubFor(get(urlEqualTo("/product/" + productId + "/similarids"))
                .willReturn(aResponse().withStatus(404)));

        // WHEN & THEN:
        assertThrows(ProductNotFoundException.class, () -> adapter.getSimilarIds(productId));
    }

    @Test
    void getSimilarIds_shouldReturnEmptyList_whenApiReturns500_Fallback() {
        // GIVEN:
        String productId = "500";
        stubFor(get(urlEqualTo("/product/" + productId + "/similarids"))
                .willReturn(aResponse().withStatus(500)));

        // WHEN
        List<String> result = adapter.getSimilarIds(productId);

        // THEN:
        assertThat(result).isEmpty();
    }


    @Test
    void getProductDetail_shouldReturnDetail_whenApiReturns200() {
        // GIVEN
        String similarId = "2";
        stubFor(get(urlEqualTo("/product/" + similarId))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        // Ojo: json simple simulando tu DTO
                        .withBody("""
                            {
                                "id": "2",
                                "name": "Dress",
                                "price": 19.99,
                                "availability": true
                            }
                        """)));

        // WHEN
        Optional<ProductDetail> result = adapter.getProductDetail(similarId);

        // THEN
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo("2");
        assertThat(result.get().getName()).isEqualTo("Dress");
    }

    @Test
    void getProductDetail_shouldReturnEmpty_whenApiReturns404() {
        // GIVEN
        String similarId = "5";
        stubFor(get(urlEqualTo("/product/" + similarId))
                .willReturn(aResponse().withStatus(404)));

        // WHEN
        Optional<ProductDetail> result = adapter.getProductDetail(similarId);

        // THEN:
        assertThat(result).isEmpty();
    }

    @Test
    void getProductDetail_shouldReturnEmpty_whenApiReturns500_Fallback() {
        // GIVEN
        String similarId = "6";
        stubFor(get(urlEqualTo("/product/" + similarId))
                .willReturn(aResponse().withStatus(500)));

        // WHEN
        Optional<ProductDetail> result = adapter.getProductDetail(similarId);

        // THEN:
        assertThat(result).isEmpty();
    }
}
