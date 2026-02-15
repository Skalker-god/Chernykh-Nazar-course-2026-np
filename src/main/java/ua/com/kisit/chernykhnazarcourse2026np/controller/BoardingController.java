package ua.com.kisit.chernykhnazarcourse2026np.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;
import ua.com.kisit.chernykhnazarcourse2026np.entity.User;
import ua.com.kisit.chernykhnazarcourse2026np.entity.UserRole;
import ua.com.kisit.chernykhnazarcourse2026np.entity.BoardingList;
import ua.com.kisit.chernykhnazarcourse2026np.entity.BusRoute;
import ua.com.kisit.chernykhnazarcourse2026np.repository.BoardingListRepository;
import ua.com.kisit.chernykhnazarcourse2026np.repository.BusRouteRepository;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.List;

@Controller
public class BoardingController {

    @Autowired
    private BusRouteRepository busRouteRepository;

    @Autowired
    private BoardingListRepository boardingListRepository;

    @GetMapping("/boarding")
    public ModelAndView boardingListsPage() {
        ModelAndView modelAndView = new ModelAndView("boarding-list");

        List<BusRoute> routes = busRouteRepository.findByIsActiveTrue();
        modelAndView.addObject("routes", routes);

        return modelAndView;
    }

    @GetMapping("/boarding/view/{routeId}")
    public ModelAndView viewBoardingList(@PathVariable Long routeId, HttpSession session) {
        User user = (User) session.getAttribute("user");

        // Перевірка прав доступу
        if (user == null || user.getRole() == UserRole.PASSENGER) {
            return new ModelAndView("redirect:/login");
        }

        ModelAndView modelAndView = new ModelAndView("boarding-list");

        BusRoute route = busRouteRepository.findById(routeId).orElse(null);

        if (route != null) {
            LocalDate travelDate = LocalDate.now();

            BoardingList boardingList = boardingListRepository
                    .findByBusRouteAndTravelDate(route, travelDate)
                    .orElse(null);

            modelAndView.addObject("route", route);
            modelAndView.addObject("boardingList", boardingList);
            modelAndView.addObject("travelDate", travelDate);
        }

        List<BusRoute> routes = busRouteRepository.findByIsActiveTrue();
        modelAndView.addObject("routes", routes);

        return modelAndView;
    }
}