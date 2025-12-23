package com.albertoosdev.similar_products.infrastructure.in.rest.mapper;

import com.albertoosdev.similar_products.domain.model.ProductDetail;
import com.albertoosdev.similarproducts.openapi.infrastructure.in.rest.model.ProductDetailResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductDtoMapper {
    ProductDetailResponse mapToResponse(ProductDetail domain);
}
