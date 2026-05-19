package com.template.ms_pedidos.domain.ports;

import com.template.ms_pedidos.domain.model.Pedido;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PedidoRepositoryPort {
    Flux<Pedido> findAllOrderByFecha();
    Mono<Pedido> save(Pedido pedido);
    Mono<Pedido> findById(String id);
}