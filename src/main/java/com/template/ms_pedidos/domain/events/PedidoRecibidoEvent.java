package com.template.ms_pedidos.domain.events;

import lombok.*;
import org.openapitools.model.EstadoPedido;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PedidoRecibidoEvent {

    private String pedidoId; 
    private EstadoPedido estado;
}
