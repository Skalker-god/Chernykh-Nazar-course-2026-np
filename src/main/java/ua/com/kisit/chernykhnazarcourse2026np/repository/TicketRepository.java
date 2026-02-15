package ua.com.kisit.chernykhnazarcourse2026np.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.com.kisit.chernykhnazarcourse2026np.entity.BusRoute;
import ua.com.kisit.chernykhnazarcourse2026np.entity.Ticket;

import java.time.LocalDate;
import java.util.List;



public interface TicketRepository extends JpaRepository<Ticket, Long> {


    List<Ticket> findByBusRouteAndTravelDateAndStatus(BusRoute busRoute,
                                                      LocalDate travelDate,
                                                      Ticket.TicketStatus status);

    List<Ticket> findByPassengerPhoneAndStatus(String phone, Ticket.TicketStatus status);

    @Query("SELECT t.seatNumber FROM Ticket t WHERE " +
            "t.busRoute = :route AND t.travelDate = :date AND t.status = 'ACTIVE'")
    List<Integer> findOccupiedSeats(@Param("route") BusRoute route,
                                    @Param("date") LocalDate date);
}
