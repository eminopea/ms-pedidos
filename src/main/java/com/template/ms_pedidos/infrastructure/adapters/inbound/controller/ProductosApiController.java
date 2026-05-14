package com.template.ms_pedidos.infrastructure.adapters.inbound.controller;

import org.springframework.http.ResponseEntity; 

import com.template.ms_pedidos.application.usecase.CrearProductoUseCase;
import com.template.ms_pedidos.infrastructure.adapters.inbound.mapper.ProductoMapper;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import org.openapitools.api.ProductosApi;
import org.openapitools.model.ProductoResponse;
import org.openapitools.model.ProductoRequest;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

@RestController
@RequiredArgsConstructor
public class ProductosApiController implements ProductosApi {

    private final CrearProductoUseCase crearProductoUseCase;
    private final ProductoMapper mapper;

    // exchange: Permite acceder a detalles de la solicitud HTTP, como encabezados, parámetros, etc. 
    // Es útil para obtener información adicional sobre la solicitud que no está directamente relacionada con el cuerpo del mensaje.
    @Override
    public Mono<ResponseEntity<ProductoResponse>> productosPost(
            Mono<ProductoRequest> productoRequest,
            ServerWebExchange exchange) {

        return productoRequest
                .flatMap(crearProductoUseCase::ejecutar)
                .map(mapper::toResponse)
                .map(ResponseEntity::ok);
    }
}

