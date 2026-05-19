package com.rafael.usuario.infrastructure.business.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponseDTO {

    private String nome;
    private String email;

    private List<EnderecoDTO> enderecos;
    private List<TelefoneDTO> telefones;
}