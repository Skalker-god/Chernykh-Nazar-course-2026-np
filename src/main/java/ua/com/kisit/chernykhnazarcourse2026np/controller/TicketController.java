package ua.com.kisit.chernykhnazarcourse2026np.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import ua.com.kisit.chernykhnazarcourse2026np.entity.User;
import ua.com.kisit.chernykhnazarcourse2026np.entity.BusRoute;
import ua.com.kisit.chernykhnazarcourse2026np.entity.Ticket;
import ua.com.kisit.chernykhnazarcourse2026np.repository.BusRouteRepository;
import ua.com.kisit.chernykhnazarcourse2026np.repository.TicketRepository;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Controller
public class TicketController {

    @Autowired
    private BusRouteRepository busRouteRepository;

    @Autowired
    private TicketRepository ticketRepository;

    private User getCurrentUser(HttpSession session) {
        return (User) session.getAttribute("user");
    }

    @GetMapping("/ticket/book/{routeId}")
    public ModelAndView bookingForm(@PathVariable Long routeId, HttpSession session) {
        ModelAndView modelAndView = new ModelAndView("ticket-booking");

        BusRoute route = busRouteRepository.findById(routeId).orElse(null);
        User user = getCurrentUser(session);

        if (route != null) {
            LocalDate today = LocalDate.now();
            List<Integer> occupiedSeats = ticketRepository.findOccupiedSeats(route, today);

            modelAndView.addObject("route", route);
            modelAndView.addObject("occupiedSeats", occupiedSeats);
            modelAndView.addObject("travelDate", today);
            modelAndView.addObject("user", user); // Додаємо користувача
        } else {
            return new ModelAndView("redirect:/");
        }

        return modelAndView;
    }

    @PostMapping("/ticket/confirm")
    public ModelAndView confirmBooking(
            @RequestParam Long routeId,
            @RequestParam String passengerName,
            @RequestParam String passengerPhone,
            @RequestParam Integer seatNumber,
            @RequestParam String destination,
            @RequestParam(defaultValue = "false") Boolean isAdvance,
            HttpSession session) {

        BusRoute route = busRouteRepository.findById(routeId).orElse(null);

        if (route == null || route.getAvailableSeats() <= 0) {
            return new ModelAndView("redirect:/");
        }

        // ⚠️ ВАЖЛИВА ПЕРЕВІРКА: Чи не зайняте вже це місце?
        LocalDate today = LocalDate.now();
        List<Integer> occupiedSeats = ticketRepository.findOccupiedSeats(route, today);

        if (occupiedSeats.contains(seatNumber)) {
            // Місце вже зайняте - повертаємо на сторінку бронювання з помилкою
            ModelAndView modelAndView = new ModelAndView("ticket-booking");
            modelAndView.addObject("route", route);
            modelAndView.addObject("occupiedSeats", occupiedSeats);
            modelAndView.addObject("travelDate", today);
            modelAndView.addObject("error", "Місце №" + seatNumber + " вже зайняте! Оберіть інше.");

            User user = (User) session.getAttribute("user");
            modelAndView.addObject("user", user);

            return modelAndView;
        }

        // Створюємо квиток
        Ticket ticket = new Ticket();
        ticket.setBusRoute(route);
        ticket.setPassengerName(passengerName);
        ticket.setPassengerPhone(passengerPhone);
        ticket.setSeatNumber(seatNumber);
        ticket.setTravelDate(LocalDate.now());
        ticket.setDestination(destination);
        ticket.setStatus(Ticket.TicketStatus.ACTIVE);
        ticket.setPurchaseDateTime(LocalDateTime.now());
        ticket.setIsAdvancePurchase(isAdvance);

        ticketRepository.save(ticket);

        // Зменшуємо кількість вільних місць
        route.setAvailableSeats(route.getAvailableSeats() - 1);
        busRouteRepository.save(route);

        ModelAndView modelAndView = new ModelAndView("ticket-success");
        modelAndView.addObject("ticket", ticket);

        return modelAndView;
    }
}