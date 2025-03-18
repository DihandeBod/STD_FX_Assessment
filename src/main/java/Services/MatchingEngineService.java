package Services;

import com.Entities.OrderBook;
import com.Entities.Orders;
import com.Entities.Side;
import com.fx_assessment.Main;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.logging.Level;

import static java.lang.Math.abs;

public class MatchingEngineService {

    protected final OrderBook orderBook;
    protected final OrderService orderService;
    protected final Map<BigDecimal, Orders> orderIdMap;

    public MatchingEngineService(OrderBook orderBook, Map<BigDecimal, Orders> orderById, OrderService orderService) {
        this.orderBook = orderBook;
        this.orderIdMap = orderById;
        this.orderService = orderService;
    }

    public boolean fulfillOrder(Orders incomingOrder) {
        if (incomingOrder.getQuantity() <= 0) {
            Main.LOGGER.log(Level.WARNING, "Order quantity is less than zero");
            return false;
        }

        // Basic variables for fulfilling an order
        int quantityRemaining = incomingOrder.getQuantity();
        Queue<Orders> targetQueueForIncomingOrder = matchingOrder(incomingOrder);
        if (targetQueueForIncomingOrder == null || targetQueueForIncomingOrder.isEmpty()) {
            Main.LOGGER.log(Level.WARNING, "The queue you are attempting to fulfill does not exist");
            return false;
        }

        for (Orders targetOrder : targetQueueForIncomingOrder) {
            if (quantityRemaining <= 0) {
                Main.LOGGER.log(Level.WARNING, "Incoming order: Order quantity is less or equal than zero");
                break;
            }

            int difference = targetOrder.getQuantity() - quantityRemaining;
            if (difference == 0) {
                // The order that was placed matches the quantity of an order that already exists, thus, remove order.
                quantityRemaining = matchEquals(incomingOrder, targetOrder, quantityRemaining);
            } else if (difference > 0) {
                // The order that was placed is for fewer quantity than the order that already existed, thus, DON'T remove order, just update quantity
                quantityRemaining = matchLessThan(incomingOrder, targetOrder);
            } else {
                // The order that was placed is for more quantity than the order that already existed, thus, Remove order and continue to next order to go through list for loop again.
                quantityRemaining = matchMoreThan(targetOrder, quantityRemaining);
            }
        }
        incomingOrder.setQuantity(quantityRemaining);
        removeCompletedOrders(targetQueueForIncomingOrder);

        if (quantityRemaining > 0) {
            List<Orders> leftoverList = new ArrayList<>();
            leftoverList.add(incomingOrder);
            if (incomingOrder.getSide() == Side.BUY) {
                orderService.addOrderNoCheck(orderBook.getBuyOrders(), leftoverList);
            } else {
                orderService.addOrderNoCheck(orderBook.getSellOrders(), leftoverList);
            }
        }
        Main.LOGGER.log(Level.INFO, "Fulfilled incoming orders");
        return true;
    }


    public Queue<Orders> matchingOrder(Orders incomingOrder) {
        BigDecimal targetPrice = incomingOrder.getPrice();

        if (incomingOrder.getSide() == Side.BUY) {
            return orderBook.getSellOrders().get(targetPrice);
        } else {
            return orderBook.getBuyOrders().get(targetPrice);
        }
    }


    private int matchEquals(Orders incomingOrder, Orders targetOrder, int quantityRemaining) {
        int remainingQuantity = quantityRemaining - targetOrder.getQuantity();
        targetOrder.setCompleted(true);
        incomingOrder.setCompleted(true);

        return remainingQuantity;
    }

    private int matchLessThan(Orders incomingOrder, Orders targetOrder) {
        int remainingQuantity = incomingOrder.getQuantity() - targetOrder.getQuantity();
        incomingOrder.setCompleted(true);
        if (remainingQuantity < 0) {
            targetOrder.setQuantity(abs(remainingQuantity));
            remainingQuantity = 0;
        }

        return remainingQuantity;
    }

    private int matchMoreThan(Orders targetOrder, int quantityRemaining) {
        int remainingQuantity = quantityRemaining - targetOrder.getQuantity();
        targetOrder.setCompleted(true);

        return remainingQuantity;
    }

    private void removeCompletedOrders(Queue<Orders> targetQueue) {
        for (Orders targetOrder : targetQueue) {
            if (targetOrder.getCompleted()) orderIdMap.remove(targetOrder.getPrice(), targetOrder);
        }
        targetQueue.removeIf(Orders::getCompleted);
        Main.LOGGER.log(Level.INFO, "Removed completed orders");
    }
}
