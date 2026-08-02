package com.rafael.usuario.api.dto;

import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioBffPerfilResponseDTO {

    private Long usuarioId;

    private String nome;
    private String email;

    private List<EnderecoDTO> enderecos;
    private List<TelefoneDTO> telefones;
}
