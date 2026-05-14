package com.template.ms_pedidos.application.usecase;

import org.openapitools.model.ProductoRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.template.ms_pedidos.domain.model.Producto;
import com.template.ms_pedidos.domain.ports.ProductoRepositoryPort;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CrearProductoUseCase {

        private final ProductoRepositoryPort repository;

        public Mono<Producto> ejecutar(ProductoRequest request) {

                return repository.existsByNombre(request.getNombre())
                                .flatMap(exists -> {

                                        if (exists) {
                                                return Mono.error(new ResponseStatusException(
                                                                HttpStatus.CONFLICT,
                                                                "Ya existe un producto con ese nombre"));
                                        }

                                        Producto producto = Producto.builder()
                                                        .nombre(request.getNombre())
                                                        .precio(request.getPrecio())
                                                        .stock(request.getStock())
                                                        .build();

                                        return repository.save(producto);
                                });
        }

}
