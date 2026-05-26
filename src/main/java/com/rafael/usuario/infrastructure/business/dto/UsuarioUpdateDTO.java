package infrastructure.business.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioUpdateDTO {

    @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
    private String nome;

    @Email(message = "Email inválido")
    private String email;

    @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
    private String senha;   // opcional - só altera se for enviado
}