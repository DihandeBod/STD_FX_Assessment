package com.fx_assessment;

import BusinessRules.OrderConstraints;
import Services.OrderService;


public class Main {
    protected static OrderConstraints orderConstraints = new OrderConstraints();
    protected static OrderService orderService = new OrderService(orderConstraints);

    public static void main(String[] args) throws Exception {
        orderService.initialiseOrders();

        orderService.printOrderBook();
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
        orderService.printOrderBook();
    }
}