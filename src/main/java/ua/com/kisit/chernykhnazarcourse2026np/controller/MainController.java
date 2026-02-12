package ua.com.kisit.chernykhnazarcourse2026np.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import ua.com.kisit.chernykhnazarcourse2026np.entity.BusRoute;
import ua.com.kisit.chernykhnazarcourse2026np.repository.BusRouteRepository;

import java.time.LocalTime;
import java.util.List;

@Controller
public class MainController {

    @Autowired
    private BusRouteRepository busRouteRepository;

    @GetMapping("/")
    public ModelAndView index() {
        ModelAndView modelAndView = new ModelAndView("index");

        List<BusRoute> routes = busRouteRepository.findByIsActiveTrue();

        modelAndView.addObject("routes", routes);
        modelAndView.addObject("currentTime", LocalTime.now());

        return modelAndView;
    }

    @GetMapping("/search")
    public ModelAndView searchRoutes(String destination) {
        ModelAndView modelAndView = new ModelAndView("index");

        if (destination != null && !destination.isEmpty()) {
            List<BusRoute> routes = busRouteRepository
                    .findAvailableRoutesToDestination(destination);
            modelAndView.addObject("routes", routes);
            modelAndView.addObject("searchQuery", destination);
        } else {
            List<BusRoute> routes = busRouteRepository.findByIsActiveTrue();
            modelAndView.addObject("routes", routes);
        }

        modelAndView.addObject("currentTime", LocalTime.now());
        return modelAndView;
    }

    @GetMapping("/about")
    public ModelAndView about() {
        return new ModelAndView("about");
    }
}