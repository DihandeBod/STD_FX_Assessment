package Services;

import BusinessRules.OrderConstraints;
import com.Entities.Orders;
import com.Entities.Side;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

public class OrderService {
    private final OrderConstraints orderConstraints;
    public OrderService(OrderConstraints orderConstraints) {
        this.orderConstraints = orderConstraints;
    }

    public List<Orders> initialiseOrders() {
        List<Orders> allOrders = new LinkedList<>();
        List<Orders> invalidOrders = new ArrayList<>();
        allOrders.add(new Orders(1, 1.00, 10, Side.BUY, LocalDateTime.now(), LocalDateTime.now().plusSeconds(1))); // Add one second on last update otherwise checks fail
        allOrders.add(new Orders(2, 1.50, 5, Side.BUY, LocalDateTime.now(), LocalDateTime.now().plusDays(1)));
        allOrders.add(new Orders(3, 1.00, 15, Side.BUY, LocalDateTime.now(), LocalDateTime.now().minusHours(1))); // This should be invalid
        allOrders.add(new Orders(4, 1.00, 5, Side.SELL, LocalDateTime.now(), LocalDateTime.now().plusMinutes(30)));
        allOrders.add(new Orders(5, 1.50, 12, Side.SELL, LocalDateTime.now(), LocalDateTime.now().plusDays(2)));
        allOrders.add(new Orders(6, 0.00, 10, Side.BUY, LocalDateTime.now(), LocalDateTime.now().plusDays(2))); // Invalid
        allOrders.add(new Orders(7, 1.25, 0, Side.BUY, LocalDateTime.now(), LocalDateTime.now().plusDays(2))); // Invalid
        allOrders.add(new Orders(8, -1.00, 10, Side.BUY, LocalDateTime.now(), LocalDateTime.now().plusSeconds(10))); // Invalid
        allOrders.add(new Orders(9, 2.00, 20, Side.SELL, LocalDateTime.now().plusDays(5), LocalDateTime.now().plusDays(4))); // Invalid
        allOrders.add(new Orders(10, 2.00, 20, Side.BUY, LocalDateTime.now(), LocalDateTime.now().plusHours(2)));

        for (Orders order : allOrders) {
            if (order.getPrice().compareTo(BigDecimal.ZERO) > 0 && order.getQuantity() > 0 && order.getOrderDate().isBefore(order.getOrderDate().plusDays(orderConstraints.VALID_ORDER_DATE_IN_FUTURE)) && order.getOrderDate().isBefore(order.getLastUpdated())) {
                System.out.println("Order " + order.getId() + " is a valid order with side: " + order.getSide());
            } else {
                invalidOrders.add(order);
            }
        }
        allOrders.removeAll(invalidOrders);

        return allOrders;
    }


    public void addBuyOrder(HashMap<BigDecimal, Queue<Orders>> orderTable, List<Orders> orderDetails) {
        for (Orders order : orderDetails) {
            // Determine whether to add a new item in hashtable
            // TODO: YOU ARE HERE
            System.out.println("Adding a buy order");
            if (orderTable.containsKey(order.getPrice())) {
                // TODO Add order to Queue
                orderTable.get(order.getPrice()).add(order);
                System.out.println("Hashmap: @ Price " + order.getPrice() + ": " + orderTable.get(order.getPrice()));
            } else {
                Queue<Orders> ordersQueue = new LinkedList<>();
                ordersQueue.add(order);

                orderTable.put(order.getPrice(), ordersQueue);
                System.out.println("Hashmap: @ Price " + order.getPrice() + ": " + orderTable.get(order.getPrice()));
                // TODO Add price to hashtable and order to Queue
                // orderTable.put(order.Price, order);
            }
        }
    }

    public void addSellOrder(HashMap<BigDecimal, Queue<Orders>> orderTable, List<Orders> orderDetails) {
        for (Orders order : orderDetails) {
            // Determine whether to add a new item in hashtable
            // TODO: YOU ARE HERE
            System.out.println("Adding a sell order");
            if (orderTable.containsKey(order.getPrice())) {
                // TODO Add order to Queue
                orderTable.get(order.getPrice()).add(order);
                System.out.println("Hashmap: @ Price " + order.getPrice() + ": " + orderTable.get(order.getPrice()));
            } else {
                Queue<Orders> ordersQueue = new LinkedList<>();
                ordersQueue.add(order);

                orderTable.put(order.getPrice(), ordersQueue);
                System.out.println("Hashmap: @ Price " + order.getPrice() + ": " + orderTable.get(order.getPrice()));
                // TODO Add price to hashtable and order to Queue
                // orderTable.put(order.Price, order);
            }
        }
    }
}
