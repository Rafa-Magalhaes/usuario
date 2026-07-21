package com.rafael.usuario.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FrontUsuarioCadastroRequestDTO {

    private String nome;
    @Valid
    private String email;
    @NotBlank
    private String senha;

    private List<EnderecoDTO> enderecos;
    private List<TelefoneDTO> telefones;
}