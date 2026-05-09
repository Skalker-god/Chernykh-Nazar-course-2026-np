package ua.com.kisit.chernykhnazarcourse2026np.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tickets")
public class Ticket implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Рейс на який придбано квиток
    @NotNull(message = "Рейс обов'язковий")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "route_id", nullable = false)
    private BusRoute busRoute;

    // ПІБ пасажира
    @NotEmpty(message = "ПІБ пасажира не може бути порожнім")
    @Size(min = 2, max = 100, message = "ПІБ: від 2 до 100 символів")
    @Column(nullable = false)
    private String passengerName;

    // Телефон пасажира — використовується для пошуку квитків
    @NotEmpty(message = "Телефон пасажира не може бути порожнім")
    @Pattern(regexp = "\\+380[0-9]{9}", message = "Формат телефону: +380XXXXXXXXX")
    @Column(nullable = false)
    private String passengerPhone;

    // Номер місця: від 1 до максимальної кількості місць у рейсі
    @NotNull(message = "Номер місця обов'язковий")
    @Min(value = 1, message = "Номер місця від 1")
    @Column(nullable = false)
    private Integer seatNumber;

    // Дата поїздки (береться з рейсу)
    @NotNull(message = "Дата поїздки обов'язкова")
    @Column(nullable = false)
    private LocalDate travelDate;

    // Пункт призначення пасажира (може бути проміжна зупинка)
    @NotEmpty(message = "Пункт призначення не може бути порожнім")
    @Column(nullable = false)
    private String destination;

    // Статус квитка: ACTIVE → RETURNED або USED
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketStatus status = TicketStatus.ACTIVE;

    // Час і дата купівлі квитка
    @NotNull
    @Column(nullable = false)
    private LocalDateTime purchaseDateTime;

    // true = попередній продаж (куплено заздалегідь, не в день рейсу)
    @Column(nullable = false)
    private Boolean isAdvancePurchase = false;

    public enum TicketStatus {
        ACTIVE,   // Активний — пасажир ще не їхав
        RETURNED, // Повернутий — скасований до відправлення
        USED      // Використаний — пасажир здійснив поїздку
    }
}