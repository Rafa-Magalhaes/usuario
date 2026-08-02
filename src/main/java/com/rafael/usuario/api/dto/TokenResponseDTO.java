package com.rafael.usuario.api.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TokenResponseDTO {
    private String token;
}