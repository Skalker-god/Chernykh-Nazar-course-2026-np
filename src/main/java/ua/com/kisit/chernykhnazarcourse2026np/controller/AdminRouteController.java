package ua.com.kisit.chernykhnazarcourse2026np.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ua.com.kisit.chernykhnazarcourse2026np.entity.User;
import ua.com.kisit.chernykhnazarcourse2026np.service.RouteService;

import java.time.LocalDate;
import java.time.LocalTime;

@Controller
@RequestMapping("/admin/routes")
public class AdminRouteController {

    private static final Logger log = LoggerFactory.getLogger(AdminRouteController.class);

    private final RouteService routeService;

    @Autowired
    public AdminRouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    // Сторінка управління рейсами — показує всі рейси
    @GetMapping
    public ModelAndView routesPage() {
        ModelAndView mav = new ModelAndView("admin/routes");
        mav.addObject("routes", routeService.getAllRoutes());
        return mav;
    }

    @PostMapping("/add")
    public String addRoute(@RequestParam String routeNumber,
                           @RequestParam String originCity,
                           @RequestParam String finalDestination,
                           @RequestParam(required = false) String intermediateStops,
                           @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate departureDate,
                           @RequestParam String departureTime,
                           @RequestParam Integer totalSeats,
                           @RequestParam Double ticketPrice,
                           HttpSession session,
                           HttpServletRequest request) {

        routeService.addRoute(routeNumber, originCity, finalDestination,
                intermediateStops, departureDate,
                LocalTime.parse(departureTime), totalSeats, ticketPrice);

        // null-safe: якщо currentUser чомусь null — пишемо "unknown"
        User admin = (User) session.getAttribute("user");
        String adminPhone = admin != null ? admin.getPhone() : "unknown";

        log.info("АДМІН: {} додав новий рейс {} {}→{} на {} (IP: {})",
                adminPhone, routeNumber, originCity, finalDestination,
                departureDate, request.getRemoteAddr());

        return "redirect:/admin/routes";
    }

    @PostMapping("/toggle/{id}")
    public String toggleRoute(@PathVariable Long id,
                              HttpSession session,
                              HttpServletRequest request) {
        routeService.toggleRoute(id);

        User admin = (User) session.getAttribute("user");
        String adminPhone = admin != null ? admin.getPhone() : "unknown";

        log.info("АДМІН: {} змінив статус рейсу ID {} (IP: {})",
                adminPhone, id, request.getRemoteAddr());

        return "redirect:/admin/routes";
    }

    @PostMapping("/delete/{id}")
    public String deleteRoute(@PathVariable Long id,
                              HttpSession session,
                              HttpServletRequest request) {
        routeService.deleteRoute(id);

        User admin = (User) session.getAttribute("user");
        String adminPhone = admin != null ? admin.getPhone() : "unknown";

        log.warn("АДМІН: {} ВИДАЛИВ рейс ID {} (IP: {})",
                adminPhone, id, request.getRemoteAddr());

        return "redirect:/admin/routes";
    }
}