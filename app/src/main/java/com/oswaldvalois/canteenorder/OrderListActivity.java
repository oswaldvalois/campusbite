package com.oswaldvalois.canteenorder;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;

import java.util.List;

/** Shows every saved order, newest first. */
public class OrderListActivity extends BaseActivity {

    private OrderDbHelper db;
    private OrderAdapter adapter;
    private RecyclerView recyclerOrders;
    private View emptyState;
    private TextView textCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);
        applySystemBarInsets(R.id.root);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        db = new OrderDbHelper(this);
        recyclerOrders = findViewById(R.id.recyclerOrders);
        emptyState = findViewById(R.id.emptyState);
        textCount = findViewById(R.id.textCount);

        adapter = new OrderAdapter(this::openDetail);
        recyclerOrders.setLayoutManager(new LinearLayoutManager(this));
        recyclerOrders.setAdapter(adapter);

        findViewById(R.id.buttonCreateOrder).setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadOrders();
    }

    private void loadOrders() {
        List<Order> orders = db.getAll();
        adapter.submit(orders);
        boolean isEmpty = orders.isEmpty();
        emptyState.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        recyclerOrders.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        textCount.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        if (!isEmpty) {
            textCount.setText(getString(R.string.list_count, orders.size()));
        }
        if (isEmpty) {
            emptyState.setAlpha(0f);
            emptyState.animate().alpha(1f).setDuration(220).start();
        }
    }

    private void openDetail(Order order) {
        OrderDetailActivity.start(this, order.getId());
        overridePendingTransition(R.anim.slide_in_right, R.anim.fade_out);
    }

    @Override
    protected void onDestroy() {
        db.close();
        super.onDestroy();
    }
}
