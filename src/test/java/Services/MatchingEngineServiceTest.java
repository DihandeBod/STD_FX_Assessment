package Services;

import BusinessRules.OrderConstraints;
import com.Entities.Orders;
import com.Entities.Side;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import static org.junit.jupiter.api.Assertions.*;

class MatchingEngineServiceTest {

    private final OrderConstraints orderConstraints = new OrderConstraints();
    private final OrderService orderService = new OrderService(orderConstraints);
    private final MatchingEngineService matchingEngineService = new MatchingEngineService(orderService.orderBook, orderService.orderIdMap, orderService);

    @BeforeEach
    void setUp() {
        matchingEngineService.orderBook.buyOrders.clear();
        matchingEngineService.orderBook.sellOrders.clear();
        matchingEngineService.orderIdMap.clear();

        orderService.initialiseOrders();
        orderService.handleOrders(orderService.allOrders);
    }

    @Test
    void fulfillOrder() {
        List<Orders> incomingOrdersToMatch = new ArrayList<>();
        incomingOrdersToMatch.add(new Orders(12, 1.00, 57, Side.SELL, LocalDateTime.now(), LocalDateTime.now().plusDays(1), false));
        orderService.handleOrders(incomingOrdersToMatch);

        Orders orderToMatch = incomingOrdersToMatch.get(0);

        assertTrue(matchingEngineService.fulfillOrder(orderToMatch));
        assertTrue(matchingEngineService.orderBook.buyOrders.containsKey(orderToMatch.getPrice()));
    }

    @Test
    void matchingOrder() {
        // Check the length of the queue the method returns
        List<Orders> incomingOrdersToMatch = new ArrayList<>();
        incomingOrdersToMatch.add(new Orders(12, 1.00, 57, Side.SELL, LocalDateTime.now(), LocalDateTime.now().plusDays(1), false));
        orderService.handleOrders(incomingOrdersToMatch);

        Orders orderToMatch = incomingOrdersToMatch.get(0);
        Queue<Orders> targetQueue = matchingEngineService.matchingOrder(orderToMatch);
        assertNotNull(targetQueue, "target queue is null");

        assertFalse(targetQueue.isEmpty(), "matching order should not be empty");
    }
}