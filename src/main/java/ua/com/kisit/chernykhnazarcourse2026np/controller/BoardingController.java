package ua.com.kisit.chernykhnazarcourse2026np.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ua.com.kisit.chernykhnazarcourse2026np.entity.*;
import ua.com.kisit.chernykhnazarcourse2026np.repository.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class BoardingController {

    private static final Logger log = LoggerFactory.getLogger(BoardingController.class);

    @Autowired
    private BusRouteRepository busRouteRepository;

    @Autowired
    private BoardingListRepository boardingListRepository;

    @Autowired
    private BoardingPassengerRepository boardingPassengerRepository;

    @Autowired
    private TicketRepository ticketRepository;

    // Список активних рейсів для вибору відомості
    // Доступ захищений у Security.java: .hasAnyRole("CASHIER", "ADMIN")
    @GetMapping("/boarding")
    public ModelAndView boardingListsPage() {
        ModelAndView mav = new ModelAndView("boarding-list");
        mav.addObject("routes", busRouteRepository.findByIsActiveTrue());
        return mav;
    }

    // Перегляд посадкової відомості конкретного рейсу
    // Якщо відомість ще не існує — створюємо її автоматично
    @GetMapping("/boarding/view/{routeId}")
    @Transactional
    public ModelAndView viewBoardingList(@PathVariable Long routeId, HttpSession session) {
        BusRoute route = busRouteRepository.findById(routeId).orElse(null);
        if (route == null) {
            return new ModelAndView("redirect:/boarding");
        }

        LocalDate travelDate = route.getDepartureDate();

        // Шукаємо відомість, або створюємо нову
        BoardingList boardingList = boardingListRepository
                .findByBusRouteAndTravelDate(route, travelDate)
                .orElseGet(() -> {
                    BoardingList bl = new BoardingList();
                    bl.setBusRoute(route);
                    bl.setTravelDate(travelDate);
                    bl.setCreatedAt(LocalDateTime.now());
                    bl.setIsClosed(false);
                    return boardingListRepository.save(bl);
                });

        // Синхронізуємо квитки з відомістю: додаємо нових пасажирів
        List<Ticket> activeTickets = ticketRepository
                .findByBusRouteAndStatus(route, Ticket.TicketStatus.ACTIVE);
        Set<Long> existingTicketIds = boardingPassengerRepository
                .findByBoardingListId(boardingList.getId())
                .stream()
                .map(bp -> bp.getTicket().getId())
                .collect(Collectors.toSet());

        for (Ticket ticket : activeTickets) {
            if (!existingTicketIds.contains(ticket.getId())) {
                BoardingPassenger bp = new BoardingPassenger();
                bp.setBoardingList(boardingList);
                bp.setTicket(ticket);
                bp.setHasBoarded(false);
                boardingPassengerRepository.save(bp);
            }
        }

        ModelAndView mav = new ModelAndView("boarding-list");
        mav.addObject("route", route);
        mav.addObject("boardingList", boardingList);
        mav.addObject("travelDate", travelDate);
        mav.addObject("routes", busRouteRepository.findByIsActiveTrue());
        return mav;
    }

    // Відмітка посадки / скасування посадки пасажира
    @PostMapping("/boarding/board/{passengerId}")
    @Transactional
    public String markAsBoarded(@PathVariable Long passengerId,
                                HttpSession session,
                                HttpServletRequest request) {
        User user = (User) session.getAttribute("user");
        BoardingPassenger passenger = boardingPassengerRepository.findById(passengerId).orElse(null);

        if (passenger != null) {
            // Перемикаємо статус посадки
            boolean newStatus = !passenger.getHasBoarded();
            passenger.setHasBoarded(newStatus);
            boardingPassengerRepository.save(passenger);

            log.info("ПОСАДКА: {} ({}) відмітив посадку пасажира квитка ID {} (статус: {})",
                    user != null ? user.getFullName() : "unknown",
                    user != null ? user.getRole() : "unknown",
                    passenger.getTicket().getId(),
                    newStatus ? "Посаджено" : "Скасовано посадку");

            Long routeId = passenger.getBoardingList().getBusRoute().getId();
            return "redirect:/boarding/view/" + routeId;
        }
        return "redirect:/boarding";
    }
}