package Services;

import BusinessRules.OrderConstraints;
import com.Entities.Orders;
import com.Entities.Side;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Queue;

import static org.junit.jupiter.api.Assertions.*;

class OrderServiceTest {

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        OrderConstraints orderConstraints = new OrderConstraints();
        orderService = new OrderService(orderConstraints);

        orderService.allOrders.clear();
        orderService.invalidOrders.clear();
        orderService.allBuyOrders.clear();
        orderService.allSellOrders.clear();
        orderService.orderIdMap.clear();
        orderService.orderBook.getBuyOrders().clear();
        orderService.orderBook.getSellOrders().clear();
    }

    @Test
    @DisplayName("Loading orders into hashmap")
    void getAllOrders() {
        OrderConstraints orderConstraints = new OrderConstraints();
        OrderService orderService = new OrderService(orderConstraints);
        orderService.allOrders.clear();

        orderService.initialiseOrders();
        assertEquals(10, orderService.allOrders.size());
    }

    @Test
    @DisplayName("Size check of buy orders")
    void checkBuyOrdersSize() {
        OrderConstraints orderConstraints = new OrderConstraints();
        OrderService orderService = new OrderService(orderConstraints);
        orderService.allOrders.clear();

        orderService.initialiseOrders();
        orderService.handleOrders(orderService.allOrders);

        assertEquals(0, orderService.allBuyOrders.size());
    }

    @Test
    @DisplayName("Size check of sell orders")
    void checkSellOrdersSize() {
        OrderConstraints orderConstraints = new OrderConstraints();
        OrderService orderService = new OrderService(orderConstraints);
        orderService.allOrders.clear();

        orderService.initialiseOrders();

        assertEquals(0, orderService.allSellOrders.size());
    }

    @Test
    @DisplayName("Retrieving order with id 4")
    void getOrderById() {
        orderService.initialiseOrders();

        Orders targetOrder = orderService.getOrderById(4);
        assertAll(
                () -> assertNotNull(targetOrder),
                () -> assertEquals(10, targetOrder.getQuantity()),
                () -> assertEquals(4, targetOrder.getId())
        );
    }

    @Test
    @DisplayName("Removing order with id 1")
    void removeOrderById() {
        orderService.initialiseOrders();

        Orders existingOrder = orderService.getOrderById(1);
        assertNotNull(existingOrder);

        orderService.removeOrderById(1);

        if (existingOrder.getSide() == Side.BUY) {
            Queue<Orders> queueAtPrice = orderService.orderBook.getBuyOrders().get(existingOrder.getPrice());
            // queue might be null if it was the only order at that price
            if (queueAtPrice != null) {
                assertNull(orderService.getOrderById(1));
                assertFalse(queueAtPrice.contains(existingOrder), "Order should be removed from the queue");
            }
        } else {
            Queue<Orders> queueAtPrice = orderService.orderBook.getSellOrders().get(existingOrder.getPrice());
            // queue might be null if it was the only order at that price
            if (queueAtPrice != null) {
                assertNull(orderService.getOrderById(1));
                assertFalse(queueAtPrice.contains(existingOrder), "Order should be removed from the queue");
            }
        }
    }

    @Test
    @DisplayName("Modifying quantity of order with id 2")
    void modifyOrderById() {
        OrderConstraints orderConstraints = new OrderConstraints();
        OrderService orderService = new OrderService(orderConstraints);
        orderService.allOrders.clear();

        orderService.initialiseOrders();

        Orders targetOrder = orderService.getOrderById(2);
        assertAll(
                () -> assertEquals(5, targetOrder.getQuantity()),
                () -> assertEquals(2, targetOrder.getId())
        );

        orderService.modifyOrderById(2, 10);
        assertAll(
                () -> assertEquals(10, targetOrder.getQuantity()),
                () -> assertEquals(2, targetOrder.getId())
        );
    }

    @Test
    @DisplayName("Test printOrderBook() - just ensure it runs without error")
    void printOrderBook() {
        orderService.initialiseOrders();
        assertDoesNotThrow(() -> orderService.printOrderBook(orderService.orderBook));
    }
}