package project.authservice.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest
{
    @NotBlank( message = "Email é obrigatório" )
    @Email( message = "Email deve ser válido" )
    private String email;

    @NotBlank( message = "Senha é obrigatória" )
    @Size( min = 8, max = 20, message = "Senha deve ter entre 8 e 20 caracteres" )
    private String password;

    @NotBlank( message = "Confirmação de senha é obrigatória" )
    private String confirmPassword;
}