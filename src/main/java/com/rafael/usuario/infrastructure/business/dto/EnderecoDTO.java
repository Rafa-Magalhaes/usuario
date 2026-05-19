package com.rafael.usuario.infrastructure.business.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EnderecoDTO {

    private String rua;
    private String numero;
    private String cep;
    private String bairro;
    private String cidade;
    private String estado;
}