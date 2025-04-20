package com.example.perfumeshopapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView perfumeRecyclerView;
    private PerfumeAdapter perfumeAdapter;
    public static List<PerfumeItem> perfumeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        perfumeRecyclerView = findViewById(R.id.perfumeRecyclerView);
        perfumeRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        perfumeList = new ArrayList<>();

        // إضافة العطور
        perfumeList.add(new PerfumeItem("Dior Sauvage", "Dior", 120.0, R.drawable.perfume1, 3, "Male", "Eau de Parfum", true, true));
        perfumeList.add(new PerfumeItem("Chanel Bleu", "Chanel", 150.0, R.drawable.perfume2, 1, "Male", "Eau de Parfum", false, true));
        // ... أكمل العطور اللي بدك إياها

        perfumeAdapter = new PerfumeAdapter(this, perfumeList);
        perfumeRecyclerView.setAdapter(perfumeAdapter);

        // زر الذهاب إلى السلة
        Button checkCartButton = findViewById(R.id.checkCartButton);
        checkCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CartActivity.class);
                startActivity(intent);
            }
        });
    }

    public static List<PerfumeItem> getPerfumeList() {
        return perfumeList;
    }
}
