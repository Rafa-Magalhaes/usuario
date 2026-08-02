package com.rafael.usuario.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EnderecoDTO {

    private Long id;

    @NotBlank(message = "A rua é obrigatória")
    private String rua;

    private String numero;

    @NotBlank(message = "O CEP é obrigatório")
    private String cep;

    @NotBlank(message = "O bairro é obrigatório")
    private String bairro;

    @NotBlank(message = "A cidade é obrigatória")
    private String cidade;

    @NotBlank(message = "O estado é obrigatório")
    @Size(min = 2, max = 2, message = "O estado deve conter exatamente 2 caracteres (Sigla da UF, ex: SP)")
    private String estado;
}