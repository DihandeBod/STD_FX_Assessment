package Services;

import BusinessRules.OrderConstraints;
import com.Entities.OrderBook;
import com.Entities.Orders;
import com.Entities.Side;
import com.fx_assessment.Main;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class OrderService {
    private final OrderConstraints orderConstraints;
    protected final List<Orders> allOrders = new LinkedList<>();
    protected final List<Orders> invalidOrders = new ArrayList<>();
    protected final List<Orders> allBuyOrders = new ArrayList<>();
    protected final List<Orders> allSellOrders = new ArrayList<>();
    protected final Map<BigDecimal, Orders> orderIdMap = new HashMap<>();
    protected final OrderBook orderBook = new OrderBook(new HashMap<>(), new HashMap<>());
    protected MatchingEngineService matchingEngineService;

    public OrderService(OrderConstraints orderConstraints) {
        this.orderConstraints = orderConstraints;
    }

    public void setMatchingEngineService(MatchingEngineService matchingEngineService) {
        this.matchingEngineService = matchingEngineService;
    }

    public OrderBook getOrderBook() {
        return this.orderBook;
    }

    public Map<BigDecimal, Orders> getOrderIdMap() {
        return this.orderIdMap;
    }


    // getAllOrders & initialiseOrders & handleOrders are all in charge of ensuring that there are orders to add to the order  book
    public void getAllOrders() {
        allOrders.add(new Orders(1, 1.00, 10, Side.BUY, LocalDateTime.now(), LocalDateTime.now(), false));
        allOrders.add(new Orders(2, 1.00, 5, Side.BUY, LocalDateTime.now(), LocalDateTime.now().plusDays(1), false));
        allOrders.add(new Orders(3, 1.00, 15, Side.BUY, LocalDateTime.now(), LocalDateTime.now().plusHours(1), false));
        allOrders.add(new Orders(4, 1.25, 10, Side.BUY, LocalDateTime.now(), LocalDateTime.now().plusDays(2), false));
        allOrders.add(new Orders(5, 1.25, 16, Side.BUY, LocalDateTime.now(), LocalDateTime.now().plusDays(2), false));
        allOrders.add(new Orders(6, 1.00, 10, Side.BUY, LocalDateTime.now(), LocalDateTime.now().plusSeconds(10), false));
        allOrders.add(new Orders(7, 1.00, 20, Side.BUY, LocalDateTime.now(), LocalDateTime.now().plusHours(2), false));

        allOrders.add(new Orders(8, 2.00, 5, Side.SELL, LocalDateTime.now(), LocalDateTime.now().plusMinutes(30), false));
        allOrders.add(new Orders(9, 2.00, 12, Side.SELL, LocalDateTime.now(), LocalDateTime.now().plusDays(2), false));
        allOrders.add(new Orders(10, 2.00, 20, Side.SELL, LocalDateTime.now().plusDays(5), LocalDateTime.now().plusDays(5), false));
    }

    public void initialiseOrders() {
        getAllOrders();

        for (Orders order : allOrders) {
            if (order.getPrice().compareTo(BigDecimal.ZERO) > 0 && order.getQuantity() > orderConstraints.VALID_ORDER_QUANTITY && order.getOrderDate().isBefore(order.getOrderDate().plusDays(orderConstraints.VALID_ORDER_DATE_IN_FUTURE)) && (order.getOrderDate().isBefore(order.getLastUpdated()) || order.getOrderDate().isEqual(order.getLastUpdated()))) {
                orderIdMap.put(new BigDecimal(order.getId()), order);
            } else {
                invalidOrders.add(order);
            }
        }
        allOrders.removeAll(invalidOrders);
        handleOrders(allOrders);
    }

    public void handleOrders(List<Orders> ordersToHandle) {
        if (ordersToHandle == null || ordersToHandle.isEmpty()) {
            Main.LOGGER.log(Level.WARNING, "No orders found");
        }

        assert ordersToHandle != null;
        for (Orders order : ordersToHandle) {
            if (order.getSide().equals(Side.BUY)) {
                allBuyOrders.add(order);
            } else {
                allSellOrders.add(order);
            }
        }

        addBuyOrder(orderBook.buyOrders, allBuyOrders);
        addSellOrder(orderBook.sellOrders, allSellOrders);

        allBuyOrders.clear();
        allSellOrders.clear();
    }


    // These methods ensure that the orders are added to their respective hashmaps
    private void setupOrderBook(HashMap<BigDecimal, Queue<Orders>> orderTable, List<Orders> orderDetails) {
        for (Orders order : orderDetails) {
            boolean wasMatched = false;

            if (matchingEngineService != null) {
                wasMatched = matchingEngineService.fulfillOrder(order);
            }

            if (!wasMatched) {
                if (orderTable.containsKey(order.getPrice())) {
                    orderTable.get(order.getPrice()).add(order);
                } else {
                    Queue<Orders> ordersQueue = new LinkedList<>();
                    ordersQueue.add(order);
                    orderTable.put(order.getPrice(), ordersQueue);
                }
            }
        }
        Main.LOGGER.log(Level.INFO, "Order book has been setup");
    }

    // Functionality necessary for the assessment, missing modify
    public void addBuyOrder(HashMap<BigDecimal, Queue<Orders>> orderTable, List<Orders> orderDetails) {
        setupOrderBook(orderTable, orderDetails);
    }

    public void addSellOrder(HashMap<BigDecimal, Queue<Orders>> orderTable, List<Orders> orderDetails) {
        setupOrderBook(orderTable, orderDetails);
    }

    public void addOrderNoCheck(HashMap<BigDecimal, Queue<Orders>> orderTable, List<Orders> orderDetails) {
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

    public Orders getOrderById(int id) {
        Orders targetOrder = orderIdMap.get(new BigDecimal(id));
        if (targetOrder == null) {
            Main.LOGGER.log(Level.WARNING, "Order not found");
        }
        return targetOrder;
    }

    public void removeOrderById(int id) {
        Orders orderToRemove = getOrderById(id);
        if (orderToRemove == null) return; // Safety check, as getOrderById() has a null check

        Queue<Orders> targetQueue;
        if (orderToRemove.getSide() == Side.BUY) {
            targetQueue = orderBook.buyOrders.get(orderToRemove.getPrice());
            targetQueue.remove(orderToRemove);
            if (targetQueue.isEmpty()) {
                orderBook.buyOrders.remove(orderToRemove.getPrice());
            }
        } else {
            targetQueue = orderBook.sellOrders.get(orderToRemove.getPrice());
            targetQueue.remove(orderToRemove);
            if (targetQueue.isEmpty()) {
                orderBook.sellOrders.remove(orderToRemove.getPrice());
            }
        }
        orderIdMap.remove(new BigDecimal(id));
        targetQueue.remove(orderToRemove);
        Main.LOGGER.log(Level.INFO, "Order removed");
    }

    public void modifyOrderById(int id, int quantity) {
        Orders orderToModify = getOrderById(id);
        if (orderToModify == null) return;

        orderToModify.setQuantity(quantity);
        orderToModify.setLastUpdated(LocalDateTime.now());

        Queue<Orders> targetQueue;
        if (orderToModify.getSide() == Side.BUY) {
            targetQueue = orderBook.buyOrders.get(orderToModify.getPrice());
        } else {
            targetQueue = orderBook.sellOrders.get(orderToModify.getPrice());
        }

        if (targetQueue != null) {
            targetQueue.remove(orderToModify);
            targetQueue.add(orderToModify);
        }
        Main.LOGGER.log(Level.INFO, "Order modified");
    }


    // Additional functionality to see what the hashmap looks like
    public void printOrderBook(OrderBook orderBookToPrint) {
        System.out.println("#########################################");
        System.out.println("############### BUY ORDER ###############");
        System.out.println("#########################################");
        System.out.println();

        orderBookToPrint.getBuyOrders().entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    // For each price key, collect the quantities of all orders into one comma-separated string
                    String quantities = entry.getValue().stream()
                            .map(order -> order.getId() + ":" + order.getQuantity())
                            .collect(Collectors.joining(", "));

                    System.out.println("KEY: " + entry.getKey() + " \t--> " + quantities);
                });

        System.out.println("\n##########################################");
        System.out.println("############### SELL ORDER ###############");
        System.out.println("##########################################\n");

        orderBookToPrint.getSellOrders().entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    String quantities = entry.getValue().stream()
                            .map(order -> order.getId() + ":" + order.getQuantity())
                            .collect(Collectors.joining(", "));

                    System.out.println("KEY: " + entry.getKey() + " \t--> " + quantities);
                });
    }

    public void cleanUpOrderBook() {
        Set<BigDecimal> buyKeySet = orderBook.buyOrders.keySet();
        Set<BigDecimal> sellKeySet = orderBook.sellOrders.keySet();

        for (BigDecimal price : new ArrayList<>(buyKeySet)) {
            if (orderBook.buyOrders.get(price).isEmpty()) {
                orderBook.buyOrders.remove(price);
            }
        }

        for (BigDecimal price : new ArrayList<>(sellKeySet)) {
            if (orderBook.sellOrders.get(price).isEmpty()) {
                orderBook.sellOrders.remove(price);
            }
        }
    }

}
