package com.rafael.usuario.api.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TelefoneDTO {

    private Long id;

    private String ddd;
    private String numero;
}