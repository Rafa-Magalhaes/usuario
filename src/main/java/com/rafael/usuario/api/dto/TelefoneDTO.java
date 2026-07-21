package com.rafael.usuario.api.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TelefoneDTO {

    private Long id;

    private String ddd;
    private String numero;
}