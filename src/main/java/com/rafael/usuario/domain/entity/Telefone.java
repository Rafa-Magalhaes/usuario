package com.rafael.usuario.domain.entity;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EqualsAndHashCode(of = "id")
@ToString(exclude = "usuario")
@Table(name = "telefones")

public class Telefone {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (name = "numero", length = 9)
    private String numero;

    @Column (name = "DDD", length = 3)
    private String ddd;

    // Relacionamento com o Usuário (lado "muitos")
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
}
