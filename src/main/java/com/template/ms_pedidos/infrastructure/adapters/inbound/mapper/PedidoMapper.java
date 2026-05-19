package com.template.ms_pedidos.infrastructure.adapters.inbound.mapper;

import org.openapitools.model.PedidoResponse;
import org.openapitools.model.ProductoDetalleResponse;
import org.springframework.stereotype.Component;

import com.template.ms_pedidos.infrastructure.adapters.inbound.dto.ProductoDetalle;
import com.template.ms_pedidos.domain.model.Pedido;

@Component
public class PedidoMapper {

    public PedidoResponse toResponse(Pedido pedido) {

        PedidoResponse response = new PedidoResponse()
                .id(pedido.getId())
                .estado(pedido.getEstado())
                .total(pedido.getTotal())
                .documentNumber(pedido.getDocumentNumber())
                .client(pedido.getClient());
                
                
        if (pedido.getFechaRegistro() != null) {
            response.setFechaRegistro(
                pedido.getFechaRegistro()
            );
        }

        response.setProductos(
                pedido.getProductos().stream()
                        .map(this::toDetalleResponse)
                        .toList()
        );

        return response;
    }

    private ProductoDetalleResponse toDetalleResponse(ProductoDetalle d) {

        return new ProductoDetalleResponse()
                .productoId(d.getProductoId())
                .nombre(d.getNombre())
                .precio(d.getPrecio())
                .cantidad(d.getCantidad())
                .subtotal(d.getSubtotal());
    }
}
