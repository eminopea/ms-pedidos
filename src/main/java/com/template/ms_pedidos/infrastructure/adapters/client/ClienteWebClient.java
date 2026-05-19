package com.template.ms_pedidos.infrastructure.adapters.client;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.template.ms_pedidos.infrastructure.adapters.inbound.dto.Cliente;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ClienteWebClient {

    private final WebClient webClient;

    public Mono<Cliente> obtenerPorDni(String dni) {
        return webClient.get()
                .uri("http://localhost:7080/api/v1/customers/dni/{dni}", dni)
                .retrieve()
                .bodyToMono(Cliente.class)
                .onErrorResume(WebClientResponseException.NotFound.class, ex -> Mono.empty());
    }

}
