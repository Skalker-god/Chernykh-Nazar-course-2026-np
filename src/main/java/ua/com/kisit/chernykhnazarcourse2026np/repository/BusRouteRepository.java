package ua.com.kisit.chernykhnazarcourse2026np.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.com.kisit.chernykhnazarcourse2026np.entity.BusRoute;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BusRouteRepository extends JpaRepository<BusRoute, Long> {

    Optional<BusRoute> findByRouteNumber(String routeNumber);

    List<BusRoute> findByIsActiveTrue();

    // Пошук за датою (нова функція)
    List<BusRoute> findByDepartureDateAndIsActiveTrue(LocalDate departureDate);

    // Пошук за містом призначення
    @Query("SELECT r FROM BusRoute r WHERE " +
            "(LOWER(r.finalDestination) LIKE LOWER(CONCAT('%', :destination, '%')) " +
            "OR LOWER(r.intermediateStops) LIKE LOWER(CONCAT('%', :destination, '%'))) " +
            "AND r.availableSeats > 0 AND r.isActive = true " +
            "ORDER BY r.departureDate ASC, r.departureTime ASC")
    List<BusRoute> findAvailableRoutesToDestination(@Param("destination") String destination);

    // Пошук за місто відправлення + місто призначення + дата
    @Query("SELECT r FROM BusRoute r WHERE " +
            "LOWER(r.originCity) LIKE LOWER(CONCAT('%', :origin, '%')) " +
            "AND (LOWER(r.finalDestination) LIKE LOWER(CONCAT('%', :destination, '%')) " +
            "     OR LOWER(r.intermediateStops) LIKE LOWER(CONCAT('%', :destination, '%'))) " +
            "AND r.departureDate = :date " +
            "AND r.isActive = true " +
            "ORDER BY r.departureTime ASC")
    List<BusRoute> searchRoutes(@Param("origin") String origin,
                                @Param("destination") String destination,
                                @Param("date") LocalDate date);

    // Пошук за містом відправлення + призначення (без дати)
    @Query("SELECT r FROM BusRoute r WHERE " +
            "LOWER(r.originCity) LIKE LOWER(CONCAT('%', :origin, '%')) " +
            "AND (LOWER(r.finalDestination) LIKE LOWER(CONCAT('%', :destination, '%')) " +
            "     OR LOWER(r.intermediateStops) LIKE LOWER(CONCAT('%', :destination, '%'))) " +
            "AND r.isActive = true " +
            "ORDER BY r.departureDate ASC, r.departureTime ASC")
    List<BusRoute> searchRoutesByOriginAndDestination(@Param("origin") String origin,
                                                      @Param("destination") String destination);

    // Всі унікальні міста відправлення (для автодоповнення)
    @Query("SELECT DISTINCT r.originCity FROM BusRoute r WHERE r.isActive = true ORDER BY r.originCity")
    List<String> findAllOriginCities();

    // Всі унікальні міста призначення (для автодоповнення)
    @Query("SELECT DISTINCT r.finalDestination FROM BusRoute r WHERE r.isActive = true ORDER BY r.finalDestination")
    List<String> findAllDestinationCities();

    // Рейси не раніше сьогодні (не показувати минулі)
    @Query("SELECT r FROM BusRoute r WHERE r.departureDate >= :today AND r.isActive = true " +
            "ORDER BY r.departureDate ASC, r.departureTime ASC")
    List<BusRoute> findUpcomingRoutes(@Param("today") LocalDate today);
}