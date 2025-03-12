package com.Entities;

import java.math.BigDecimal;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

public class OrderBook {
    public ConcurrentHashMap<BigDecimal, Queue<Orders>> buyOrders;
    public ConcurrentHashMap<BigDecimal, Queue<Orders>> sellOrders;

    public OrderBook(ConcurrentHashMap<BigDecimal, Queue<Orders>> buyOrders, ConcurrentHashMap<BigDecimal, Queue<Orders>> sellOrders) {
        this.buyOrders = buyOrders;
        this.sellOrders = sellOrders;
    }

    public ConcurrentHashMap<BigDecimal, Queue<Orders>> getBuyOrders() {
        return buyOrders;
    }

    public ConcurrentHashMap<BigDecimal, Queue<Orders>> getSellOrders() {
        return sellOrders;
    }
}
