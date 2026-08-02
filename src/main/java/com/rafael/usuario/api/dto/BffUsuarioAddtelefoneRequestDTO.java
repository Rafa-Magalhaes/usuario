package com.rafael.usuario.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BffUsuarioAddtelefoneRequestDTO {

    @NotBlank(message = "O campo número é obrigatório")
    @Size(min = 8, max = 9)
    private String numero;

    @NotBlank (message = "O campo DDD é obrigatório")
    @Size(min = 3, max = 3)
    private String ddd;
}
