package com.albertoosdev.similar_products.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class ProductDetail {
    private String id;

    private String name;

    private BigDecimal price;

    private Boolean availability;

}
