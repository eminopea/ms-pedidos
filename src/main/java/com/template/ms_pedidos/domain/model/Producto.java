package com.template.ms_pedidos.domain.model;

import java.math.BigDecimal;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(collection = "productos")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Producto {
    @Id
    private String id;
    private String nombre;
    private BigDecimal precio;
    private Integer stock;
}