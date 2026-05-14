package com.template.ms_pedidos.application.usecase;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.template.ms_pedidos.domain.model.Producto;
import com.template.ms_pedidos.domain.ports.ProductoRepositoryPort;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class ObtenerProductoUseCaseTest {

    @Mock
    private ProductoRepositoryPort repository;

    @InjectMocks
    private ObtenerProductoUseCase useCase;

    @Test
    void ejecutar_ok() {

        Producto producto = Producto.builder()
                .id("1")
                .nombre("Laptop")
                .build();

        when(repository.findById(anyString()))
                .thenReturn(Mono.just(producto));

        StepVerifier.create(useCase.ejecutar("1"))
                .expectNext(producto)
                .verifyComplete();

        verify(repository).findById(anyString());
    }

    @Test
    void ejecutar_noEncontrado() {

        when(repository.findById(anyString()))
                .thenReturn(Mono.empty());

        StepVerifier.create(useCase.ejecutar("1"))
                .expectErrorMatches(err ->
                        err instanceof RuntimeException &&
                        err.getMessage().equals("Producto no encontrado")
                )
                .verify();

        verify(repository).findById(anyString());
    }
}
