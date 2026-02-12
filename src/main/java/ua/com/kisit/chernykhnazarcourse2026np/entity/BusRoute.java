package ua.com.kisit.chernykhnazarcourse2026np.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "bus_routes")
public class BusRoute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String routeNumber; // Номер рейсу

    @Column(nullable = false)
    private String finalDestination; // Кінцевий пункт

    @Column(columnDefinition = "TEXT")
    private String intermediateStops; // Проміжні пункти (через кому)

    @Column(nullable = false)
    private LocalTime departureTime; // Час відправлення

    @Column(nullable = false)
    private Integer totalSeats = 45; // Загальна кількість місць

    @Column(nullable = false)
    private Integer availableSeats = 45; // Вільні місця

    @Column(nullable = false)
    private Double ticketPrice = 0.0; // Ціна квитка

    @Column(nullable = false)
    private Boolean isActive = true; // Активний рейс
}