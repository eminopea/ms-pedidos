package com.template.ms_pedidos.application.usecase;

import org.springframework.stereotype.Service;

import com.template.ms_pedidos.domain.model.Producto;
import com.template.ms_pedidos.domain.ports.ProductoRepositoryPort;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ObtenerProductoUseCase {

    private final ProductoRepositoryPort repository;

    public Mono<Producto> ejecutar(String id) {

        return repository.findById(id)
                .switchIfEmpty(Mono.error(
                        new RuntimeException("Producto no encontrado")
                ));
    }
}
