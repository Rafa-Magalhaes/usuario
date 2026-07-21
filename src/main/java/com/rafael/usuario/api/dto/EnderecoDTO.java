package com.rafael.usuario.api.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EnderecoDTO {

    private Long id;

    private String rua;
    private String numero;
    private String cep;
    private String bairro;
    private String cidade;
    private String estado;
}