package com.template.ms_pedidos.infrastructure.adapters.outbound.mongo;

import org.springframework.stereotype.Component;

import com.template.ms_pedidos.domain.model.Pedido;
import com.template.ms_pedidos.domain.ports.PedidoRepositoryPort;
import com.template.ms_pedidos.infrastructure.adapters.outbound.mongo.repository.PedidoReactiveRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class PedidoRepositoryAdapter implements PedidoRepositoryPort {

    private final PedidoReactiveRepository repo;

    @Override
    public Flux<Pedido> findAllOrderByFecha() {
        return repo.findAllByOrderByFechaRegistroAsc();
    }

    @Override
    public Mono<Pedido> save(Pedido pedido) {
        return repo.save(pedido);
    }
}
