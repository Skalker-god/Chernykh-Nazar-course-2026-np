package ua.com.kisit.chernykhnazarcourse2026np.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import ua.com.kisit.chernykhnazarcourse2026np.entity.BusRoute;
import ua.com.kisit.chernykhnazarcourse2026np.repository.BusRouteRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class RouteService {

    private final BusRouteRepository busRouteRepository;

    @Autowired
    public RouteService(BusRouteRepository busRouteRepository) {
        this.busRouteRepository = busRouteRepository;
    }

    // Повертає рейси починаючи з сьогодні (для головної сторінки)
    public List<BusRoute> getUpcomingRoutes() {
        return busRouteRepository.findUpcomingRoutes(LocalDate.now());
    }

    // Повертає всі рейси без фільтрації (для адмін-панелі)
    public List<BusRoute> getAllRoutes() {
        return busRouteRepository.findAll();
    }

    // Отримує рейс за ID з кешуванням (уникає зайвих запитів до БД)
    @Cacheable(value = "routeById", key = "#id")
    public BusRoute getRouteById(Long id) {
        return busRouteRepository.findById(id).orElse(null);
    }

    // Головний пошук рейсів: за містами та/або датою
    public List<BusRoute> searchRoutes(String origin, String destination, LocalDate date) {
        if (origin != null && !origin.isBlank()
                && destination != null && !destination.isBlank()
                && date != null) {
            return busRouteRepository.searchRoutes(origin.trim(), destination.trim(), date);
        } else if (origin != null && !origin.isBlank()
                && destination != null && !destination.isBlank()) {
            return busRouteRepository.searchRoutesByOriginAndDestination(
                    origin.trim(), destination.trim());
        } else if (destination != null && !destination.isBlank()) {
            return busRouteRepository.findAvailableRoutesToDestination(destination.trim());
        } else if (date != null) {
            return busRouteRepository.findByDepartureDateAndIsActiveTrue(date);
        }
        return getUpcomingRoutes();
    }

    // Список унікальних міст відправлення для автодоповнення
    public List<String> getAllOriginCities() {
        return busRouteRepository.findAllOriginCities();
    }

    // Список унікальних міст призначення для автодоповнення
    public List<String> getAllDestinationCities() {
        return busRouteRepository.findAllDestinationCities();
    }

    // Додає новий рейс, очищає кеш після додавання
    @CacheEvict(value = "routes", allEntries = true)
    public void addRoute(String routeNumber,
                         String originCity,
                         String finalDestination,
                         String intermediateStops,
                         LocalDate departureDate,
                         LocalTime departureTime,
                         Integer totalSeats,
                         Double ticketPrice) {

        BusRoute route = new BusRoute();
        route.setRouteNumber(routeNumber);
        route.setOriginCity(originCity);
        route.setFinalDestination(finalDestination);
        route.setIntermediateStops(intermediateStops);
        route.setDepartureDate(departureDate);
        route.setDepartureTime(departureTime);
        route.setTotalSeats(totalSeats);
        route.setAvailableSeats(totalSeats);
        route.setTicketPrice(ticketPrice);
        route.setIsActive(true);

        busRouteRepository.save(route);
    }

    // Змінює статус рейсу (активний/неактивний), оновлює кеш
    @CacheEvict(value = "routeById", key = "#id")
    public void toggleRoute(Long id) {
        BusRoute route = busRouteRepository.findById(id).orElse(null);
        if (route != null) {
            route.setIsActive(!route.getIsActive());
            busRouteRepository.save(route);
        }
    }

    // Видаляє рейс з БД і чистить кеш
    @CacheEvict(value = "routeById", key = "#id")
    public void deleteRoute(Long id) {
        busRouteRepository.deleteById(id);
    }
}