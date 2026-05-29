package com.rafael.usuario.infrastructure.business.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SenhaUpdateDTO {

    @NotBlank(message = "Senha atual é obrigatória")
    @Size(min = 6, message = "Senha atual deve ter no mínimo 6 caracteres")
    private String senhaAtual;

    @NotBlank(message = "Nova senha é obrigatória")
    @Size(min = 6, message = "Nova senha deve ter no mínimo 6 caracteres")
    private String senhaNova;
}