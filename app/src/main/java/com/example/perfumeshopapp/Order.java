package com.example.perfumeshopapp;

import java.util.List;

public class Order {
    private long orderId;
    private long orderDate;
    private List<PerfumeItem> items;

    public Order(long orderId, long orderDate, List<PerfumeItem> items) {
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.items = items;
    }

    public long getOrderId() {
        return orderId;
    }

    public long getOrderDate() {
        return orderDate;
    }

    public List<PerfumeItem> getItems() {
        return items;
    }
}
