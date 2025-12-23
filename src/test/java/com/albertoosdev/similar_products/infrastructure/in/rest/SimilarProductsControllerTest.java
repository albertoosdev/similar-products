package com.albertoosdev.similar_products.infrastructure.in.rest;

import com.albertoosdev.similar_products.domain.exception.ProductNotFoundException;
import com.albertoosdev.similar_products.domain.model.ProductDetail;
import com.albertoosdev.similar_products.domain.port.GetProductSimilarPort;
import com.albertoosdev.similar_products.infrastructure.in.rest.mapper.ProductDtoMapper;
import com.albertoosdev.similarproducts.openapi.infrastructure.in.rest.model.ProductDetailResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SimilarProductsController.class)
class SimilarProductsControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GetProductSimilarPort getProductSimilarUseCase;

    @MockitoBean
    private ProductDtoMapper productDtoMapper;

    private static final String URL_TEMPLATE = "/product/{productId}/similar";

    @Test
    void getProductSimilar_shouldReturn200AndList_whenProductsFound() throws Exception {
        // GIVEN
        String productId = "1";

        // 1. Preparamos datos de Dominio (Mock)
        ProductDetail domainProduct = ProductDetail.builder()
                .id("10")
                .name("Name")
                .price(BigDecimal.valueOf(100.0))
                .availability(true)
                .build();

        // 2. Preparamos datos de Respuesta (DTO)
        ProductDetailResponse responseDto = new ProductDetailResponse();
        responseDto.setId("10");
        responseDto.setName("Name");
        // ... settea los campos necesarios del DTO

        // 3. Mockeamos las llamadas
        given(getProductSimilarUseCase.execute(productId)).willReturn(Set.of(domainProduct));
        given(productDtoMapper.mapToResponse(any(ProductDetail.class))).willReturn(responseDto);

        // WHEN & THEN
        mockMvc.perform(get(URL_TEMPLATE, productId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                // Verificamos el contenido del JSON
                .andExpect(jsonPath("$[0].id").value("10"))
                .andExpect(jsonPath("$[0].name").value("Name"));

        verify(getProductSimilarUseCase).execute(productId);
    }

    @Test
    void getProductSimilar_shouldReturn200AndEmptyList_whenNoSimilarProducts() throws Exception {
        // GIVEN
        String productId = "1";
        given(getProductSimilarUseCase.execute(productId)).willReturn(Set.of());

        // WHEN & THEN
        mockMvc.perform(get(URL_TEMPLATE, productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getProductSimilar_shouldPropagateException_whenUseCaseFails() throws Exception {
        // GIVEN
        String productId = "999";
        // Simulamos que el caso de uso lanza la excepción de negocio
        given(getProductSimilarUseCase.execute(productId))
                .willThrow(new ProductNotFoundException("Product not found"));

        // WHEN & THEN
        // NOTA: Al no tener el ControllerAdvice configurado en este test slice,
        // Spring lanzará la excepción hacia arriba. Esto verifica que el Controller NO se la come.
        // Si tuvieras el ControllerAdvice, pondrías .andExpect(status().isNotFound())
        mockMvc.perform(get(URL_TEMPLATE, productId))
                .andExpect(status().isNotFound());
        // IMPORTANTE: Para que esto pase (isNotFound), necesitas que el GlobalExceptionHandler
        // exista y esté escaneado, o añadirlo a este test.
        // Si no tienes el ExceptionHandler aún, cambia esto por una aserción de excepción.
    }
}
