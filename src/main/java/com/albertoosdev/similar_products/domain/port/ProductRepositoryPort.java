package com.albertoosdev.similar_products.domain.port;

import com.albertoosdev.similarproducts.openapi.infrastructure.out.dto.ProductDetail;
import java.util.List;
import java.util.Optional;

public interface ProductRepositoryPort {

    List<String> getSimilarIds(final String productId);
    Optional<ProductDetail> getProductDetail(final String similarId);
}
