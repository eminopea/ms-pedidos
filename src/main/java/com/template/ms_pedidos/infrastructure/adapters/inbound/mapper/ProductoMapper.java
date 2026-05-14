package com.template.ms_pedidos.infrastructure.adapters.inbound.mapper;

import org.springframework.stereotype.Component;

import com.template.ms_pedidos.domain.model.Producto;
import org.openapitools.model.ProductoResponse;

@Component
public class ProductoMapper {

    public ProductoResponse toResponse(Producto producto) {

        return new ProductoResponse()
                .id(producto.getId())
                .nombre(producto.getNombre())
                .precio(producto.getPrecio())
                .stock(producto.getStock());
    }

}