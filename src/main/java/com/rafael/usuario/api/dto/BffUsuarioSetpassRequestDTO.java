package com.rafael.usuario.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BffUsuarioSetpassRequestDTO {

    @NotBlank(message = "Senha atual é obrigatória")
    @Size(min = 6, message = "Senha atual deve ter no mínimo 6 caracteres")
    private String senha;

    @NotBlank(message = "Nova senha é obrigatória")
    @Size(min = 6, message = "Nova senha deve ter no mínimo 6 caracteres")
    private String novaSenha;
}
