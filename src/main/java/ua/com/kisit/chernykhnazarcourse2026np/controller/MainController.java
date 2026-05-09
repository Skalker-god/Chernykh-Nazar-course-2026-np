package ua.com.kisit.chernykhnazarcourse2026np.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import ua.com.kisit.chernykhnazarcourse2026np.service.RouteService;

import java.time.LocalDate;
import java.util.List;

@Controller
public class MainController {

    @Autowired
    private RouteService routeService;

    @GetMapping("/")
    public ModelAndView index() {
        ModelAndView mav = new ModelAndView("index");
        mav.addObject("routes", routeService.getUpcomingRoutes());
        mav.addObject("originCities", routeService.getAllOriginCities());
        mav.addObject("destinationCities", routeService.getAllDestinationCities());
        mav.addObject("today", LocalDate.now().toString());
        return mav;
    }

    @GetMapping("/search")
    public ModelAndView searchRoutes(
            @RequestParam(required = false) String origin,
            @RequestParam(required = false) String destination,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        ModelAndView mav = new ModelAndView("index");

        List<?> results = routeService.searchRoutes(origin, destination, date);
        mav.addObject("routes", results);
        mav.addObject("searchOrigin", origin);
        mav.addObject("searchDestination", destination);
        mav.addObject("searchDate", date != null ? date.toString() : "");
        mav.addObject("originCities", routeService.getAllOriginCities());
        mav.addObject("destinationCities", routeService.getAllDestinationCities());
        mav.addObject("today", LocalDate.now().toString());
        mav.addObject("searchPerformed", true);

        return mav;
    }

    @GetMapping("/about")
    public ModelAndView about() {
        return new ModelAndView("about");
    }
}