package com.rafael.usuario.api.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioBffMailResponseDTO {

    private String email;
    private String nome;
}
