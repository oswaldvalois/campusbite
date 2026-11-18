package com.oswaldvalois.canteenorder;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        applySystemBarInsets(R.id.root);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        db = new OrderDbHelper(this);
        long orderId = getIntent().getLongExtra(EXTRA_ORDER_ID, -1L);
        order = db.getById(orderId);
        if (order == null) {
            finish();
            return;
        }

        bindOrder(order);
        findViewById(R.id.buttonDelete).setOnClickListener(v -> deleteOrder());
    }

    private void bindOrder(Order order) {
        ((TextView) findViewById(R.id.detailCustomer)).setText(order.getCustomer());
        ((TextView) findViewById(R.id.detailMenu)).setText(order.getMenu());
        ((TextView) findViewById(R.id.detailToppings)).setText(order.getToppings());
        ((TextView) findViewById(R.id.detailPortion))
                .setText(getString(R.string.portion_value, order.getQuantity()));
        ((TextView) findViewById(R.id.detailTotal)).setText(Money.rupiah(order.getTotal()));
        ((TextView) findViewById(R.id.detailTime))
                .setText(dateFormat.format(new Date(order.getCreatedAt())));
    }

    private void deleteOrder() {
        db.delete(order.getId());
        Toast.makeText(this, R.string.order_deleted, Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    protected void onDestroy() {
        db.close();
        super.onDestroy();
    }
}
