package com.Entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Orders {
    private int Id;
    private BigDecimal Price;
    private int Quantity;
    private Side Side;
    private LocalDateTime OrderDate;
    private LocalDateTime LastUpdated;

    public Orders(int id, double price, int quantity, Side side, LocalDateTime orderDate, LocalDateTime lastUpdated) {
        Id = id;
        Price = new BigDecimal(price);
        Quantity = quantity;
        Side = side;
        OrderDate = orderDate;
        LastUpdated = lastUpdated;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public BigDecimal getPrice() {
        return Price;
    }

    public void setPrice(BigDecimal price) {
        Price = price;
    }

    public int getQuantity() {
        return Quantity;
    }

    public void setQuantity(int quantity) {
        Quantity = quantity;
    }

    public Side getSide() {
        return Side;
    }

    public void setSide(Side side) {
        Side = side;
    }

    public LocalDateTime getOrderDate() {
        return OrderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        OrderDate = orderDate;
    }

    public LocalDateTime getLastUpdated() {
        return LastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        LastUpdated = lastUpdated;
    }
}