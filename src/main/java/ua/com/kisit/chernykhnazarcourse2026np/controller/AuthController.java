package ua.com.kisit.chernykhnazarcourse2026np.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private BusRouteRepository busRouteRepository;

    // Сторінка входу
    @GetMapping("/login")
    public ModelAndView loginPage(@RequestParam(required = false) String error,
                                  HttpSession session) {
        if (session.getAttribute("user") != null) {
            return new ModelAndView("redirect:/");
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
            session.setAttribute("user", user);
            return "redirect:/";
        }

        return "redirect:/login?error";
    }


    // Сторінка реєстрації
    @GetMapping("/register")
    public ModelAndView registerPage(@RequestParam(required = false) String error,
                                     HttpSession session) {
        // Якщо вже залогінений - редірект на головну
        if (session.getAttribute("user") != null) {
            return new ModelAndView("redirect:/");
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

        // Перевіряємо чи не існує вже такий телефон
        if (userRepository.existsByPhone(phone)) {
            return "redirect:/register?error";
        }

        // Створюємо нового користувача
        User user = new User();
        user.setFullName(fullName);
        user.setPhone(phone);
        user.setPassword(password); // В реальному проекті треба хешувати!
        user.setRole(UserRole.PASSENGER); // За замовчуванням - пасажир
        user.setCreatedAt(LocalDateTime.now());
        user.setIsActive(true);

        userRepository.save(user);

        // Одразу логінимо
        session.setAttribute("user", user);

        return "redirect:/";
    }

    // Вихід
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // Видаляє всю сесію
        return "redirect:/";
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

        // Форматуємо дату
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        String formattedDate = user.getCreatedAt().format(formatter);
        modelAndView.addObject("formattedDate", formattedDate);

        // Отримуємо квитки користувача
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
            // Змінюємо статус квитка
            ticket.setStatus(Ticket.TicketStatus.RETURNED);
            ticketRepository.save(ticket);

            // Повертаємо вільне місце
            BusRoute route = ticket.getBusRoute();
            route.setAvailableSeats(route.getAvailableSeats() + 1);
            busRouteRepository.save(route);
        }

        return "redirect:/profile?cancelled=true";
    }

}