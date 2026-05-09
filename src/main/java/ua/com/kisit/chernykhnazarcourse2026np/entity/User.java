package ua.com.kisit.chernykhnazarcourse2026np.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Повне ім'я: обов'язкове, мінімум 2 символи
    @NotEmpty(message = "ПІБ не може бути порожнім")
    @Size(min = 2, max = 100, message = "ПІБ: від 2 до 100 символів")
    @Column(nullable = false)
    private String fullName;

    // Телефон використовується як логін, має бути унікальним
    @NotEmpty(message = "Телефон не може бути порожнім")
    @Pattern(regexp = "\\+380[0-9]{9}", message = "Формат телефону: +380XXXXXXXXX")
    @Column(nullable = false, unique = true)
    private String phone;

    // Пароль зберігається у зашифрованому вигляді (BCrypt)
    @NotEmpty(message = "Пароль не може бути порожнім")
    @Size(min = 6, message = "Пароль: мінімум 6 символів")
    @Column(nullable = false)
    private String password;

    // Роль визначає доступні функції системи
    @NotNull(message = "Роль не може бути порожньою")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role = UserRole.PASSENGER;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    // false = заблокований адміністратором
    @Column(nullable = false)
    private Boolean isActive = true;
}