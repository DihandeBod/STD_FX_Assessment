package com.fx_assessment;

import BusinessRules.OrderConstraints;
import Services.MatchingEngineService;
import Services.OrderService;
import com.Entities.Orders;
import com.Entities.Side;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class Main {
    public static void main(String[] args) throws Exception {

        OrderConstraints orderConstraints = new OrderConstraints();

        OrderService orderService = new OrderService(orderConstraints);

        MatchingEngineService matchingEngineService = new MatchingEngineService(
                orderService.getOrderBook(),
                orderService.getOrderIdMap(),
                orderService
        );

        orderService.setMatchingEngineService(matchingEngineService);

        orderService.initialiseOrders();
        System.out.println("Order book BEFORE any modifications");
        orderService.printOrderBook(orderService.getOrderBook());
        System.out.println();
        System.out.println();
        try {
            orderService.removeOrderById(1);
            orderService.modifyOrderById(2, 10); // Key 1 BUY
            orderService.modifyOrderById(8, 1); // Key 1 SELL
            orderService.modifyOrderById(4, 100); // Key 1.25 BUY
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        System.out.println("Order book AFTER removals and modifications");
        orderService.printOrderBook(orderService.getOrderBook());
        System.out.println();
        System.out.println();

        System.out.println("Adding and matching new orders");
        List<Orders> incomingOrdersToMatch = new ArrayList<>();
        incomingOrdersToMatch.add(new Orders(12, 1.00, 57, Side.SELL, LocalDateTime.now(), LocalDateTime.now().plusDays(1), false));
        incomingOrdersToMatch.add(new Orders(11, 2.00, 35, Side.BUY, LocalDateTime.now(), LocalDateTime.now(), false));
        try {
            orderService.handleOrders(incomingOrdersToMatch);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        orderService.cleanUpOrderBook();

        System.out.println("Order book AFTER matching new orders");
        orderService.printOrderBook(orderService.getOrderBook());
    }
}