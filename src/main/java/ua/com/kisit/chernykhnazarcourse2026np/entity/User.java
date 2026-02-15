package ua.com.kisit.chernykhnazarcourse2026np.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName; // ПІБ

    @Column(nullable = false, unique = true)
    private String phone; // Телефон (використовується як логін)

    @Column(nullable = false)
    private String password; // Пароль (в реальному проекті треба хешувати!)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role = UserRole.PASSENGER; // Роль користувача

    @Column(nullable = false)
    private LocalDateTime createdAt; // Дата реєстрації

    @Column(nullable = false)
    private Boolean isActive = true; // Чи активний акаунт
}