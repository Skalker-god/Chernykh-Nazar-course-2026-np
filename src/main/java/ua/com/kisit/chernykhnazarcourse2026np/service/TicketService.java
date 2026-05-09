package ua.com.kisit.chernykhnazarcourse2026np.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import ua.com.kisit.chernykhnazarcourse2026np.entity.BoardingPassenger;
import ua.com.kisit.chernykhnazarcourse2026np.entity.BusRoute;
import ua.com.kisit.chernykhnazarcourse2026np.entity.Ticket;
import ua.com.kisit.chernykhnazarcourse2026np.repository.BoardingPassengerRepository;
import ua.com.kisit.chernykhnazarcourse2026np.repository.BusRouteRepository;
import ua.com.kisit.chernykhnazarcourse2026np.repository.TicketRepository;

import java.time.LocalDate;
import java.util.List;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final BusRouteRepository busRouteRepository;
    private final BoardingPassengerRepository boardingPassengerRepository;

    @Autowired
    public TicketService(TicketRepository ticketRepository,
                         BusRouteRepository busRouteRepository,
                         BoardingPassengerRepository boardingPassengerRepository) {
        this.ticketRepository = ticketRepository;
        this.busRouteRepository = busRouteRepository;
        this.boardingPassengerRepository = boardingPassengerRepository;
    }

    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    public List<Ticket> getTicketsByPhone(String phone) {
        return ticketRepository.findByPassengerPhoneAndStatus(
                phone,
                Ticket.TicketStatus.ACTIVE
        );
    }

    // Кешування зайнятих місць з урахуванням дати рейсу
    @Cacheable(value = "occupiedSeats", key = "'route_' + #route.id + '_date_' + #date")
    public List<Integer> getOccupiedSeats(BusRoute route, LocalDate date) {
        return ticketRepository.findOccupiedSeats(route, date);
    }

    @CacheEvict(value = "occupiedSeats", allEntries = true)
    public void cancelTicket(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId).orElse(null);
        if (ticket != null && ticket.getStatus() == Ticket.TicketStatus.ACTIVE) {
            ticket.setStatus(Ticket.TicketStatus.RETURNED);
            ticketRepository.save(ticket);

            boardingPassengerRepository.findByTicket(ticket).ifPresent(boardingPassengerRepository::delete);

            BusRoute route = ticket.getBusRoute();
            route.setAvailableSeats(route.getAvailableSeats() + 1);
            busRouteRepository.save(route);
        }
    }
}