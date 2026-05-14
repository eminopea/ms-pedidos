package com.template.ms_pedidos.infrastructure.adapters.outbound.mongo.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.template.ms_pedidos.domain.model.Producto;

import reactor.core.publisher.Mono;

@Repository
public interface ProductoReactiveRepository  extends ReactiveMongoRepository<Producto, String> {
      
    Mono<Boolean> existsByNombre(String nombre);

}