package com.template.ms_pedidos.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.openapitools.model.EstadoPedido;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
 
import com.template.ms_pedidos.domain.dto.ProductoDetalle;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(collection = "pedidos")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Pedido {

    @Id
    private String id;

    private LocalDate fechaRegistro;

    private EstadoPedido estado;

    private BigDecimal total;

    private String documentNumber;

    private String client;

    private List<ProductoDetalle> productos;
}
