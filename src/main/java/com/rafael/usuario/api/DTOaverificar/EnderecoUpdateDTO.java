package com.rafael.usuario.api.DTOaverificar;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EnderecoUpdateDTO {

    @NotBlank(message = "Rua é obrigatória")
    @Size(max = 255, message = "Rua deve ter no máximo 255 caracteres")
    private String rua;

    @NotBlank(message = "Número é obrigatório")
    @Size(max = 20, message = "Número deve ter no máximo 20 caracteres")
    private String numero;

    @NotBlank(message = "Bairro é obrigatório")
    @Size(max = 100, message = "Bairro deve ter no máximo 100 caracteres")
    private String bairro;

    @NotBlank(message = "Cidade é obrigatória")
    @Size(max = 100, message = "Cidade deve ter no máximo 100 caracteres")
    private String cidade;

    @NotBlank(message = "Estado é obrigatório")
    @Size(min = 2, max = 2, message = "Estado deve ter exatamente 2 caracteres")
    private String estado;

    @NotBlank(message = "CEP é obrigatório")
    @Size(max = 9, message = "CEP deve ter no máximo 9 caracteres")
    private String cep;
}