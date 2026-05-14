package com.template.ms_pedidos.application.usecase;

import java.time.Duration;

import org.springframework.stereotype.Service;

import com.template.ms_pedidos.domain.model.Pedido;
import com.template.ms_pedidos.domain.ports.PedidoRepositoryPort;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class ConsultarPedidosUseCase {

    private final PedidoRepositoryPort repo;

    public Flux<Pedido> execute() {
        return repo.findAllOrderByFecha()
                .timeout(Duration.ofSeconds(5));
    }
}
