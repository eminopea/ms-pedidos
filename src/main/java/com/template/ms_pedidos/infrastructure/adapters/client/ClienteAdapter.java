package com.template.ms_pedidos.infrastructure.adapters.client;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import com.template.ms_pedidos.infrastructure.adapters.inbound.dto.Cliente;
import com.template.ms_pedidos.domain.ports.ClienteRepositoryPort;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono; 

@Component
@RequiredArgsConstructor
@Slf4j
public class ClienteAdapter implements ClienteRepositoryPort {

    private final ClienteWebClient clienteWebClient;

    @Override
    @CircuitBreaker(name = "clienteService", fallbackMethod = "fallbackCliente")
    public Mono<Cliente> obtenerPorDni(String dni) {
        return clienteWebClient.obtenerPorDni(dni);
    }

    // ✅ fallback
    public Mono<Cliente> fallbackCliente(String dni, Throwable ex) {
        log.error("Circuit breaker activado para DNI {}: {}", dni, ex.getMessage());

        return Mono.error(new ResponseStatusException(
                HttpStatus.SERVICE_UNAVAILABLE,
                "Servicio de cliente no disponible"
        ));
    }
}
