package ua.com.kisit.chernykhnazarcourse2026np.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ua.com.kisit.chernykhnazarcourse2026np.entity.BusRoute;
import ua.com.kisit.chernykhnazarcourse2026np.entity.Ticket;
import ua.com.kisit.chernykhnazarcourse2026np.entity.User;
import ua.com.kisit.chernykhnazarcourse2026np.repository.BusRouteRepository;
import ua.com.kisit.chernykhnazarcourse2026np.repository.TicketRepository;
import ua.com.kisit.chernykhnazarcourse2026np.service.TicketService;

import java.time.LocalDateTime;
import java.util.List;

@Controller
public class TicketController {

    private static final Logger log = LoggerFactory.getLogger(TicketController.class);

    @Autowired
    private BusRouteRepository busRouteRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private TicketService ticketService;

    private User getCurrentUser(HttpSession session) {
        return (User) session.getAttribute("user");
    }

    @GetMapping("/ticket/book/{routeId}")
    public ModelAndView bookingForm(@PathVariable Long routeId, HttpSession session) {
        ModelAndView mav = new ModelAndView("ticket-booking");

        BusRoute route = busRouteRepository.findById(routeId).orElse(null);
        User user = getCurrentUser(session);

        if (route == null) {
            return new ModelAndView("redirect:/");
        }

        List<Integer> occupiedSeats = ticketService.getOccupiedSeats(route, route.getDepartureDate());

        mav.addObject("route", route);
        mav.addObject("occupiedSeats", occupiedSeats);
        mav.addObject("travelDate", route.getDepartureDate());
        mav.addObject("user", user);

        return mav;
    }

    @PostMapping("/ticket/confirm")
    public ModelAndView confirmBooking(
            @RequestParam Long routeId,
            @RequestParam String passengerName,
            @RequestParam String passengerPhone,
            @RequestParam Integer seatNumber,
            @RequestParam String destination,
            @RequestParam(defaultValue = "false") Boolean isAdvance,
            HttpSession session,
            HttpServletRequest request) {

        BusRoute route = busRouteRepository.findById(routeId).orElse(null);

        if (route == null || route.getAvailableSeats() <= 0) {
            return new ModelAndView("redirect:/");
        }

        List<Integer> occupiedSeats = ticketService.getOccupiedSeats(route, route.getDepartureDate());

        if (occupiedSeats.contains(seatNumber)) {
            ModelAndView mav = new ModelAndView("ticket-booking");
            mav.addObject("route", route);
            mav.addObject("occupiedSeats", occupiedSeats);
            mav.addObject("travelDate", route.getDepartureDate());
            mav.addObject("error", "Місце №" + seatNumber + " вже зайняте! Оберіть інше.");
            mav.addObject("user", getCurrentUser(session));
            return mav;
        }

        Ticket ticket = new Ticket();
        ticket.setBusRoute(route);
        ticket.setPassengerName(passengerName);
        ticket.setPassengerPhone(passengerPhone);
        ticket.setSeatNumber(seatNumber);
        ticket.setTravelDate(route.getDepartureDate());
        ticket.setDestination(destination);
        ticket.setStatus(Ticket.TicketStatus.ACTIVE);
        ticket.setPurchaseDateTime(LocalDateTime.now());
        ticket.setIsAdvancePurchase(isAdvance);

        ticketRepository.save(ticket);

        route.setAvailableSeats(route.getAvailableSeats() - 1);
        busRouteRepository.save(route);

        log.info("ПОКУПКА (пряма): {} придбав квиток рейс {} місце {} на суму {} грн (IP: {})",
                passengerPhone, route.getRouteNumber(), seatNumber, route.getTicketPrice(), request.getRemoteAddr());

        ModelAndView mav = new ModelAndView("ticket-success");
        mav.addObject("ticket", ticket);
        return mav;
    }
}