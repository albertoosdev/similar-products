package com.albertoosdev.similar_products.infrastructure.out.rest;

import com.albertoosdev.similar_products.domain.exception.ProductNotFoundException;
import com.albertoosdev.similar_products.domain.model.ProductDetail;
import com.albertoosdev.similar_products.domain.port.ProductRepositoryPort;
import com.albertoosdev.similar_products.infrastructure.out.rest.mapper.ProductDetailMapper;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductRestAdapter implements ProductRepositoryPort {

    private final RestClient restClient;
    private final ProductDetailMapper mapper;

    private static final String CB_IDS = "external-similar-ids";
    private static final String CB_DETAIL = "external-product-detail";

    private static final String SIMILAR_IDS_PATH = "/product/{productId}/similarids";
    private static final String PRODUCT_DETAIL_PATH = "/product/{productId}";

    @Override
    @CircuitBreaker(name = CB_IDS, fallbackMethod = "fallbackGetSimilarIds")
    public List<String> getSimilarIds(final String productId) {
        log.debug("Requesting similar IDs for product [{}] to external API", productId);

        List<String> result = restClient.get()
                .uri(SIMILAR_IDS_PATH, productId)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, resp) -> {
                    if (resp.getStatusCode().value() == 404) {
                        log.warn("Product {} not found in external API (404).", productId);
                        throw new ProductNotFoundException("Product " + productId + " not found");
                    }
                })
                .body(new ParameterizedTypeReference<>() {});

        log.debug("Retrieved {} similar IDs for product [{}]: {}",
                result != null ? result.size() : 0, productId, result);
        return result;
    }


    @Override
    @CircuitBreaker(name = CB_DETAIL, fallbackMethod = "fallbackGetDetail")
    public Optional<ProductDetail> getProductDetail(final String similarId) {
        try {
            log.debug("Fetching details for similar product ID: {}", similarId);

            ProductDetail detail = mapper.mapToDomain(restClient.get()
                    .uri(PRODUCT_DETAIL_PATH, similarId)
                    .retrieve()
                    .body(com.albertoosdev.similarproducts.openapi.infrastructure.out.dto.ProductDetailExternal.class));

            log.debug("External DTO received for ID {}: {}", similarId, detail);

            return Optional.ofNullable(detail);

        } catch (HttpClientErrorException.NotFound e) {
            log.warn("Similar product {} not found (404). Skipping.", similarId);
            return Optional.empty();
        }
    }

    public List<String> fallbackGetSimilarIds(String id, Throwable t) {
        if (t instanceof ProductNotFoundException e) {
            throw e;
        }

        log.error("Fallback triggered fetching similar IDs for product {}. Reason: {}. Returning empty list.",
                id, t.getMessage());

        return List.of();
    }

    public Optional<ProductDetail> fallbackGetDetail(String id, Throwable t) {
        log.warn("Fallback triggered fetching detail for product {}. Reason: {}. Skipping product.",
                id, t.getMessage());

        return Optional.empty();
    }
}
