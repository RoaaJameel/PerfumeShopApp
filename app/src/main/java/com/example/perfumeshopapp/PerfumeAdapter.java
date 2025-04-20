package com.example.perfumeshopapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class PerfumeAdapter extends RecyclerView.Adapter<PerfumeAdapter.PerfumeViewHolder> {

    private Context context;
    private List<PerfumeItem> perfumeList;

    public PerfumeAdapter(Context context, List<PerfumeItem> perfumeList) {
        this.context = context;
        this.perfumeList = perfumeList;
    }

    public void setPerfumeList(List<PerfumeItem> newList) {
        this.perfumeList.clear();
        this.perfumeList.addAll(newList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PerfumeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_perfume, parent, false);
        return new PerfumeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PerfumeViewHolder holder, int position) {
        PerfumeItem item = perfumeList.get(position);

        holder.nameTextView.setText(item.getName());
        holder.brandTextView.setText(item.getBrand());
        holder.priceTextView.setText("$" + item.getPrice());
        holder.imageView.setImageResource(item.getImageResId());
        holder.stockTextView.setText("In stock: " + item.getInStock());

        holder.addToCartButton.setOnClickListener(v -> {
            addToCart(item, 1);
            Toast.makeText(context, item.getName() + " added to cart!", Toast.LENGTH_SHORT).show();
        });
    }

    private void addToCart(PerfumeItem item, int quantityToAdd) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("PerfumeShop", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String cartJson = sharedPreferences.getString("cart", "[]");

        JSONArray cartArray;
        try {
            cartArray = new JSONArray(cartJson);
        } catch (JSONException e) {
            cartArray = new JSONArray();
        }

        boolean found = false;

        for (int i = 0; i < cartArray.length(); i++) {
            try {
                JSONObject obj = cartArray.getJSONObject(i);
                if (obj.getString("name").equals(item.getName())) {
                    int currentQty = obj.getInt("quantity");
                    int newQty = currentQty + quantityToAdd;
                    if (newQty > item.getInStock()) {
                        newQty = item.getInStock();
                        Toast.makeText(context, "Reached maximum stock available!", Toast.LENGTH_SHORT).show();
                    }
                    obj.put("quantity", newQty);
                    found = true;
                    break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (!found) {
            JSONObject perfumeObject = new JSONObject();
            try {
                perfumeObject.put("name", item.getName());
                perfumeObject.put("brand", item.getBrand());
                perfumeObject.put("price", item.getPrice());
                perfumeObject.put("imageResId", item.getImageResId());
                perfumeObject.put("quantity", Math.min(quantityToAdd, item.getInStock()));
                perfumeObject.put("inStock", item.getInStock());
                perfumeObject.put("gender", item.getGender());
                perfumeObject.put("type", item.getType());
                perfumeObject.put("isLongLasting", item.isLongLasting());
                perfumeObject.put("isStrongScent", item.isStrongScent());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            cartArray.put(perfumeObject);
        }

        editor.putString("cart", cartArray.toString());
        editor.apply();
    }

    @Override
    public int getItemCount() {
        return perfumeList.size();
    }

    public static class PerfumeViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, brandTextView, priceTextView, stockTextView;
        ImageView imageView;
        Button addToCartButton;

        public PerfumeViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.perfumeName);
            brandTextView = itemView.findViewById(R.id.perfumeBrand);
            priceTextView = itemView.findViewById(R.id.perfumePrice);
            stockTextView = itemView.findViewById(R.id.perfumeStock);
            imageView = itemView.findViewById(R.id.perfumeImage);
            addToCartButton = itemView.findViewById(R.id.addToCartButton);
        }
    }
}
