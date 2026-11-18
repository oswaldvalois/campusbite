package com.oswaldvalois.canteenorder;

import android.content.Intent;
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
    private TextView textEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);
        applySystemBarInsets(R.id.root);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        db = new OrderDbHelper(this);
        recyclerOrders = findViewById(R.id.recyclerOrders);
        textEmpty = findViewById(R.id.textEmpty);

        adapter = new OrderAdapter(this::openDetail);
        recyclerOrders.setLayoutManager(new LinearLayoutManager(this));
        recyclerOrders.setAdapter(adapter);
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
        textEmpty.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        recyclerOrders.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }

    private void openDetail(Order order) {
        Intent intent = new Intent(this, OrderDetailActivity.class);
        intent.putExtra(OrderDetailActivity.EXTRA_ORDER_ID, order.getId());
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        db.close();
        super.onDestroy();
    }
}
