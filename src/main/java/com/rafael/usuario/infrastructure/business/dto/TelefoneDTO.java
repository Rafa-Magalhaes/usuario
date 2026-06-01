package com.rafael.usuario.infrastructure.business.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TelefoneDTO {

    private Long id;

    private String ddd;
    private String numero;
}