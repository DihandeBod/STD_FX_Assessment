package com.fx_assessment;

import BusinessRules.OrderConstraints;
import Services.MatchingEngineService;
import Services.OrderService;


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
        orderService.printOrderBook(orderService.getOrderBook());
        try {
            orderService.removeOrderById(1);
            orderService.modifyOrderById(2, 10); // Key 1 BUY
            orderService.modifyOrderById(8, 1); // Key 1 SELL
            orderService.modifyOrderById(4, 100); // Key 1.25 BUY
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        System.out.println();
        System.out.println("###########################################################################");
        System.out.println("########################### After modifications ###########################");
        System.out.println("###########################################################################");
        System.out.println();
        // orderService.printOrderBook();
    }
}