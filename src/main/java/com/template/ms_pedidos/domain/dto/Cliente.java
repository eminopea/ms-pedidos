package com.template.ms_pedidos.domain.dto;
 

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Cliente {

    private String customerType;

    private String fullName;

    private String email;

    private Integer age;

    private String documentType;

    private String identificationNumber;

    private Integer bonusMiles;
}
