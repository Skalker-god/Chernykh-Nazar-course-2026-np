package ua.com.kisit.chernykhnazarcourse2026np.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ua.com.kisit.chernykhnazarcourse2026np.entity.*;
import ua.com.kisit.chernykhnazarcourse2026np.repository.BusRouteRepository;
import ua.com.kisit.chernykhnazarcourse2026np.repository.TicketRepository;
import ua.com.kisit.chernykhnazarcourse2026np.service.TicketService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
public class CartController {

    private static final Logger log = LoggerFactory.getLogger(CartController.class);

    @Autowired
    private BusRouteRepository busRouteRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private TicketService ticketService;

    // Отримуємо кошик із сесії або створюємо новий порожній
    private Cart getCart(HttpSession session) {
        Cart cart = (Cart) session.getAttribute("cart");
        if (cart == null) {
            cart = new Cart();
            session.setAttribute("cart", cart);
        }
        return cart;
    }

    // Перевірка: чи вільне конкретне місце на рейс
    private boolean isSeatAvailable(BusRoute route, Integer seatNumber) {
        List<Integer> occupied = ticketService.getOccupiedSeats(route, route.getDepartureDate());
        return !occupied.contains(seatNumber);
    }

    private void validateCartItems(Cart cart) {
        List<CartItem> toRemove = new ArrayList<>();
        for (CartItem item : cart.getItems()) {
            BusRoute route = item.getRoute();
            // Видаляємо якщо рейс зник або деактивований
            if (route == null || !route.getIsActive()) {
                toRemove.add(item);
                continue;
            }
            // Видаляємо якщо місце вже зайняте (інший користувач встиг купити)
            if (!isSeatAvailable(route, item.getSeatNumber())) {
                toRemove.add(item);
            }
        }
        toRemove.forEach(item -> cart.removeItem(item.getRoute().getId(), item.getSeatNumber()));
    }

    // Відображення кошика — спочатку очищаємо застарілі позиції
    @GetMapping("/cart")
    public ModelAndView viewCart(HttpSession session) {
        Cart cart = getCart(session);
        User user = (User) session.getAttribute("user");
        // validateCartItems робить всі перевірки місць одноразово
        validateCartItems(cart);
        ModelAndView mav = new ModelAndView("cart");
        mav.addObject("cart", cart);
        mav.addObject("user", user);
        return mav;
    }

    @PostMapping("/cart/add")
    public String addToCart(@RequestParam Long routeId,
                            @RequestParam String destination,
                            @RequestParam Integer seatNumber,
                            HttpSession session,
                            HttpServletRequest request) {
        BusRoute route = busRouteRepository.findById(routeId).orElse(null);
        if (route == null) return "redirect:/?error=route_not_found";
        if (route.getAvailableSeats() <= 0) return "redirect:/ticket/book/" + routeId + "?error=no_seats";

        // Перевірка конкретного місця перед додаванням (без validateCartItems)
        if (!isSeatAvailable(route, seatNumber))
            return "redirect:/ticket/book/" + routeId + "?error=seat_taken&seat=" + seatNumber;

        Cart cart = getCart(session);

        // Перевірка: це місце вже є в кошику цього користувача
        boolean alreadyInCart = cart.getItems().stream().anyMatch(
                item -> item.getRoute().getId().equals(routeId)
                        && item.getSeatNumber().equals(seatNumber));
        if (alreadyInCart) return "redirect:/cart?error=already_in_cart";

        cart.addItem(route, destination, seatNumber);

        User currentUser = (User) session.getAttribute("user");
        log.info("КОШИК: Додано квиток рейс {} місце {} (користувач: {})",
                routeId, seatNumber,
                currentUser != null ? currentUser.getPhone() : "guest");
        return "redirect:/cart";
    }

    // Видалення одного квитка з кошика
    @PostMapping("/cart/remove")
    public String removeFromCart(@RequestParam Long routeId,
                                 @RequestParam Integer seatNumber,
                                 HttpSession session,
                                 HttpServletRequest request) {
        getCart(session).removeItem(routeId, seatNumber);
        log.info("КОШИК: Видалено квиток рейс {} місце {}", routeId, seatNumber);
        return "redirect:/cart";
    }

    // Повне очищення кошика
    @PostMapping("/cart/clear")
    public String clearCart(HttpSession session) {
        getCart(session).clear();
        return "redirect:/cart";
    }

    @PostMapping("/cart/checkout")
    public String checkoutCart(@RequestParam(required = false) String passengerName,
                               @RequestParam(required = false) String passengerPhone,
                               @RequestParam(defaultValue = "false") Boolean isAdvance,
                               HttpSession session,
                               HttpServletRequest request) {
        Cart cart = getCart(session);
        User sessionUser = (User) session.getAttribute("user");
        if (cart.isEmpty()) return "redirect:/cart";

        // Якщо користувач залогінений — беремо його дані, інакше з форми
        String finalName;
        String finalPhone;
        if (sessionUser != null) {
            finalName = sessionUser.getFullName();
            finalPhone = sessionUser.getPhone();
        } else {
            if (passengerName == null || passengerName.isBlank()
                    || passengerPhone == null || passengerPhone.isBlank()) {
                return "redirect:/cart?error=missing_data";
            }
            finalName = passengerName;
            finalPhone = passengerPhone;
        }

        // Фінальна перевірка всіх місць перед збереженням у БД
        for (CartItem item : cart.getItems()) {
            BusRoute route = item.getRoute();
            if (route == null || !route.getIsActive()) {
                cart.removeItem(item.getRoute().getId(), item.getSeatNumber());
                return "redirect:/cart?error=route_inactive";
            }
            if (route.getAvailableSeats() <= 0) {
                cart.clear();
                return "redirect:/cart?error=no_seats_left";
            }
            if (!isSeatAvailable(route, item.getSeatNumber())) {
                cart.removeItem(route.getId(), item.getSeatNumber());
                return "redirect:/cart?error=seat_taken&seat=" + item.getSeatNumber();
            }
        }

        // Зберігаємо всі квитки і зменшуємо лічильник вільних місць
        int ticketCount = cart.getItemCount();
        double total = cart.getTotal();
        for (CartItem item : cart.getItems()) {
            BusRoute route = item.getRoute();
            Ticket ticket = new Ticket();
            ticket.setBusRoute(route);
            ticket.setPassengerName(finalName);
            ticket.setPassengerPhone(finalPhone);
            ticket.setSeatNumber(item.getSeatNumber());
            ticket.setTravelDate(route.getDepartureDate());
            ticket.setDestination(item.getDestination());
            ticket.setStatus(Ticket.TicketStatus.ACTIVE);
            ticket.setPurchaseDateTime(LocalDateTime.now());
            ticket.setIsAdvancePurchase(isAdvance);
            ticketRepository.save(ticket);
            route.setAvailableSeats(route.getAvailableSeats() - 1);
            busRouteRepository.save(route);
        }
        cart.clear();
        log.info("ПОКУПКА: {} придбав {} квитків на суму {} грн (IP: {})",
                finalPhone, ticketCount, total, request.getRemoteAddr());
        return "redirect:/?purchased=true";
    }
}