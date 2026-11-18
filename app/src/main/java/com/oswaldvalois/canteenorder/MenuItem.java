package com.oswaldvalois.canteenorder;

/** One row of the canteen menu: a name and a price in rupiah. */
public class MenuItem {

    private final String name;
    private final int price;

    public MenuItem(String name, int price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return name + " — " + Money.rupiah(price);
    }
}
