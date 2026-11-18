package com.oswaldvalois.canteenorder;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/** Detail screen for one order, reached from the list through an Intent extra. */
public class OrderDetailActivity extends BaseActivity {

    public static final String EXTRA_ORDER_ID = "order_id";

    private final SimpleDateFormat dateFormat =
            new SimpleDateFormat("dd MMM yyyy, HH:mm", new Locale("id", "ID"));

    private OrderDbHelper db;
    private Order order;

    public static void start(Context context, long orderId) {
        Intent intent = new Intent(context, OrderDetailActivity.class);
        intent.putExtra(EXTRA_ORDER_ID, orderId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        applySystemBarInsets(R.id.root);

        db = new OrderDbHelper(this);
        long orderId = getIntent().getLongExtra(EXTRA_ORDER_ID, -1L);
        order = db.getById(orderId);
        if (order == null) {
            finish();
            return;
        }

        findViewById(R.id.buttonBack).setOnClickListener(v -> finish());
        bindOrder(order);
        findViewById(R.id.buttonDelete).setOnClickListener(v -> deleteOrder());
    }

    private void bindOrder(Order order) {
        ((TextView) findViewById(R.id.detailInitials)).setText(initialsOf(order.getCustomer()));
        ((TextView) findViewById(R.id.detailCustomer)).setText(order.getCustomer());
        ((TextView) findViewById(R.id.detailMenu)).setText(order.getMenu());
        ((TextView) findViewById(R.id.detailToppings)).setText(order.getToppings());
        ((TextView) findViewById(R.id.detailPortion))
                .setText(getString(R.string.portion_value, order.getQuantity()));
        ((TextView) findViewById(R.id.detailTotal)).setText(Money.rupiah(order.getTotal()));
        ((TextView) findViewById(R.id.detailTime))
                .setText(dateFormat.format(new Date(order.getCreatedAt())));
    }

    private static String initialsOf(String name) {
        String trimmed = name.trim();
        if (trimmed.isEmpty()) {
            return "?";
        }
        String[] parts = trimmed.split("\\s+");
        StringBuilder initials = new StringBuilder();
        initials.append(Character.toUpperCase(parts[0].charAt(0)));
        if (parts.length > 1 && !parts[1].isEmpty()) {
            initials.append(Character.toUpperCase(parts[1].charAt(0)));
        }
        return initials.toString();
    }

    private void deleteOrder() {
        int total = order.getTotal();
        db.delete(order.getId());
        Snackbar.make(findViewById(R.id.root),
                        getString(R.string.order_deleted) + "  ·  " + Money.rupiah(total),
                        Snackbar.LENGTH_SHORT)
                .show();
        finish();
    }

    @Override
    protected void onDestroy() {
        db.close();
        super.onDestroy();
    }
}
