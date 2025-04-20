package com.example.perfumeshopapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MyOrdersActivity extends AppCompatActivity {

    private RecyclerView ordersRecyclerView;
    private OrderDetailsAdapter orderDetailsAdapter;
    private List<Order> orderList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);

        ordersRecyclerView = findViewById(R.id.ordersRecyclerView);
        ordersRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadOrders();

        orderDetailsAdapter = new OrderDetailsAdapter(this, orderList);
        ordersRecyclerView.setAdapter(orderDetailsAdapter);
    }

    private void loadOrders() {
        SharedPreferences sharedPreferences = getSharedPreferences("PerfumeShop", MODE_PRIVATE);
        String ordersJson = sharedPreferences.getString("orders", "[]");

        Log.d("OrdersDebug", "Loaded orders JSON: " + ordersJson);

        try {
            JSONArray ordersArray = new JSONArray(ordersJson);
            orderList.clear();

            for (int i = 0; i < ordersArray.length(); i++) {
                JSONObject orderObj = ordersArray.getJSONObject(i);
                long orderId = orderObj.optLong("orderId", 0);
                long orderDate = orderObj.optLong("orderDate", 0);
                JSONArray itemsArray = orderObj.getJSONArray("items");

                List<PerfumeItem> items = new ArrayList<>();
                for (int j = 0; j < itemsArray.length(); j++) {
                    JSONObject itemObj = itemsArray.getJSONObject(j);
                    String name = itemObj.optString("name", "");
                    String brand = itemObj.optString("brand", "");
                    double price = itemObj.optDouble("price", 0.0);
                    int quantity = itemObj.optInt("quantity", 1);
                    int imageResId = itemObj.optInt("imageResId", 0);

                    PerfumeItem item = new PerfumeItem();
                    item.setName(name);
                    item.setBrand(brand);
                    item.setPrice(price);
                    item.setQuantity(quantity);
                    item.setImageResId(imageResId);

                    items.add(item);
                }

                Order order = new Order(orderId, orderDate, items);
                orderList.add(order);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
