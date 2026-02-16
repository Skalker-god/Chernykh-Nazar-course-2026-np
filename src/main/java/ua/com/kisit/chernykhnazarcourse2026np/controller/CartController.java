package ua.com.kisit.chernykhnazarcourse2026np.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import ua.com.kisit.chernykhnazarcourse2026np.entity.User;
import ua.com.kisit.chernykhnazarcourse2026np.entity.BusRoute;
import ua.com.kisit.chernykhnazarcourse2026np.entity.Cart;
import ua.com.kisit.chernykhnazarcourse2026np.entity.Ticket;
import ua.com.kisit.chernykhnazarcourse2026np.repository.BusRouteRepository;
import ua.com.kisit.chernykhnazarcourse2026np.repository.TicketRepository;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Controller
public class CartController {

    @Autowired
    private BusRouteRepository busRouteRepository;

    @Autowired
    private TicketRepository ticketRepository;

    private User getCurrentUser(HttpSession session) {
        return (User) session.getAttribute("user");
    }

    // Отримати кошик з сесії або створити новий
    private Cart getCart(HttpSession session) {
        Cart cart = (Cart) session.getAttribute("cart");
        if (cart == null) {
            cart = new Cart();
            session.setAttribute("cart", cart);
        }
        return cart;
    }

    // Переглянути кошик
    @GetMapping("/cart")
    public ModelAndView viewCart(HttpSession session) {
        ModelAndView modelAndView = new ModelAndView("cart");
        Cart cart = getCart(session);
        User user = (User) session.getAttribute("user");

        modelAndView.addObject("cart", cart);
        modelAndView.addObject("user", user);

        return modelAndView;
    }

    // Додати квиток до кошика
    @PostMapping("/cart/add")
    public String addToCart(@RequestParam Long routeId,
                            @RequestParam String destination,
                            @RequestParam Integer seatNumber,
                            HttpSession session) {

        BusRoute route = busRouteRepository.findById(routeId).orElse(null);

        if (route != null && route.getAvailableSeats() > 0) {
            Cart cart = getCart(session);
            cart.addItem(route, destination, seatNumber);
        }

        return "redirect:/cart";
    }

    // Видалити квиток з кошика
    @PostMapping("/cart/remove")
    public String removeFromCart(@RequestParam Long routeId,
                                 @RequestParam Integer seatNumber,
                                 HttpSession session) {
        Cart cart = getCart(session);
        cart.removeItem(routeId, seatNumber);
        return "redirect:/cart";
    }

    // Очистити кошик
    @PostMapping("/cart/clear")
    public String clearCart(HttpSession session) {
        Cart cart = getCart(session);
        cart.clear();
        return "redirect:/cart";
    }

    // Оформити всі квитки з кошика
    @PostMapping("/cart/checkout")
    public String checkoutCart(@RequestParam String passengerName,
                               @RequestParam String passengerPhone,
                               @RequestParam(defaultValue = "false") Boolean isAdvance,
                               HttpSession session) {

        Cart cart = getCart(session);

        if (cart.isEmpty()) {
            return "redirect:/cart";
        }

        // Створюємо квитки для кожного елемента кошика
        cart.getItems().forEach(item -> {
            Ticket ticket = new Ticket();
            ticket.setBusRoute(item.getRoute());
            ticket.setPassengerName(passengerName);
            ticket.setPassengerPhone(passengerPhone);
            ticket.setSeatNumber(item.getSeatNumber());
            ticket.setTravelDate(LocalDate.now());
            ticket.setDestination(item.getDestination());
            ticket.setStatus(Ticket.TicketStatus.ACTIVE);
            ticket.setPurchaseDateTime(LocalDateTime.now());
            ticket.setIsAdvancePurchase(isAdvance);

            ticketRepository.save(ticket);

            // Зменшуємо кількість вільних місць
            BusRoute route = item.getRoute();
            route.setAvailableSeats(route.getAvailableSeats() - 1);
            busRouteRepository.save(route);
        });

        // Очищаємо кошик після оформлення
        cart.clear();

        return "redirect:/cart/success";
    }

    // Сторінка успішного оформлення
    @GetMapping("/cart/success")
    public String checkoutSuccess() {
        return "redirect:/?purchased=true";
    }
}