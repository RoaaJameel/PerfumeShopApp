package com.example.perfumeshopapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private Context context;
    private List<PerfumeItem> cartList;
    private Runnable onCartUpdated; // لتحديث السعر الكلي في Activity
    private OnItemRemoveListener removeListener;

    public interface OnItemRemoveListener {
        void onItemRemoveRequested(int position);
    }

    public CartAdapter(Context context, List<PerfumeItem> cartList, Runnable onCartUpdated, OnItemRemoveListener removeListener) {
        this.context = context;
        this.cartList = cartList;
        this.onCartUpdated = onCartUpdated;
        this.removeListener = removeListener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        PerfumeItem item = cartList.get(position);
        holder.name.setText(item.getName());
        holder.brand.setText(item.getBrand());
        holder.price.setText("$" + item.getPrice());
        holder.image.setImageResource(item.getImageResId());
        holder.quantity.setText("Quantity: " + item.getQuantity());

        holder.increaseQuantityBtn.setOnClickListener(v -> {
            if (item.getQuantity() < item.getInStock()) {
                item.setQuantity(item.getQuantity() + 1);
                updateCartData();
                notifyItemChanged(position);
                onCartUpdated.run();
            } else {
                Toast.makeText(context, "Reached maximum stock available!", Toast.LENGTH_SHORT).show();
            }
        });

        holder.decreaseQuantityBtn.setOnClickListener(v -> {
            if (item.getQuantity() > 1) {
                item.setQuantity(item.getQuantity() - 1);
                updateCartData();
                notifyItemChanged(position);
                onCartUpdated.run();
            } else {
                if (removeListener != null) {
                    removeListener.onItemRemoveRequested(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView name, brand, price, quantity;
        ImageView image;
        Button decreaseQuantityBtn, increaseQuantityBtn;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.cartPerfumeName);
            brand = itemView.findViewById(R.id.cartPerfumeBrand);
            price = itemView.findViewById(R.id.cartPerfumePrice);
            quantity = itemView.findViewById(R.id.cartQuantityText);
            image = itemView.findViewById(R.id.cartPerfumeImage);
            decreaseQuantityBtn = itemView.findViewById(R.id.decreaseQuantityBtn);
            increaseQuantityBtn = itemView.findViewById(R.id.increaseQuantityBtn);
        }
    }

    private void updateCartData() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("PerfumeShop", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        JSONArray jsonArray = new JSONArray();

        for (PerfumeItem item : cartList) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("name", item.getName());
                jsonObject.put("brand", item.getBrand());
                jsonObject.put("price", item.getPrice());
                jsonObject.put("imageResId", item.getImageResId());
                jsonObject.put("inStock", item.getInStock());
                jsonObject.put("quantity", item.getQuantity());
                jsonObject.put("gender", item.getGender());
                jsonObject.put("type", item.getType());
                jsonObject.put("isLongLasting", item.isLongLasting());
                jsonObject.put("isStrongScent", item.isStrongScent());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            jsonArray.put(jsonObject);
        }

        editor.putString("cart", jsonArray.toString());
        editor.apply();
    }
}
