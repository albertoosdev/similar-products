package com.albertoosdev.similar_products.domain.port;

import com.albertoosdev.similar_products.domain.model.ProductDetail;

import java.util.Set;

public interface GetProductSimilarPort {
    Set<ProductDetail> execute(final String productId);
}
