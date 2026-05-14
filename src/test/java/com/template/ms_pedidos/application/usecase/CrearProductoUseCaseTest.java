package com.template.ms_pedidos.application.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.model.ProductoRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.template.ms_pedidos.domain.ports.ProductoRepositoryPort;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class CrearProductoUseCaseTest {

    @Mock
    private ProductoRepositoryPort repository;

    @InjectMocks
    private CrearProductoUseCase useCase;

    private ProductoRequest request;

    @BeforeEach
    void setUp() {
        request = new ProductoRequest();
        request.setNombre("Laptop");
        request.setPrecio(new BigDecimal("1000"));
        request.setStock(10);
    }

    @Test
    void ejecutar_ok() {

        when(repository.existsByNombre(any())).thenReturn(Mono.just(false));
        when(repository.save(any())).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(useCase.ejecutar(request))
                .assertNext(producto -> {
                    assertEquals("Laptop", producto.getNombre());
                    assertEquals(10, producto.getStock());
                    assertEquals(new BigDecimal("1000"), producto.getPrecio());
                })
                .verifyComplete();

        verify(repository).existsByNombre(any());
        verify(repository).save(any());
    }

    @Test
    void ejecutar_nombreDuplicado() {

        when(repository.existsByNombre(any()))
                .thenReturn(Mono.just(true));

        StepVerifier.create(useCase.ejecutar(request))
                .expectErrorMatches(err ->
                        err instanceof ResponseStatusException &&
                        ((ResponseStatusException) err).getStatusCode() == HttpStatus.CONFLICT
                )
                .verify();

        verify(repository).existsByNombre(any());
        verify(repository, never()).save(any());
    }
}