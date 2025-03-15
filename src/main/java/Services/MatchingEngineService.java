package Services;

import BusinessRules.OrderConstraints;
import com.Entities.OrderBook;
import com.Entities.Orders;
import com.Entities.Side;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

public class MatchingEngineService {

    private final OrderBook orderBook;
    private Map<BigDecimal, Orders> orderIdMap;
    private final OrderService orderService;
    protected boolean orderFulfilled = false;

    public MatchingEngineService(OrderBook orderBook, Map<BigDecimal, Orders> orderById, OrderService orderService) {
        this.orderBook = orderBook;
        this.orderIdMap = orderById;
        this.orderService = orderService;
    }

    public boolean fulfillOrder(Orders incomingOrder) {

        // TODO: Think about this, does it make sense to try and match the order outside the try-catch or should it be done within the if statements
        matchingOrder(incomingOrder);

        try {
            if (incomingOrder.getSide() == Side.BUY) {
                // TODO: Complete statement for the SellOrders in the order book

                orderFulfilled = true;
            } else {
                // TODO: Complete statement for the BuyOrders in the order book

                orderFulfilled = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            updateOrderbook();
        }

        return orderFulfilled;
    }

    public void matchingOrder(Orders incomingOrder){
        // TODO: Match order here to orders in the order book

    }

    public void updateOrderbook(){
        // TODO: Update order book to display orders that remain.
    }
}
