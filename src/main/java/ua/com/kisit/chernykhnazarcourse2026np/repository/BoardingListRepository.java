package ua.com.kisit.chernykhnazarcourse2026np.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.com.kisit.chernykhnazarcourse2026np.entity.BoardingList;
import ua.com.kisit.chernykhnazarcourse2026np.entity.BusRoute;

import java.time.LocalDate;
import java.util.Optional;

public interface BoardingListRepository extends JpaRepository<BoardingList, Long> {

    Optional<BoardingList> findByBusRouteAndTravelDate(BusRoute busRoute, LocalDate travelDate);

    Optional<BoardingList> findByBusRouteAndTravelDateAndIsClosedFalse(BusRoute busRoute,
                                                                       LocalDate travelDate);
}