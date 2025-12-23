package com.albertoosdev.similar_products.application;

import com.albertoosdev.similar_products.domain.exception.ProductNotFoundException;
import com.albertoosdev.similar_products.domain.model.ProductDetail;
import com.albertoosdev.similar_products.domain.port.ProductRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetProductSimilarUseCaseTest {

    @Mock
    private ProductRepositoryPort productRepositoryPort;

    @InjectMocks
    private GetProductSimilarUseCase useCase;

    @Test
    void execute_shouldReturnSetOfProducts_whenIdsAndDetailsAreFound() {
        // GIVEN
        String productId = "1";
        List<String> similarIds = List.of("10", "20");

        ProductDetail detail1 = mock(ProductDetail.class);
        ProductDetail detail2 = mock(ProductDetail.class);

        when(productRepositoryPort.getSimilarIds(productId)).thenReturn(similarIds);

        when(productRepositoryPort.getProductDetail("10")).thenReturn(Optional.of(detail1));
        when(productRepositoryPort.getProductDetail("20")).thenReturn(Optional.of(detail2));

        // WHEN
        Set<ProductDetail> result = useCase.execute(productId);

        // THEN
        assertThat(result).hasSize(2).contains(detail1, detail2);

        verify(productRepositoryPort).getSimilarIds(productId);
        verify(productRepositoryPort, times(2)).getProductDetail(anyString());
    }

    @Test
    void execute_shouldFilterOutEmptyDetails_whenSomeProductsAreMissing() {
        // GIVEN:
        String productId = "1";
        List<String> similarIds = List.of("10", "20", "30");

        ProductDetail detail1 = mock(ProductDetail.class);

        when(productRepositoryPort.getSimilarIds(productId)).thenReturn(similarIds);

        when(productRepositoryPort.getProductDetail("10")).thenReturn(Optional.of(detail1));
        when(productRepositoryPort.getProductDetail("20")).thenReturn(Optional.empty());
        when(productRepositoryPort.getProductDetail("30")).thenReturn(Optional.of(mock(ProductDetail.class)));

        // WHEN
        Set<ProductDetail> result = useCase.execute(productId);

        // THEN
        assertThat(result)
                .hasSize(2)
                .contains(detail1);
    }

    @Test
    void execute_shouldReturnEmptySet_whenNoSimilarIdsFound() {
        // GIVEN
        String productId = "1";
        when(productRepositoryPort.getSimilarIds(productId)).thenReturn(List.of());

        // WHEN
        Set<ProductDetail> result = useCase.execute(productId);

        // THEN
        assertThat(result).isEmpty();
        verify(productRepositoryPort, never()).getProductDetail(anyString());
    }

    @Test
    void execute_shouldPropagateException_whenGetIdsFails() {
        // GIVEN:
        String productId = "999";
        when(productRepositoryPort.getSimilarIds(productId))
                .thenThrow(new ProductNotFoundException("Product not found"));

        // WHEN & THEN: El
        assertThrows(ProductNotFoundException.class, () -> useCase.execute(productId));

        verify(productRepositoryPort, never()).getProductDetail(anyString());
    }
}
