package com.albertoosdev.similar_products.infrastructure.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(ProductsApiProperties.class)
public class RestClientConfig {

    @Bean("productsRestClient")
    public RestClient productsRestClient(RestClient.Builder builder, ProductsApiProperties properties) {

        var requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout((int) properties.connectionTimeout().toMillis());
        requestFactory.setReadTimeout((int) properties.readTimeout().toMillis());

        return builder
                .baseUrl(properties.url())
                .requestFactory(requestFactory)
                .build();
    }
}
