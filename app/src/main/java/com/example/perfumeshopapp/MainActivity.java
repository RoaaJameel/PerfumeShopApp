package com.example.perfumeshopapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView perfumeRecyclerView;
    private PerfumeAdapter perfumeAdapter;
    public static List<PerfumeItem> perfumeList = new ArrayList<>();

    private TextView cartItemCountTextView;

    static {
        // many objects of perfumes to view
        perfumeList.add(new PerfumeItem("Dior Sauvage", "Dior", 120.0, R.drawable.sauvage, 5, "Male", "Eau de Parfum", true, true));
        perfumeList.add(new PerfumeItem("Chanel Bleu", "Chanel", 150.0, R.drawable.chanelblue, 3, "Male", "Eau de Toilette", false, true));
        perfumeList.add(new PerfumeItem("Tom Ford Oud Wood", "Tom Ford", 200.0, R.drawable.tom_ford_oud_wood, 2, "Male", "Eau de Parfum", true, false));
        perfumeList.add(new PerfumeItem("Yves Saint Laurent La Nuit de L'Homme", "Yves Saint Laurent", 130.0, R.drawable.ysl_la_nuit, 4, "Male", "Eau de Toilette", true, false));
        perfumeList.add(new PerfumeItem("Giorgio Armani Acqua di Gio", "Giorgio Armani", 90.0, R.drawable.armani_acqua_di_gio, 7, "Male", "Eau de Toilette", false, false));
        perfumeList.add(new PerfumeItem("Chanel No. 5", "Chanel", 180.0, R.drawable.chanel_no_5, 3, "Female", "Eau de Parfum", true, true));
        perfumeList.add(new PerfumeItem("Dior J'adore", "Dior", 160.0, R.drawable.dior_jadore, 5, "Female", "Eau de Parfum", false, true));
        perfumeList.add(new PerfumeItem("Gucci Bloom", "Gucci", 140.0, R.drawable.gucci_bloom, 2, "Female", "Eau de Parfum", true, false));
        perfumeList.add(new PerfumeItem("Tom Ford Black Orchid", "Tom Ford", 220.0, R.drawable.tom_ford_black_orchid, 4, "Female", "Eau de Parfum", true, true));
        perfumeList.add(new PerfumeItem("Yves Saint Laurent Mon Paris", "Yves Saint Laurent", 150.0, R.drawable.ysl_mon_paris, 6, "Female", "Eau de Parfum", false, false));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cartItemCountTextView = findViewById(R.id.cartItemCountTextView);
        perfumeRecyclerView = findViewById(R.id.perfumeRecyclerView);
        perfumeRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        Button checkCartButton = findViewById(R.id.checkCartButton);
        checkCartButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CartActivity.class);
            startActivity(intent);
        });

        updateCartItemCount();
        loadAndDisplayAvailablePerfumes();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCartItemCount();
        loadAndDisplayAvailablePerfumes();
    }

    private void updateCartItemCount() {
        SharedPreferences sharedPreferences = getSharedPreferences("PerfumeShop", MODE_PRIVATE);
        String cartJson = sharedPreferences.getString("cart", "[]");

        int totalCount = 0;

        try {
            JSONArray cartArray = new JSONArray(cartJson);
            for (int i = 0; i < cartArray.length(); i++) {
                JSONObject item = cartArray.getJSONObject(i);
                totalCount += item.getInt("quantity");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        cartItemCountTextView.setText("Cart (" + totalCount + ")");
    }

    private void loadAndDisplayAvailablePerfumes() {
        List<PerfumeItem> availablePerfumes = new ArrayList<>();
        for (PerfumeItem item : perfumeList) {
            if (item.getInStock() > 0) {
                availablePerfumes.add(item);
            }
        }

        if (perfumeAdapter == null) {
            perfumeAdapter = new PerfumeAdapter(this, availablePerfumes);
            perfumeRecyclerView.setAdapter(perfumeAdapter);
        } else {
            perfumeAdapter.setPerfumeList(availablePerfumes);
        }
    }

    public static List<PerfumeItem> getPerfumeList() {
        return perfumeList;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
