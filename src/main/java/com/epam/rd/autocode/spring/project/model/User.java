package com.epam.rd.autocode.spring.project.model;

import com.epam.rd.autocode.spring.project.model.generaEntities.AuditableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.epam.rd.autocode.spring.project.model.enums.UserRole;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_users_email", columnNames = "email")
        },
        indexes = {
                @Index(name = "idx_users_email", columnList = "email")
        }
)
public class User extends AuditableEntity {

    @Column(nullable = false, length = 100)
    @NotBlank(message = "Username cannot be null or empty")
    private String username;

    @Column(nullable = false, length = 150)
    @NotBlank(message = "Email cannot be null or empty")
    @Email(message = "Invalid email format")
    private String email;

    @Column(nullable = false)
    @NotBlank(message = "Password cannot be null or empty")
    @Size(min = 8, message = "Password must contain 8 characters or more")
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private UserRole role = UserRole.CUSTOMER;
}
