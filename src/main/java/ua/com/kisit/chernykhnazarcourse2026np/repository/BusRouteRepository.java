package ua.com.kisit.chernykhnazarcourse2026np.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.com.kisit.chernykhnazarcourse2026np.entity.BusRoute;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;


public interface BusRouteRepository extends JpaRepository<BusRoute, Long> {

    Optional<BusRoute> findByRouteNumber(String routeNumber);

    List<BusRoute> findByIsActiveTrue();

    @Query("SELECT r FROM BusRoute r WHERE " +
            "(r.finalDestination LIKE %:destination% OR r.intermediateStops LIKE %:destination%) " +
            "AND r.availableSeats > 0 AND r.isActive = true " +
            "ORDER BY r.departureTime ASC")
    List<BusRoute> findAvailableRoutesToDestination(@Param("destination") String destination);

    @Query("SELECT r FROM BusRoute r WHERE " +
            "(r.finalDestination LIKE %:destination% OR r.intermediateStops LIKE %:destination%) " +
            "AND r.availableSeats > 0 AND r.isActive = true " +
            "AND r.departureTime >= :currentTime " +
            "ORDER BY r.departureTime ASC")
    List<BusRoute> findNearestRoutesToDestination(@Param("destination") String destination,
                                                  @Param("currentTime") LocalTime currentTime);
}