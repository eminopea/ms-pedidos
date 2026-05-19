package com.template.ms_pedidos.domain.ports;

import com.template.ms_pedidos.infrastructure.adapters.inbound.dto.Cliente;

import reactor.core.publisher.Mono;

public interface ClienteRepositoryPort {
    Mono<Cliente> obtenerPorDni(String dni);
}
