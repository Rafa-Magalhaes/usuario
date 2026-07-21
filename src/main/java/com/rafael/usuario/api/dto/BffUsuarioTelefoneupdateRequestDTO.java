package com.rafael.usuario.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BffUsuarioTelefoneupdateRequestDTO {

    @NotBlank(message = "Telefone é obrigatório")
    @Size(max = 9, message = "Telefone deve ter no máximo 9 caracteres")
    private String numero;

    @NotBlank(message = "DDD é obrigatório")
    @Size(max = 3, message = "DDD deve ter no máximo 3 caracteres")
    private String ddd;
}
