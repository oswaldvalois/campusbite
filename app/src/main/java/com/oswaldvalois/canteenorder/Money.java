package com.oswaldvalois.canteenorder;

import java.util.Locale;

/** Small helper so every screen formats rupiah the same way. */
public final class Money {

    private Money() {
    }

    public static String rupiah(int amount) {
        return "Rp" + String.format(Locale.US, "%,d", amount).replace(',', '.');
    }
}
