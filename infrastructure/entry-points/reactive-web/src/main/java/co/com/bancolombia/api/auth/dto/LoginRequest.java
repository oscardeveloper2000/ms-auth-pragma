package co.com.bancolombia.api.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;


@Builder(toBuilder = true)
public record LoginRequest (

  @NotBlank(message = "El correo electrónico es obligatorio")
  @Size(max = 80, message = "El correo electrónico debe tener menos de 80 caracteres")
  @Email(message = "El correo electrónico debe tener un formato válido")
  String email,

  @NotBlank(message = "La contraseña es obligatoria")
  @Size(max = 100, message = "La contraseña debe tener menos de 100 caracteres")
   String password
) {
}
