package br.gov.biblioteca_ag_backend.model;

import br.gov.biblioteca_ag_backend.enums.TipoUsuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity()
@Table(name = "tb_usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    private String telefone;

    @Column(unique = true)
    private String email;

    @Column(nullable = false)
    private String senha_hash;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoUsuario tipoUsuario;
}
