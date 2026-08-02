package com.rafael.usuario.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BffUsuarioAddenderecoRequestDTO {

    @NotBlank(message = "Rua é obrigatória")
    @Size(max = 100, message = "Rua deve ter no máximo 100 caracteres")
    private String rua;

    @Size(max = 10, message = "Número deve ter no máximo 10 caracteres")
    private String numero;

    @NotBlank(message = "Bairro é obrigatório")
    @Size(max = 100, message = "Bairro deve ter no máximo 100 caracteres")
    private String bairro;

    @NotBlank(message = "Cidade é obrigatória")
    @Size(max = 100, message = "Cidade deve ter no máximo 100 caracteres")
    private String cidade;

    @NotBlank(message = "Estado é obrigatório")
    @Size(min = 2, max = 2, message = "O estado deve conter exatamente 2 caracteres (Sigla da UF, ex: SP)")
    private String estado;

    @NotBlank(message = "O CEP é obrigatório")
    @Size(max = 8, message = "O CEP não pode ter mais que 8 números")
    @Pattern(regexp = "^\\d+$", message = "Informar somente números no CEP")
    private String cep;
}