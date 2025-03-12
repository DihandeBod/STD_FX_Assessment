package com.Entities;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Queue;

public class OrderBook {
    public HashMap<BigDecimal, Queue<Orders>> buyOrders;
    public HashMap<BigDecimal, Queue<Orders>> sellOrders;

    public OrderBook(HashMap<BigDecimal, Queue<Orders>> buyOrders, HashMap<BigDecimal, Queue<Orders>> sellOrders) {
        this.buyOrders = buyOrders;
        this.sellOrders = sellOrders;
    }

    public HashMap<BigDecimal, Queue<Orders>> getBuyOrders() {
        return buyOrders;
    }

    public HashMap<BigDecimal, Queue<Orders>> getSellOrders() {
        return sellOrders;
    }
}
