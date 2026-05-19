package com.template.ms_pedidos.domain.events;

import com.template.ms_pedidos.infrastructure.adapters.inbound.dto.ProductoDetalle;
import lombok.*;
import org.openapitools.model.EstadoPedido;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PedidoCreadoEvent {

    private String pedidoId;

    private LocalDate fechaRegistro;

    private EstadoPedido estado;

    private BigDecimal total;

    private String documentNumber;

    private String client;

    private List<ProductoDetalle> productos;
}