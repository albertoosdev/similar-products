package com.albertoosdev.similar_products.application;

import com.albertoosdev.similar_products.domain.model.ProductDetail;
import com.albertoosdev.similar_products.domain.port.GetProductSimilarPort;
import com.albertoosdev.similar_products.domain.port.ProductRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetProductSimilarUseCase implements GetProductSimilarPort {

    private final ProductRepositoryPort productRepositoryPort;

    @Override
    public Set<ProductDetail> execute(final String productId) {
        log.info("Fetching similar products for product ID: {}", productId);

        List<String> similarIds = productRepositoryPort.getSimilarIds(productId);
        return similarIds.stream()
                .map(productRepositoryPort::getProductDetail)
                .flatMap(Optional::stream)
                .collect(Collectors.toSet());
    }
}
