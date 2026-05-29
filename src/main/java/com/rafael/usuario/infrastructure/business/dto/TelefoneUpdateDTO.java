package com.rafael.usuario.infrastructure.business.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TelefoneUpdateDTO {

    @NotBlank(message = "DDD é obrigatório")
    @Size(min = 2, max = 3, message = "DDD deve ter entre 2 e 3 caracteres")
    private String ddd;

    @NotBlank(message = "Número é obrigatório")
    @Size(min = 8, max = 9, message = "Número deve ter entre 8 e 9 caracteres")
    private String numero;
}