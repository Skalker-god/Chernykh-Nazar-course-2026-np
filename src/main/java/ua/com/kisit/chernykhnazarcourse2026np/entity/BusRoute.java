package ua.com.kisit.chernykhnazarcourse2026np.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bus_routes")
public class BusRoute implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "Номер рейсу не може бути порожнім")
    @Column(nullable = false, unique = true)
    private String routeNumber;

    @NotEmpty(message = "Місто відправлення не може бути порожнім")
    @Column(nullable = false)
    private String originCity;

    @NotEmpty(message = "Кінцевий пункт не може бути порожнім")
    @Column(nullable = false)
    private String finalDestination;

    @Column(columnDefinition = "TEXT")
    private String intermediateStops;

    @NotNull(message = "Дата відправлення обов'язкова")
    @Column(nullable = false)
    private LocalDate departureDate;

    @NotNull(message = "Час відправлення обов'язковий")
    @Column(nullable = false)
    private LocalTime departureTime;

    @NotNull(message = "Кількість місць обов'язкова")
    @Min(value = 1, message = "Мінімум 1 місце")
    @Column(nullable = false)
    private Integer totalSeats = 45;

    @NotNull
    @Min(value = 0, message = "Кількість вільних місць не може бути від'ємною")
    @Column(nullable = false)
    private Integer availableSeats = 45;

    @NotNull(message = "Ціна квитка обов'язкова")
    @DecimalMin(value = "0.0", inclusive = true, message = "Ціна не може бути від'ємною")
    @Column(nullable = false)
    private Double ticketPrice = 0.0;

    @Column(nullable = false)
    private Boolean isActive = true;
}