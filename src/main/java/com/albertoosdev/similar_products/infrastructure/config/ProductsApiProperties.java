package com.albertoosdev.similar_products.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "external.products-api")
public record ProductsApiProperties (
        String url,
        Duration connectionTimeout,
        Duration readTimeout
) {}