package com.oswaldvalois.canteenorder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/** RecyclerView adapter that renders saved orders as avatar cards. */
public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    public interface OnOrderClickListener {
        void onOrderClick(Order order);
    }

    private final List<Order> orders = new ArrayList<>();
    private final OnOrderClickListener listener;
    private final SimpleDateFormat dateFormat =
            new SimpleDateFormat("dd MMM yyyy, HH:mm", new Locale("id", "ID"));

    public OrderAdapter(OnOrderClickListener listener) {
        this.listener = listener;
    }

    public void submit(List<Order> newOrders) {
        orders.clear();
        orders.addAll(newOrders);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orders.get(position);
        holder.initials.setText(initialsOf(order.getCustomer()));
        holder.customer.setText(order.getCustomer());
        holder.total.setText(Money.rupiah(order.getTotal()));
        holder.menu.setText(order.getMenu() + " × " + order.getQuantity());
        holder.toppings.setText(order.getToppings());
        holder.time.setText(dateFormat.format(new Date(order.getCreatedAt())));
        holder.itemView.setOnClickListener(v -> listener.onOrderClick(order));
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

    @Override
    public int getItemCount() {
        return orders.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {

        final TextView initials;
        final TextView customer;
        final TextView total;
        final TextView menu;
        final TextView toppings;
        final TextView time;

        OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            initials = itemView.findViewById(R.id.textInitials);
            customer = itemView.findViewById(R.id.textCustomer);
            total = itemView.findViewById(R.id.textTotal);
            menu = itemView.findViewById(R.id.textMenu);
            toppings = itemView.findViewById(R.id.textToppings);
            time = itemView.findViewById(R.id.textTime);
        }
    }
}
