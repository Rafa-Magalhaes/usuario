package com.rafael.usuario.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BffUsuarioSetnameRequestDTO {

    @NotBlank(message = "O nome não pode estar em branco")
    private String nome;
}