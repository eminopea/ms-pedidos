package com.template.ms_pedidos.infrastructure.adapters.outbound.mongo.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.template.ms_pedidos.domain.model.Pedido;

import reactor.core.publisher.Flux;

@Repository
public interface PedidoReactiveRepository 
        extends ReactiveMongoRepository<Pedido, String> {

    Flux<Pedido> findAllByOrderByFechaRegistroAsc();
}