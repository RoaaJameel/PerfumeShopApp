package com.example.perfumeshopapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView totalPriceText;
    private Button checkoutButton, clearCartButton;
    private ArrayList<PerfumeItem> cartItems = new ArrayList<>();
    private CartAdapter cartAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        recyclerView = findViewById(R.id.cartRecyclerView);
        totalPriceText = findViewById(R.id.totalPriceText);
        checkoutButton = findViewById(R.id.checkoutButton);
        clearCartButton = findViewById(R.id.clearCartButton);

        loadCartData();

        cartAdapter = new CartAdapter(this, cartItems);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(cartAdapter);

        updateTotalPrice();

        checkoutButton.setOnClickListener(v -> {
            Toast.makeText(this, "Thank you for your purchase!", Toast.LENGTH_SHORT).show();
        });

        clearCartButton.setOnClickListener(v -> {
            clearCart();
        });
    }

    private void loadCartData() {
        SharedPreferences sharedPreferences = getSharedPreferences("PerfumeShop", Context.MODE_PRIVATE);
        String cartJson = sharedPreferences.getString("cart", "[]");

        try {
            JSONArray jsonArray = new JSONArray(cartJson);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                String name = obj.getString("name");
                String brand = obj.getString("brand");
                double price = obj.getDouble("price");
                int imageResId = obj.getInt("imageResId");
                int inStock = obj.getInt("inStock");
                String gender = obj.getString("gender");
                String type = obj.getString("type");
                boolean isLongLasting = obj.getBoolean("isLongLasting");
                boolean isStrongScent = obj.getBoolean("isStrongScent");

                PerfumeItem item = new PerfumeItem(name, brand, price, imageResId, inStock, gender, type, isLongLasting, isStrongScent);
                item.setQuantity(obj.getInt("quantity")); // قراءة الكمية
                cartItems.add(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void updateTotalPrice() {
     //   int quantity = cart
        double total = 0.0;
        for (PerfumeItem item : cartItems) {
            total += item.getPrice() ;
        }
        totalPriceText.setText("Total: $" + String.format("%.2f", total));
    }

    private void clearCart() {
        SharedPreferences sharedPreferences = getSharedPreferences("PerfumeShop", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("cart");
        editor.apply();

        cartItems.clear();
        cartAdapter.notifyDataSetChanged();
        updateTotalPrice();

        Toast.makeText(this, "Cart cleared!", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cartAdapter.notifyDataSetChanged();
        updateTotalPrice();
    }
}
