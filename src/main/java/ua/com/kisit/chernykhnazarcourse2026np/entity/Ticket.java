package ua.com.kisit.chernykhnazarcourse2026np.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "tickets")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "route_id", nullable = false)
    private BusRoute busRoute;

    @Column(nullable = false)
    private String passengerName; // ПІБ пасажира

    @Column(nullable = false)
    private String passengerPhone; // Телефон

    @Column(nullable = false)
    private Integer seatNumber; // Номер місця

    @Column(nullable = false)
    private LocalDate travelDate; // Дата поїздки

    @Column(nullable = false)
    private String destination; // Пункт призначення

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketStatus status = TicketStatus.ACTIVE; // Статус квитка

    @Column(nullable = false)
    private LocalDateTime purchaseDateTime; // Час покупки

    @Column(nullable = false)
    private Boolean isAdvancePurchase = false; // Попередній продаж

    public enum TicketStatus {
        ACTIVE,    // Активний
        RETURNED,  // Повернутий
        USED       // Використаний
    }
}