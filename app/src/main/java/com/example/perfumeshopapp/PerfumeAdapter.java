package com.example.perfumeshopapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
            int selectedQuantity = 1;

            // الحصول على SharedPreferences
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

            // البحث عن العطر في السلة
            for (int i = 0; i < cartArray.length(); i++) {
                try {
                    JSONObject obj = cartArray.getJSONObject(i);
                    if (obj.getString("name").equals(item.getName())) {
                        // تحديث الكمية إذا كان العطر موجودًا
                        int currentQty = obj.getInt("quantity");
                        obj.put("quantity", currentQty + selectedQuantity);
                        found = true;
                        break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            // إذا كان العطر غير موجود، أضفه إلى السلة
            if (!found) {
                JSONObject perfumeObject = new JSONObject();
                try {
                    perfumeObject.put("name", item.getName());
                    perfumeObject.put("brand", item.getBrand());
                    perfumeObject.put("price", item.getPrice());
                    perfumeObject.put("imageResId", item.getImageResId());
                    perfumeObject.put("quantity", selectedQuantity);
                    cartArray.put(perfumeObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            // حفظ التعديلات في SharedPreferences
            editor.putString("cart", cartArray.toString());
            editor.apply();

            // إعلام المستخدم أن العطر تم إضافته
            Toast.makeText(context, item.getName() + " added to cart!", Toast.LENGTH_SHORT).show();
        });
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
