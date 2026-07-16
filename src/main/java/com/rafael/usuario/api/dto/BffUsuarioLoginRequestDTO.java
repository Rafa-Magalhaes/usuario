package com.rafael.usuario.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BffUsuarioLoginRequestDTO {

    private String email;
    private String senha;
}
