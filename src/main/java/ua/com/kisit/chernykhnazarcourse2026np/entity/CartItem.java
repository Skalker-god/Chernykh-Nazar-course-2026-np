package ua.com.kisit.chernykhnazarcourse2026np.entity;

public class CartItem {

    private BusRoute route;
    private String destination;
    private Integer seatNumber;

    public CartItem() {
    }

    public CartItem(BusRoute route, String destination, Integer seatNumber) {
        this.route = route;
        this.destination = destination;
        this.seatNumber = seatNumber;
    }

    public BusRoute getRoute() {
        return route;
    }

    public void setRoute(BusRoute route) {
        this.route = route;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public Integer getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(Integer seatNumber) {
        this.seatNumber = seatNumber;
    }

    public Double getPrice() {
        return route != null ? route.getTicketPrice() : 0.0;
    }
}