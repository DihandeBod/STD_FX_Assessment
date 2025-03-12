package com.fx_assessment;

import BusinessRules.OrderConstraints;
import Services.OrderService;


public class Main {
    protected static OrderConstraints orderConstraints = new OrderConstraints();
    protected static OrderService orderService = new OrderService(orderConstraints);

    public static void main(String[] args) throws Exception {
        orderService.initialiseOrders();
        try {
            orderService.getOrderById(1);
            orderService.removeOrderById(1);
            orderService.removeOrderById(4);
            orderService.removeOrderById(5);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        orderService.printOrderBook();
    }
}