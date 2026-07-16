package com.rafael.usuario.domain.entity;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table (name = "enderecos")

public class Endereco {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (name = "rua", length = 100, nullable = false)
    private String rua;

    @Column (name = "numero", length = 5)
    private String numero;

    @Column (name = "CEP", length = 9)
    private String cep;

    @Column (name = "bairro", length = 100)
    private String bairro;

    @Column (name = "cidade", length = 100)
    private String cidade;

    @Column (name = "estado", length = 2)
    private String estado;

    // Relacionamento com o Usuário (lado "muitos")
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
}
