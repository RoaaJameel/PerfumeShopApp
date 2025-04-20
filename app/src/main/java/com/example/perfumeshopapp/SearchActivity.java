package com.example.perfumeshopapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private AutoCompleteTextView searchEditText;
    private Spinner perfumeTypeSpinner;
    private RadioButton radioMale, radioFemale;
    private CheckBox checkBoxLongLasting, checkBoxStrong;
    private Switch switchPriceSort;
    private Button searchButton;
    private RecyclerView perfumeRecyclerView;
    private Button checkCartButton;

    private PerfumeAdapter perfumeAdapter;
    private List<PerfumeItem> originalList;
    private List<PerfumeItem> filteredList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchEditText = findViewById(R.id.searchEditText);
        perfumeTypeSpinner = findViewById(R.id.perfumeTypeSpinner);
        radioMale = findViewById(R.id.radioMale);
        radioFemale = findViewById(R.id.radioFemale);
        checkBoxLongLasting = findViewById(R.id.checkBoxLongLasting);
        checkBoxStrong = findViewById(R.id.checkBoxStrong);
        switchPriceSort = findViewById(R.id.switchPriceSort);
        searchButton = findViewById(R.id.searchButton);
        perfumeRecyclerView = findViewById(R.id.perfumeRecyclerView);
        checkCartButton = findViewById(R.id.checkCartButton); // تهيئة الزر

        // Spinner إعداد
        String[] perfumeTypes = {"All Types", "Eau de Parfum", "Eau de Toilette", "Eau de Cologne"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, perfumeTypes);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        perfumeTypeSpinner.setAdapter(spinnerAdapter);

        // إعداد AutoComplete
        String[] perfumeNames = {"Dior Sauvage", "Chanel Bleu", "Tom Ford Oud Wood", "Yves Saint Laurent La Nuit",
                "Armani Code", "Chanel No. 5", "Dior J'adore", "Gucci Bloom", "Tom Ford Black Orchid",
                "Yves Saint Laurent Mon Paris"};
        ArrayAdapter<String> searchAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, perfumeNames);
        searchEditText.setAdapter(searchAdapter);
        searchEditText.setThreshold(1);

        // RecyclerView إعداد
        perfumeRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        originalList = MainActivity.getPerfumeList();
        if (originalList == null) {
            originalList = new ArrayList<>();
            Toast.makeText(this, "No perfumes found!", Toast.LENGTH_SHORT).show();
        }
        filteredList = new ArrayList<>();
        perfumeAdapter = new PerfumeAdapter(this, filteredList);
        perfumeRecyclerView.setAdapter(perfumeAdapter);

        // زر البحث
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterPerfumes();
            }
        });

        switchPriceSort.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                filterPerfumes();
            }
        });

        // الانتقال الى عربة التسوق
        checkCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchActivity.this, CartActivity.class);
                startActivity(intent);
            }
        });
    }

    private void filterPerfumes() {
        List<PerfumeItem> newFilteredList = new ArrayList<>();

        String nameQuery = searchEditText.getText().toString().toLowerCase().trim();
        String selectedType = perfumeTypeSpinner.getSelectedItem().toString();
        String gender = radioMale.isChecked() ? "Male" : (radioFemale.isChecked() ? "Female" : "");
        boolean isLongLasting = checkBoxLongLasting.isChecked();
        boolean isStrong = checkBoxStrong.isChecked();

        if (originalList != null) {
            for (PerfumeItem item : originalList) {
                boolean matches = true;

                // فلترة حسب النص إذا المستخدم كتب
                if (!nameQuery.isEmpty() && !item.getName().toLowerCase().contains(nameQuery)) {
                    matches = false;
                }

                // فلترة حسب النوع إذا المستخدم اختار نوع غير "All Types"
                if (!selectedType.equals("All Types") && !item.getType().equalsIgnoreCase(selectedType)) {
                    matches = false;
                }

                // فلترة حسب الجنس إذا اختار
                if (!gender.isEmpty() && !item.getGender().equalsIgnoreCase(gender)) {
                    matches = false;
                }

                // فلترة حسب خاصية العطر لو مستخدم فعّلها
                if (isLongLasting && !item.isLongLasting()) {
                    matches = false;
                }

                if (isStrong && !item.isStrongScent()) {
                    matches = false;
                }

                if (matches) {
                    newFilteredList.add(item);
                }
            }
        }

        if (switchPriceSort.isChecked()) {
            Collections.sort(newFilteredList, (a, b) -> Double.compare(a.getPrice(), b.getPrice()));
        }

        filteredList.clear();
        filteredList.addAll(newFilteredList);
        if (perfumeAdapter != null) {
            perfumeAdapter.notifyDataSetChanged();
        }
    }

}
