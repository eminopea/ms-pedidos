package com.template.ms_pedidos.infrastructure.adapters.inbound.controller;

import org.openapitools.api.PedidosApi;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import com.template.ms_pedidos.application.usecase.ConsultarPedidosUseCase;
import com.template.ms_pedidos.application.usecase.CrearPedidoUseCase;
import com.template.ms_pedidos.infrastructure.adapters.inbound.mapper.PedidoMapper;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.openapitools.model.PedidoResponse;
import org.openapitools.model.PedidoRequest;


@RestController
@RequiredArgsConstructor
public class PedidosApiController implements PedidosApi {

    private final CrearPedidoUseCase crearUseCase;
    private final ConsultarPedidosUseCase consultarUseCase;
    private final PedidoMapper mapper;

    @Override
    public Mono<ResponseEntity<Flux<PedidoResponse>>> pedidosGet(ServerWebExchange exchange) {

        Flux<PedidoResponse> response = consultarUseCase.execute()
                .map(mapper::toResponse);

        return Mono.just(ResponseEntity.ok(response));
    }

    @Override
    public Mono<ResponseEntity<PedidoResponse>> pedidosPost(
            Mono<PedidoRequest> pedidoRequest,
            ServerWebExchange exchange) {

        return pedidoRequest
                .flatMap(crearUseCase::ejecutar)
                .map(mapper::toResponse)
                .map(ResponseEntity::ok);
    }
}