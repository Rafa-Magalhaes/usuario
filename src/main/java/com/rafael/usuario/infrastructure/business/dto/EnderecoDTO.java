package com.rafael.usuario.infrastructure.business.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EnderecoDTO {

    private Long id;

    private String rua;
    private String numero;
    private String cep;
    private String bairro;
    private String cidade;
    private String estado;
}