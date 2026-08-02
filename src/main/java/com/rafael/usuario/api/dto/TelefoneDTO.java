package com.rafael.usuario.api.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TelefoneDTO {

    private Long id;

    @Size(max = 3, message = "O DDD não pode ter mais que 3 números")
    @Pattern(regexp = "^\\d+$", message = "Informar somente números no DDD")
    private String ddd;

    @Size(max = 8, message = "O número não pode ter mais que 8 números")
    @Pattern(regexp = "^\\d+$", message = "Informar somente números no telefone")
    private String numero;
}