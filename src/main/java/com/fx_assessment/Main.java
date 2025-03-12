package com.fx_assessment;

import com.Entities.Orders;
import com.Entities.Side;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Queue;
import Services.OrderService;
import BusinessRules.Rules;

public class Main {
    public static void main(String[] args) {
        OrderService orderService = new OrderService();
        Rules rules = new Rules();

        List<Orders> allOrders = new ArrayList<Orders>();
        Hashtable<BigDecimal, Queue<Orders>> buyOrders = new Hashtable<BigDecimal, Queue<Orders>>();
        Hashtable<BigDecimal, Queue<Orders>> sellOrders = new Hashtable<BigDecimal, Queue<Orders>>();

        allOrders.add(new Orders(1, 1.00, 10, Side.BUY, LocalDateTime.now(), LocalDateTime.now().plusSeconds(1))); // Add one second on last update otherwise checks fail
        allOrders.add(new Orders(2, 1.50, 5, Side.BUY, LocalDateTime.now(), LocalDateTime.now().plusDays(1)));
        allOrders.add(new Orders(3, 1.00, 15, Side.DRAFT, LocalDateTime.now(), LocalDateTime.now().minusHours(1))); // This should be invalid


        // Before placing an order, you have to have checks first:
        for (Orders order : allOrders) {
            if (order.Price.compareTo(BigDecimal.ZERO) > 0 && order.Quantity > 0 &&
                    order.OrderDate.isBefore(order.OrderDate.plusDays(rules.VALID_ORDER_DATE_IN_FUTURE)) &&
                    order.OrderDate.isBefore(order.LastUpdated)) {

                if (order.Side.equals(Side.BUY)) {
                    //orderService.addBuyOrder();
                } else {
                    //orderService.addSellOrder();
                }
            }


            //orderService.addBuyOrder(buyOrders, allOrders);


            System.out.println("Hello, World!");
        }
    }
}