package ua.com.kisit.chernykhnazarcourse2026np.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Cart implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<CartItem> items = new ArrayList<>();

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }

    public void addItem(BusRoute route, String destination, Integer seatNumber) {
        for (CartItem item : items) {
            if (item.getRoute().getId().equals(route.getId()) &&
                    item.getSeatNumber().equals(seatNumber)) {
                return;
            }
        }
        CartItem newItem = new CartItem(route, destination, seatNumber);
        items.add(newItem);
    }

    public void removeItem(Long routeId, Integer seatNumber) {
        items.removeIf(item ->
                item.getRoute().getId().equals(routeId) &&
                        item.getSeatNumber().equals(seatNumber)
        );
    }

    public void clear() {
        items.clear();
    }

    public Double getTotal() {
        return items.stream()
                .mapToDouble(item -> item.getRoute().getTicketPrice())
                .sum();
    }

    public int getItemCount() {
        return items.size();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }
}