package com.template.ms_pedidos.infrastructure.adapters.outbound.mongo;

import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import com.template.ms_pedidos.domain.model.Producto;
import com.template.ms_pedidos.domain.ports.ProductoRepositoryPort;
import com.template.ms_pedidos.infrastructure.adapters.outbound.mongo.repository.ProductoReactiveRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ProductoRepositoryAdapter implements ProductoRepositoryPort {

    private final ProductoReactiveRepository repository;
    private final ReactiveMongoTemplate mongoTemplate;

    @Override
    public Mono<Producto> save(Producto producto) {
        return repository.save(producto);
    }

    @Override
    public Mono<Producto> findById(String id) {
        return repository.findById(id);
    }

    @Override
    public Mono<Boolean> descontarStock(String id, int cantidad) {

        Query query = new Query(
                Criteria.where("_id").is(id)
                        .and("stock").gte(cantidad)
        );

        Update update = new Update().inc("stock", -cantidad);

        return mongoTemplate.updateFirst(query, update, Producto.class)
                .map(result -> result.getModifiedCount() > 0);

    }

    @Override
    public Mono<Boolean> existsByNombre(String nombre) {
        return repository.existsByNombre(nombre);
    }

}
