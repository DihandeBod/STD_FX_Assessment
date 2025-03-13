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
            orderService.modifyOrderById(2, 10);
            orderService.modifyOrderById(10, 1);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        System.out.println("########################### After modifications ###########################");
        orderService.printOrderBook();
    }
}