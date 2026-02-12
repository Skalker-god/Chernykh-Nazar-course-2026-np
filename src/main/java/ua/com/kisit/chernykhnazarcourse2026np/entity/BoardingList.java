package ua.com.kisit.chernykhnazarcourse2026np.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "boarding_lists")
public class BoardingList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "route_id", nullable = false)
    private BusRoute busRoute;

    @Column(nullable = false)
    private LocalDate travelDate;

    @OneToMany(mappedBy = "boardingList", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BoardingPassenger> passengers = new ArrayList<>();

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private Boolean isClosed = false; // Закрита відомість
}