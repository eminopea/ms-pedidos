package com.template.ms_pedidos.domain.ports;

import com.template.ms_pedidos.domain.model.Producto;

import reactor.core.publisher.Mono;

public interface ProductoRepositoryPort {
    Mono<Producto> findById(String id);
    Mono<Producto> save(Producto producto); 
    Mono<Boolean> descontarStock(String id, int cantidad);
    Mono<Boolean> existsByNombre(String nombre);
}