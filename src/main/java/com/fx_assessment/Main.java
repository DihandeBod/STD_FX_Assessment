package com.fx_assessment;

import BusinessRules.OrderConstraints;
import Services.OrderService;
import com.Entities.Orders;
import com.Entities.Side;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;

public class Main {
    public static void main(String[] args) {
        OrderConstraints orderConstraints = new OrderConstraints();
        OrderService orderService = new OrderService(orderConstraints);

        List<Orders> validOrders = orderService.initialiseOrders();
        List<Orders> allBuyOrders = new ArrayList<>();
        List<Orders> allSellOrders = new ArrayList<>();
        HashMap<BigDecimal, Queue<Orders>> buyOrders = new HashMap<BigDecimal, Queue<Orders>>();
        HashMap<BigDecimal, Queue<Orders>> sellOrders = new HashMap<BigDecimal, Queue<Orders>>();


        // Before placing an order, you have to have checks first:
        for (Orders order : validOrders) {
            if (order.getSide().equals(Side.BUY)) {
                allBuyOrders.add(order);
            } else {
                allSellOrders.add(order);
            }
        }

        orderService.addBuyOrder(buyOrders, allBuyOrders);
        orderService.addSellOrder(sellOrders, allSellOrders);
    }
}