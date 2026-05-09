package ua.com.kisit.chernykhnazarcourse2026np.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ua.com.kisit.chernykhnazarcourse2026np.entity.Ticket;
import ua.com.kisit.chernykhnazarcourse2026np.entity.User;
import ua.com.kisit.chernykhnazarcourse2026np.entity.UserRole;
import ua.com.kisit.chernykhnazarcourse2026np.repository.TicketRepository;
import ua.com.kisit.chernykhnazarcourse2026np.repository.UserRepository;
import ua.com.kisit.chernykhnazarcourse2026np.service.TicketService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final UserRepository userRepository;
    private final TicketRepository ticketRepository;
    private final TicketService ticketService;
    private final PasswordEncoder passwordEncoder;

    // Конструктор – видалено зайвий BusRouteRepository
    public AuthController(UserRepository userRepository,
                          TicketRepository ticketRepository,
                          TicketService ticketService,
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.ticketRepository = ticketRepository;
        this.ticketService = ticketService;
        this.passwordEncoder = passwordEncoder;
    }

    // Показує сторінку входу або перенаправляє на головну, якщо вже залогінений
    @GetMapping("/login")
    public ModelAndView loginPage(@RequestParam(required = false) String error) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth.getPrincipal() instanceof String)) {
            return new ModelAndView("redirect:/");
        }
        ModelAndView mav = new ModelAndView("login-page");
        if (error != null) {
            mav.addObject("error", "Невірний телефон або пароль");
        }
        return mav;
    }

    // Показує сторінку реєстрації (якщо не авторизований)
    @GetMapping("/register")
    public ModelAndView registerPage(@RequestParam(required = false) String error) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth.getPrincipal() instanceof String)) {
            return new ModelAndView("redirect:/");
        }
        ModelAndView mav = new ModelAndView("register");
        if (error != null) {
            mav.addObject("error", "Користувач з таким телефоном вже існує");
        }
        return mav;
    }

    // Реєстрація нового користувача: хешує пароль, зберігає в БД, логує дію
    @PostMapping("/register")
    public String register(@RequestParam String fullName,
                           @RequestParam String phone,
                           @RequestParam String password,
                           HttpServletRequest request) {
        if (userRepository.existsByPhone(phone)) {
            return "redirect:/register?error";
        }
        User user = new User();
        user.setFullName(fullName);
        user.setPhone(phone);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(UserRole.PASSENGER);
        user.setCreatedAt(LocalDateTime.now());
        user.setIsActive(true);
        userRepository.save(user);
        log.info("РЕЄСТРАЦІЯ: {} зареєструвався з телефоном {} (IP: {})",
                fullName, phone, request.getRemoteAddr());
        return "redirect:/login?registered";
    }

    // Особистий кабінет – отримує користувача з сесії, показує його квитки
    @GetMapping("/profile")
    public ModelAndView profile(HttpSession session) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || (auth.getPrincipal() instanceof String)) {
            return new ModelAndView("redirect:/login");
        }
        User user = (User) session.getAttribute("user");
        if (user == null) return new ModelAndView("redirect:/login");
        ModelAndView mav = new ModelAndView("profile");
        mav.addObject("user", user);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        mav.addObject("formattedDate", user.getCreatedAt().format(formatter));
        List<Ticket> userTickets = ticketRepository.findByPassengerPhoneAndStatus(
                user.getPhone(), Ticket.TicketStatus.ACTIVE);
        mav.addObject("tickets", userTickets);
        return mav;
    }

    // Скасування квитка – перевіряє власника, логує дію
    @PostMapping("/profile/cancel-ticket")
    public String cancelTicket(@RequestParam Long ticketId, HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || (auth.getPrincipal() instanceof String)) {
            return "redirect:/login";
        }
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        String phone = userDetails.getUsername();
        Ticket ticket = ticketRepository.findById(ticketId).orElse(null);
        if (ticket != null && ticket.getPassengerPhone().equals(phone)) {
            ticketService.cancelTicket(ticketId);
            log.info("СКАСУВАННЯ: {} скасував квиток ID {} (IP: {})",
                    phone, ticketId, request.getRemoteAddr());
        }
        return "redirect:/profile?cancelled=true";
    }
}