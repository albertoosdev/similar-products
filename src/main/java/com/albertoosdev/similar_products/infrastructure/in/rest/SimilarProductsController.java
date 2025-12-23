package com.albertoosdev.similar_products.infrastructure.in.rest;

import com.albertoosdev.similar_products.domain.port.GetProductSimilarPort;
import com.albertoosdev.similar_products.infrastructure.in.rest.mapper.ProductDtoMapper;
import com.albertoosdev.similarproducts.openapi.infrastructure.in.rest.api.DefaultApi;
import com.albertoosdev.similarproducts.openapi.infrastructure.in.rest.model.ProductDetailResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
public class SimilarProductsController implements DefaultApi {

    private final GetProductSimilarPort getProductSimilarUseCase;

    private final ProductDtoMapper productDtoMapper;

    /**
     * GET /product/{productId}/similar : Similar products
     *
     * @param productId (required)
     * @return OK (status code 200)
     * or Product Not found (status code 404)
     */
    @Override
    public ResponseEntity<Set<ProductDetailResponse>> getProductSimilar(String productId) {
        log.info("REST Request to get similar products for ID: {}", productId);

        var domainProducts = getProductSimilarUseCase.execute(productId);

        Set<ProductDetailResponse> response = domainProducts.stream()
                .map(productDtoMapper::mapToResponse)
                .collect(Collectors.toSet());

        return ResponseEntity.ok(response);
    }
}
