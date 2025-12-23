package com.albertoosdev.similar_products.infrastructure.out.rest.mapper;

import com.albertoosdev.similar_products.domain.model.ProductDetail;
import com.albertoosdev.similarproducts.openapi.infrastructure.out.dto.ProductDetailExternal;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ProductDetailMapper {

    ProductDetail mapToDomain(ProductDetailExternal externalProduct);
}
