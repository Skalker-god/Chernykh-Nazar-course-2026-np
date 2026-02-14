package ua.com.kisit.chernykhnazarcourse2026np.entity;

import java.util.ArrayList;
import java.util.List;

public class Cart {

    private List<CartItem> items = new ArrayList<>();

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }

    // Додати квиток до кошика
    public synchronized void addItem(BusRoute route, String destination, Integer seatNumber) {
        // Перевіряємо чи немає вже такого квитка
        for (CartItem item : items) {
            if (item.getRoute().getId().equals(route.getId()) &&
                    item.getSeatNumber().equals(seatNumber)) {
                return; // Місце вже в кошику
            }
        }

        CartItem newItem = new CartItem(route, destination, seatNumber);
        items.add(newItem);
    }

    // Видалити квиток з кошика
    public synchronized void removeItem(Long routeId, Integer seatNumber) {
        items.removeIf(item ->
                item.getRoute().getId().equals(routeId) &&
                        item.getSeatNumber().equals(seatNumber)
        );
    }

    // Очистити кошик
    public synchronized void clear() {
        items.clear();
    }

    // Порахувати загальну вартість
    public Double getTotal() {
        return items.stream()
                .mapToDouble(item -> item.getRoute().getTicketPrice())
                .sum();
    }

    // Кількість квитків у кошику
    public int getItemCount() {
        return items.size();
    }

    // Перевірка чи порожній кошик
    public boolean isEmpty() {
        return items.isEmpty();
    }
}