package ua.com.kisit.chernykhnazarcourse2026np.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ua.com.kisit.chernykhnazarcourse2026np.entity.User;
import ua.com.kisit.chernykhnazarcourse2026np.service.TicketService;

@Controller
@RequestMapping("/admin/tickets")
public class AdminTicketController {

    private static final Logger log = LoggerFactory.getLogger(AdminTicketController.class);

    private final TicketService ticketService;

    @Autowired
    public AdminTicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping
    public ModelAndView ticketsPage(@RequestParam(required = false) String phone) {
        ModelAndView modelAndView = new ModelAndView("admin/tickets");
        if (phone != null && !phone.isEmpty()) {
            modelAndView.addObject("tickets", ticketService.getTicketsByPhone(phone));
            modelAndView.addObject("searchPhone", phone);
        } else {
            modelAndView.addObject("tickets", ticketService.getAllTickets());
        }
        return modelAndView;
    }

    @PostMapping("/cancel/{id}")
    public String cancelTicket(@PathVariable Long id, HttpSession session, HttpServletRequest request) {
        ticketService.cancelTicket(id);
        User admin = (User) session.getAttribute("user");
        log.info("АДМІН: {} скасував квиток ID {} (IP: {})",
                admin != null ? admin.getPhone() : "unknown", id, request.getRemoteAddr());
        return "redirect:/admin/tickets";
    }
}