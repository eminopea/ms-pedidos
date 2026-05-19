package com.template.ms_pedidos.application.usecase;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;

import com.template.ms_pedidos.domain.events.PedidoCreadoEvent;
import com.template.ms_pedidos.domain.ports.outbound.KafkaEventPort;
import org.openapitools.model.PedidoRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.template.ms_pedidos.infrastructure.adapters.inbound.dto.ProductoDetalle;
import com.template.ms_pedidos.domain.model.Pedido;
import com.template.ms_pedidos.domain.ports.ClienteRepositoryPort;
import com.template.ms_pedidos.domain.ports.PedidoRepositoryPort;
import com.template.ms_pedidos.domain.ports.ProductoRepositoryPort;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.openapitools.model.EstadoPedido;

@Service
@RequiredArgsConstructor
@Slf4j
public class CrearPedidoUseCase {

    private final PedidoRepositoryPort pedidoRepo;
    private final ProductoRepositoryPort productoRepo;
    private final ClienteRepositoryPort clienteRepo;
    private final KafkaEventPort kafkaEventPort;

    @Value("${topics.order-created}")
    private String pedidoTopic;

    public Mono<Pedido> ejecutar(PedidoRequest request) {

        return clienteRepo.obtenerPorDni(request.getDocumentNumber())

                .switchIfEmpty(Mono.error(
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Cliente no existe con documento: "
                                        + request.getDocumentNumber()
                        )
                ))

                .flatMap(cliente ->

                        Flux.fromIterable(request.getProductos())

                                .flatMap(det ->

                                        productoRepo
                                                .descontarStock(
                                                        det.getProductoId(),
                                                        det.getCantidad()
                                                )

                                                .flatMap(success -> {

                                                    if (!success) {

                                                        return Mono.error(
                                                                new ResponseStatusException(
                                                                        HttpStatus.CONFLICT,
                                                                        "Stock insuficiente para productoId: "
                                                                                + det.getProductoId()
                                                                )
                                                        );
                                                    }

                                                    return productoRepo.findById(
                                                            det.getProductoId()
                                                    );
                                                })

                                                .map(prod -> {

                                                    BigDecimal precioFinal =
                                                            det.getPrecio() != null
                                                                    ? det.getPrecio()
                                                                    : prod.getPrecio();

                                                    BigDecimal subtotal =
                                                            precioFinal.multiply(
                                                                    BigDecimal.valueOf(
                                                                            det.getCantidad()
                                                                    )
                                                            );

                                                    return ProductoDetalle.builder()
                                                            .productoId(prod.getId())
                                                            .nombre(prod.getNombre())
                                                            .precio(precioFinal)
                                                            .cantidad(det.getCantidad())
                                                            .subtotal(subtotal)
                                                            .build();
                                                })
                                )

                                .collectList()

                                .flatMap(detalles -> {

                                    BigDecimal total =
                                            detalles.stream()
                                                    .map(ProductoDetalle::getSubtotal)
                                                    .reduce(
                                                            BigDecimal.ZERO,
                                                            BigDecimal::add
                                                    );

                                    Pedido pedido = Pedido.builder()
                                            .fechaRegistro(LocalDate.now())
                                            .estado(EstadoPedido.PENDIENTE)
                                            .productos(detalles)
                                            .total(total)
                                            .documentNumber(
                                                    request.getDocumentNumber()
                                            )
                                            .client(cliente.getFullName())
                                            .build();

                                    return pedidoRepo.save(pedido);
                                })

                                .flatMap(savedPedido -> {

                                    PedidoCreadoEvent event =
                                            PedidoCreadoEvent.builder()
                                                    .pedidoId(savedPedido.getId())
                                                    .fechaRegistro(
                                                            savedPedido.getFechaRegistro()
                                                    )
                                                    .estado(savedPedido.getEstado())
                                                    .total(savedPedido.getTotal())
                                                    .documentNumber(
                                                            savedPedido.getDocumentNumber()
                                                    )
                                                    .client(savedPedido.getClient())
                                                    .productos(
                                                            savedPedido.getProductos()
                                                    )
                                                    .build();

                                    return kafkaEventPort
                                            .publish(pedidoTopic, event)
                                            .thenReturn(savedPedido);
                                })
                )

                .timeout(Duration.ofSeconds(5))

                .doOnSuccess(pedido ->
                        log.info(
                                "Pedido registrado correctamente: {}",
                                pedido.getId()
                        )
                )

                .doOnError(error ->
                        log.error(
                                "ERROR REAL: {}",
                                error.getMessage()
                        )
                );
    }
}
