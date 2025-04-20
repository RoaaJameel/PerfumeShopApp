package com.example.perfumeshopapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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

        cartAdapter = new CartAdapter(this, cartItems, this::updateTotalPrice, position -> {
            showRemoveConfirmationDialog(position);
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(cartAdapter);

        updateTotalPrice();

        checkoutButton.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Confirm Checkout")
                    .setMessage("Are you sure you want to place the order?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        placeOrder();
                        dialog.dismiss();
                    })
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .show();
        });

        clearCartButton.setOnClickListener(v -> clearCart());
    }

    private void placeOrder() {
        if (cartItems.isEmpty()) {
            Toast.makeText(this, "Your cart is empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        updateStock(); // to update the stock quantity

        saveOrder();// to save the order info

        clearCart();// to clear the cart info

        Intent intent = new Intent(this, MyOrdersActivity.class);
        startActivity(intent);// go the orders page

        Toast.makeText(this, "Order placed successfully!", Toast.LENGTH_SHORT).show();
    }

    private void updateStock() {
        List<PerfumeItem> allPerfumes = MainActivity.getPerfumeList();

        for (PerfumeItem cartItem : cartItems) {
            for (PerfumeItem perfume : allPerfumes) {
                if (perfume.getName().equals(cartItem.getName())) {
                    int newStock = perfume.getInStock() - cartItem.getQuantity();
                    perfume.setInStock(Math.max(newStock, 0));
                    break;
                }
            }
        }

        savePerfumeList(allPerfumes);
    }

    private void savePerfumeList(List<PerfumeItem> perfumeList) {
        SharedPreferences sharedPreferences = getSharedPreferences("PerfumeShop", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        JSONArray jsonArray = new JSONArray();

        try {
            for (PerfumeItem item : perfumeList) {
                JSONObject obj = new JSONObject();
                obj.put("name", item.getName());
                obj.put("brand", item.getBrand());
                obj.put("price", item.getPrice());
                obj.put("imageResId", item.getImageResId());
                obj.put("inStock", item.getInStock());
                obj.put("gender", item.getGender());
                obj.put("type", item.getType());
                obj.put("isLongLasting", item.isLongLasting());
                obj.put("isStrongScent", item.isStrongScent());
                jsonArray.put(obj);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        editor.putString("perfumeList", jsonArray.toString());
        editor.apply();
    }

    /**
     * Saves the order details to SharedPreferences.
     * This method retrieves the existing orders, adds the current cart items
     * to a new order, and then saves the updated list of orders back to SharedPreferences.
     */
    private void saveOrder() {
        SharedPreferences sharedPreferences = getSharedPreferences("PerfumeShop", MODE_PRIVATE);
        String ordersJson = sharedPreferences.getString("orders", "[]");
        JSONArray ordersArray;

        try {
            ordersArray = new JSONArray(ordersJson);
        } catch (JSONException e) {
            ordersArray = new JSONArray();
        }

        JSONArray orderItemsArray = new JSONArray();
        try {
            for (PerfumeItem item : cartItems) {
                JSONObject obj = new JSONObject();
                obj.put("name", item.getName());
                obj.put("brand", item.getBrand());
                obj.put("price", item.getPrice());
                obj.put("quantity", item.getQuantity());
                obj.put("imageResId", item.getImageResId());
                orderItemsArray.put(obj);
            }

            JSONObject orderObject = new JSONObject();
            orderObject.put("orderId", System.currentTimeMillis());
            orderObject.put("items", orderItemsArray);
            orderObject.put("orderDate", System.currentTimeMillis());

            ordersArray.put(orderObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        sharedPreferences.edit().putString("orders", ordersArray.toString()).apply();
    }

    /**
     * Shows a confirmation dialog to confirm whether the user wants to remove an item from the cart.
     * @param position The position of the item in the cart to be removed.
     */
    private void showRemoveConfirmationDialog(int position) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Remove Item")
                .setMessage("Are you sure you want to remove this perfume from your cart?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    cartItems.remove(position);
                    cartAdapter.notifyItemRemoved(position);
                    updateCartData();
                    updateTotalPrice();
                    dialog.dismiss();
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

    /**
     * Updates the cart data in SharedPreferences.
     * This method saves the current list of cart items to SharedPreferences
     * so that the cart contents persist across app sessions.
     */
    private void updateCartData() {
        SharedPreferences sharedPreferences = getSharedPreferences("PerfumeShop", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        JSONArray jsonArray = new JSONArray();

        for (PerfumeItem item : cartItems) {
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

    /**
     * Loads the cart data from SharedPreferences.
     * This method retrieves the saved cart items from SharedPreferences
     * and populates the cartItems list with this data.
     */
    private void loadCartData() {
        SharedPreferences sharedPreferences = getSharedPreferences("PerfumeShop", MODE_PRIVATE);
        String cartJson = sharedPreferences.getString("cart", "[]");

        Log.d("CartDebug", "Cart JSON loaded: " + cartJson);

        try {
            JSONArray jsonArray = new JSONArray(cartJson);
            cartItems.clear();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                String name = obj.optString("name", "");
                String brand = obj.optString("brand", "");
                double price = obj.optDouble("price", 0.0);
                int imageResId = obj.optInt("imageResId", 0);
                int inStock = obj.optInt("inStock", 0);
                String gender = obj.optString("gender", "");
                String type = obj.optString("type", "");
                boolean isLongLasting = obj.optBoolean("isLongLasting", false);
                boolean isStrongScent = obj.optBoolean("isStrongScent", false);
                int quantity = obj.optInt("quantity", 1);

                PerfumeItem item = new PerfumeItem(name, brand, price, imageResId, inStock, gender, type, isLongLasting, isStrongScent);
                item.setQuantity(quantity);
                cartItems.add(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (cartAdapter != null) {
            cartAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Updates the total price displayed in the cart.
     * This method calculates the total price of all items in the cart
     * and sets the text of the totalPriceText TextView accordingly.
     */
    public void updateTotalPrice() {
        double total = 0.0;
        for (PerfumeItem item : cartItems) {
            total += item.getPrice() * item.getQuantity();
        }
        totalPriceText.setText("Total: $" + String.format("%.2f", total));
    }

    /**
     * Clears all items from the cart.
     * This method removes the cart data from SharedPreferences, clears the cartItems list,
     * updates the cart adapter, and updates the total price.
     */
    private void clearCart() {
        SharedPreferences sharedPreferences = getSharedPreferences("PerfumeShop", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("cart");
        editor.apply();

        cartItems.clear();
        if (cartAdapter != null) {
            cartAdapter.notifyDataSetChanged();
        }
        updateTotalPrice();

        Toast.makeText(this, "Cart cleared!", Toast.LENGTH_SHORT).show();
    }

    /**
     * Refreshes the cart adapter
     * on onResume Method.
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (cartAdapter != null) {
            cartAdapter.notifyDataSetChanged();
        }
        updateTotalPrice();
    }

}
