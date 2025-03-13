package Services;

import BusinessRules.OrderConstraints;
import com.Entities.OrderBook;
import com.Entities.Orders;
import com.Entities.Side;
import com.sun.tools.jconsole.JConsoleContext;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

public class OrderService {
    private final OrderConstraints orderConstraints;


    protected List<Orders> allOrders = new LinkedList<>();
    protected List<Orders> invalidOrders = new ArrayList<>();

//    protected HashMap<BigDecimal, Queue<Orders>> buyOrders = new HashMap<BigDecimal, Queue<Orders>>();
    protected List<Orders> allBuyOrders = new ArrayList<>();

//    protected HashMap<BigDecimal, Queue<Orders>> sellOrders = new HashMap<BigDecimal, Queue<Orders>>();
    protected List<Orders> allSellOrders = new ArrayList<>();

    protected Map<BigDecimal, Orders> orderById = new HashMap<>();

    protected OrderBook orderBook = new OrderBook(
            new HashMap<BigDecimal, Queue<Orders>>(),
            new HashMap<BigDecimal, Queue<Orders>>()
    );

    public OrderService(OrderConstraints orderConstraints) {
        this.orderConstraints = orderConstraints;
    }

    // getAllOrders & initialiseOrders & handleOrders are all in charge of ensuring that there are orders to add to the order  book
    public void getAllOrders() {
        allOrders.add(new Orders(1, 1.00, 10, Side.BUY, LocalDateTime.now(), LocalDateTime.now().plusSeconds(1))); // Add one second on last update otherwise checks fail
        allOrders.add(new Orders(2, 1.00, 5, Side.BUY, LocalDateTime.now(), LocalDateTime.now().plusDays(1)));
        allOrders.add(new Orders(3, 1.00, 15, Side.BUY, LocalDateTime.now(), LocalDateTime.now().minusHours(1))); // This should be invalid
        allOrders.add(new Orders(4, 1.00, 5, Side.SELL, LocalDateTime.now(), LocalDateTime.now().plusMinutes(30)));
        allOrders.add(new Orders(5, 1.50, 12, Side.SELL, LocalDateTime.now(), LocalDateTime.now().plusDays(2)));
        allOrders.add(new Orders(6, 0.00, 10, Side.BUY, LocalDateTime.now(), LocalDateTime.now().plusDays(2))); // Invalid
        allOrders.add(new Orders(7, 1.25, 0, Side.BUY, LocalDateTime.now(), LocalDateTime.now().plusDays(2))); // Invalid
        allOrders.add(new Orders(8, -1.00, 10, Side.BUY, LocalDateTime.now(), LocalDateTime.now().plusSeconds(10))); // Invalid
        allOrders.add(new Orders(9, 2.00, 20, Side.SELL, LocalDateTime.now().plusDays(5), LocalDateTime.now().plusDays(4))); // Invalid
        allOrders.add(new Orders(10, 1.00, 20, Side.BUY, LocalDateTime.now(), LocalDateTime.now().plusHours(2)));
    }
    public void initialiseOrders() {
        getAllOrders();

        for (Orders order : allOrders) {
            if (order.getPrice().compareTo(BigDecimal.ZERO) > 0 && order.getQuantity() > orderConstraints.VALID_ORDER_QUANTITY && order.getOrderDate().isBefore(order.getOrderDate().plusDays(orderConstraints.VALID_ORDER_DATE_IN_FUTURE)) && order.getOrderDate().isBefore(order.getLastUpdated())) {
                orderById.put(new BigDecimal(order.getId()), order);
            } else {
                invalidOrders.add(order);
            }
        }
        allOrders.removeAll(invalidOrders);
        handleOrders();
    }
    public void handleOrders() {
        if (allOrders.size() <= 0) {
            throw new IllegalArgumentException("There are no orders to handle");
        }

        for (Orders order : allOrders) {
            if (order.getSide().equals(Side.BUY)) {
                allBuyOrders.add(order);
            } else {
                allSellOrders.add(order);
            }
        }

        addBuyOrder(orderBook.buyOrders, allBuyOrders);
        addSellOrder(orderBook.sellOrders, allSellOrders);
    }

    // There methods ensure that the orders are added to their respective hashmaps
    private void setupOrderBook(HashMap<BigDecimal, Queue<Orders>> orderTable, List<Orders> orderDetails) {
        for (Orders order : orderDetails) {
            if (orderTable.containsKey(order.getPrice())) {
                orderTable.get(order.getPrice()).add(order);
            } else {
                Queue<Orders> ordersQueue = new LinkedList<>();
                ordersQueue.add(order);
                orderTable.put(order.getPrice(), ordersQueue);
            }
        }
    }

    // Functionality necessary for the assessment, missing modify
    public void addBuyOrder(HashMap<BigDecimal, Queue<Orders>> orderTable, List<Orders> orderDetails) {
        setupOrderBook(orderTable, orderDetails);
    }
    public void addSellOrder(HashMap<BigDecimal, Queue<Orders>> orderTable, List<Orders> orderDetails) {
        setupOrderBook(orderTable, orderDetails);
    }
    public Orders getOrderById(int id) {
        Orders targetOrder = orderById.get(new BigDecimal(id));
        if (targetOrder == null) {
            System.out.println("Order not found");
        }
        return targetOrder;
    }
    public void removeOrderById(int id) {
        Orders orderToRemove = getOrderById(id);

        Queue<Orders> targetQueue;
        if (orderToRemove.getSide() == Side.BUY) {
            targetQueue = orderBook.buyOrders.get(orderToRemove.getPrice());
            targetQueue.remove(orderToRemove);
            if(targetQueue.isEmpty()) {
                orderBook.buyOrders.remove(orderToRemove.getPrice());
            }
        } else {
            targetQueue = orderBook.sellOrders.get(orderToRemove.getPrice());
            targetQueue.remove(orderToRemove);
            if(targetQueue.isEmpty()) {
                orderBook.sellOrders.remove(orderToRemove.getPrice());
            }
        }

        targetQueue.remove(orderToRemove);
    }

    public void modifyOrderById(int id, int quantity) {
        Orders orderToModify = getOrderById(id);
        if(!Objects.equals(new BigDecimal(orderToModify.getId()), new BigDecimal(id))) {
            // TODO This should contain logging info
            System.out.println("Order not found" + orderToModify.getId());
        }

        orderToModify.setQuantity(quantity);
        orderToModify.setLastUpdated(LocalDateTime.now());

        if(orderToModify.getSide() == Side.BUY) {
            Queue<Orders> targetQueue = orderBook.buyOrders.get(orderToModify.getPrice());
            targetQueue.remove(orderToModify);
            targetQueue.add(orderToModify);
        }else {
            Queue<Orders> targetQueue = orderBook.sellOrders.get(orderToModify.getPrice());
            targetQueue.remove(orderToModify);
            targetQueue.add(orderToModify);
        }
    }


    // Additional functionality to see what the hashmap looks like
    public void printOrderBook(){
        Map.Entry<BigDecimal, Queue<Orders>> buyEntry = orderBook.buyOrders.entrySet().iterator().next();
        Map.Entry<BigDecimal, Queue<Orders>> sellEntry = orderBook.sellOrders.entrySet().iterator().next();

        buyEntry.getValue().forEach(order -> {
           System.out.println("\nBUY ORDER: " +
                   "\nId: " + order.getId() +
                   "\nBuying price: " + order.getPrice() +
                   "\nSide: " + order.getSide() +
                   "\nQuantity: " + order.getQuantity() +
                   "\nOrderDate: " + order.getOrderDate() +
                   "\nLastUpdated: " + order.getLastUpdated());
        });

        System.out.println();

        sellEntry.getValue().forEach(order -> {
            System.out.println("\nSELL ORDER: " +
                    "\nId: " + order.getId() +
                    "\nBuying price: " + order.getPrice() +
                    "\nSide: " + order.getSide() +
                    "\nQuantity: " + order.getQuantity() +
                    "\nOrderDate: " + order.getOrderDate() +
                    "\nLastUpdated: " + order.getLastUpdated());
        });

        // System.out.println("Sell Orders : " + orderBook.getSellOrders());
    }
}
