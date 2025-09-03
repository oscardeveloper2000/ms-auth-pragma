package co.com.bancolombia.r2dbc.entity;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table("users")
public class UserEntity {
    @Id
    @Column("user_id")
    private Long id;

    @NotBlank(message = "Document number is mandatory")
    @Column("document_number")
    private String documentNumber;

    @NotBlank(message = "First name is mandatory")
    private String firstName;

    @NotBlank(message = "Last name is mandatory")
    private String lastName;

    private Timestamp birthDate;
    private String address;
    private String phone;


    @Email(message = "El email should be valid")
    @NotBlank(message = "Email is mandatory")
    private String email;


    @NotNull(message = "Base salary is not null")
    private BigDecimal baseSalary;

    @NotBlank(message = "Password is mandatory")
    private String passwordHash;

    @NotNull(message = "Role is not null")
    @Column("role_id")
    private Long roleId;

}
