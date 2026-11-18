package com.oswaldvalois.canteenorder;

/** A saved canteen order, mirroring one row of the SQLite table. */
public class Order {

    private final long id;
    private final String customer;
    private final String menu;
    private final String toppings;
    private final int quantity;
    private final int total;
    private final long createdAt;

    public Order(long id, String customer, String menu, String toppings,
                 int quantity, int total, long createdAt) {
        this.id = id;
        this.customer = customer;
        this.menu = menu;
        this.toppings = toppings;
        this.quantity = quantity;
        this.total = total;
        this.createdAt = createdAt;
    }

    public long getId() {
        return id;
    }

    public String getCustomer() {
        return customer;
    }

    public String getMenu() {
        return menu;
    }

    public String getToppings() {
        return toppings;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getTotal() {
        return total;
    }

    public long getCreatedAt() {
        return createdAt;
    }
}
