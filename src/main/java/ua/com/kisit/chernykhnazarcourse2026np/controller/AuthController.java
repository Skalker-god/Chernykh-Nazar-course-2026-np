package ua.com.kisit.chernykhnazarcourse2026np.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import ua.com.kisit.chernykhnazarcourse2026np.entity.BusRoute;
import ua.com.kisit.chernykhnazarcourse2026np.entity.User;
import ua.com.kisit.chernykhnazarcourse2026np.entity.UserRole;
import ua.com.kisit.chernykhnazarcourse2026np.repository.BusRouteRepository;
import ua.com.kisit.chernykhnazarcourse2026np.repository.TicketRepository;
import ua.com.kisit.chernykhnazarcourse2026np.repository.UserRepository;
import java.time.format.DateTimeFormatter;
import java.util.List;
import ua.com.kisit.chernykhnazarcourse2026np.entity.Ticket;
import java.time.LocalDateTime;

@Controller
public class AuthController {

    // ВИПРАВЛЕННЯ 3: константа замість дублювання рядка "redirect:/" 5 разів
    private static final String REDIRECT_HOME = "redirect:/";

    // ВИПРАВЛЕННЯ 2: constructor injection замість @Autowired field injection
    private final UserRepository userRepository;
    private final TicketRepository ticketRepository;
    private final BusRouteRepository busRouteRepository;

    public AuthController(UserRepository userRepository,
                          TicketRepository ticketRepository,
                          BusRouteRepository busRouteRepository) {
        this.userRepository = userRepository;
        this.ticketRepository = ticketRepository;
        this.busRouteRepository = busRouteRepository;
    }

    // Сторінка входу
    @GetMapping("/login")
    public ModelAndView loginPage(@RequestParam(required = false) String error,
                                  HttpSession session) {
        if (session.getAttribute("user") != null) {
            return new ModelAndView(REDIRECT_HOME);
        }
        ModelAndView modelAndView = new ModelAndView("login-page");
        if (error != null) {
            modelAndView.addObject("error", "Невірний телефон або пароль");
        }
        return modelAndView;
    }

    // Обробка входу
    @PostMapping("/login")
    public String login(@RequestParam String phone,
                        @RequestParam String password,
                        HttpSession session) {
        User user = userRepository.findByPhoneAndPassword(phone, password).orElse(null);
        if (user != null && user.getIsActive()) {
            // ВИПРАВЛЕННЯ 1: User реалізує Serializable — тепер безпечно зберігати в сесії
            session.setAttribute("user", user);
            return REDIRECT_HOME;
        }
        return "redirect:/login?error";
    }

    // Сторінка реєстрації
    @GetMapping("/register")
    public ModelAndView registerPage(@RequestParam(required = false) String error,
                                     HttpSession session) {
        if (session.getAttribute("user") != null) {
            return new ModelAndView(REDIRECT_HOME);
        }
        ModelAndView modelAndView = new ModelAndView("register");
        if (error != null) {
            modelAndView.addObject("error", "Користувач з таким телефоном вже існує");
        }
        return modelAndView;
    }

    // Обробка реєстрації
    @PostMapping("/register")
    public String register(@RequestParam String fullName,
                           @RequestParam String phone,
                           @RequestParam String password,
                           HttpSession session) {
        if (userRepository.existsByPhone(phone)) {
            return "redirect:/register?error";
        }
        User user = new User();
        user.setFullName(fullName);
        user.setPhone(phone);
        user.setPassword(password);
        user.setRole(UserRole.PASSENGER);
        user.setCreatedAt(LocalDateTime.now());
        user.setIsActive(true);
        userRepository.save(user);
        session.setAttribute("user", user);
        return REDIRECT_HOME;
    }

    // Вихід
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return REDIRECT_HOME;
    }

    // Особистий кабінет
    @GetMapping("/profile")
    public ModelAndView profile(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return new ModelAndView("redirect:/login");
        }
        ModelAndView modelAndView = new ModelAndView("profile");
        modelAndView.addObject("user", user);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        String formattedDate = user.getCreatedAt().format(formatter);
        modelAndView.addObject("formattedDate", formattedDate);
        List<Ticket> userTickets = ticketRepository.findByPassengerPhoneAndStatus(
                user.getPhone(),
                Ticket.TicketStatus.ACTIVE
        );
        modelAndView.addObject("tickets", userTickets);
        return modelAndView;
    }

    @PostMapping("/profile/cancel-ticket")
    public String cancelTicket(@RequestParam Long ticketId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        Ticket ticket = ticketRepository.findById(ticketId).orElse(null);
        if (ticket != null && ticket.getPassengerPhone().equals(user.getPhone())) {
            ticket.setStatus(Ticket.TicketStatus.RETURNED);
            ticketRepository.save(ticket);
            BusRoute route = ticket.getBusRoute();
            route.setAvailableSeats(route.getAvailableSeats() + 1);
            busRouteRepository.save(route);
        }
        return "redirect:/profile?cancelled=true";
    }
}