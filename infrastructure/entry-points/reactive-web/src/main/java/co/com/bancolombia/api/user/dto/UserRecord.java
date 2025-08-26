package co.com.bancolombia.api.user.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;

public record UserRecord(
         Long id,

         @NotBlank(message = "First name is mandatory")
        String firstName,

    @NotBlank(message = "Last name is mandatory")
    String lastName,

    Timestamp birthDate,
    String address,
    String phone,


    @Email(message = "El email should be valid")
    @NotBlank(message = "Email is mandatory")
    String email,


    @NotNull(message = "Base salary is not null")
    @Min(value = 0, message = "Base salary must be positive")
         @Max(value = 15000000, message = "Base salary must be  less than 15,000,000")
         BigDecimal baseSalary
) {
}
