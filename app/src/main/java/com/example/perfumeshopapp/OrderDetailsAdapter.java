package com.example.perfumeshopapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class OrderDetailsAdapter extends RecyclerView.Adapter<OrderDetailsAdapter.ViewHolder> {

    private Context context;
    private List<Order> orderList;

    public OrderDetailsAdapter(Context context, List<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_details, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order order = orderList.get(position);

        // لنفترض أن كل طلب يحتوي على منتج واحد فقط
        if (order.getItems() != null && !order.getItems().isEmpty()) {
            PerfumeItem item = order.getItems().get(0); // جلب المنتج الأول فقط

            holder.productNameTextView.setText(item.getName());
            holder.productBrandTextView.setText(item.getBrand());
            holder.productPriceTextView.setText("$" + item.getPrice());
            holder.quantityTextView.setText("Quantity: " + item.getQuantity());
            holder.productImageView.setImageResource(item.getImageResId());

            // تنسيق التاريخ والوقت
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault());
            String dateStr = sdf.format(order.getOrderDate());

            holder.orderIdTextView.setText("Order ID: " + order.getOrderId());
            holder.orderDateTextView.setText("Date: " + dateStr);
        }
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView productImageView;
        TextView productNameTextView, productBrandTextView, productPriceTextView, quantityTextView, orderIdTextView, orderDateTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productImageView = itemView.findViewById(R.id.productImageView);
            productNameTextView = itemView.findViewById(R.id.productNameTextView);
            productBrandTextView = itemView.findViewById(R.id.productBrandTextView);
            productPriceTextView = itemView.findViewById(R.id.productPriceTextView);
            quantityTextView = itemView.findViewById(R.id.quantityTextView);
            orderIdTextView = itemView.findViewById(R.id.orderIdTextView);
            orderDateTextView = itemView.findViewById(R.id.orderDateTextView);
        }
    }
}
