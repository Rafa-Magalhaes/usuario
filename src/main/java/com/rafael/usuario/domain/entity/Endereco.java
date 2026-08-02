package com.rafael.usuario.domain.entity;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EqualsAndHashCode(of = "id")
@ToString(exclude = "usuario")
@Table(name = "enderecos")

public class Endereco {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Rua é obrigatória")
    @Column (name = "rua", length = 100, nullable = false)
    private String rua;

    @Column (name = "numero", length = 10)
    private String numero;

    @NotBlank(message = "O CEP é obrigatório")
    @Pattern(regexp = "^\\d+$", message = "Informar somente números no CEP")
    @Column (name = "CEP", length = 8)
    private String cep;

    @NotBlank(message = "Bairro é obrigatório")
    @Column (name = "bairro", length = 100)
    private String bairro;

    @NotBlank(message = "Cidade é obrigatória")
    @Column (name = "cidade", length = 100)
    private String cidade;

    @NotBlank(message = "Estado é obrigatório")
    @Column (name = "estado", length = 2)
    private String estado;

    // Relacionamento com o Usuário (lado "muitos")
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
}
