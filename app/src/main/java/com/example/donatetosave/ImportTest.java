package com.example.donatetosave;

import android.support.design.chip.Chip;
import android.support.design.chip.ChipGroup;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;


import com.example.donatetosave.Adapter.ChipAdapter;

import java.util.ArrayList;

public class ImportTest extends AppCompatActivity {
    private RecyclerView chipGroup;
    ChipAdapter chipAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_test);
        ArrayList<String> objects = getIntent().getStringArrayListExtra("objects");
        chipGroup = findViewById(R.id.import_detail);
        chipAdapter = new ChipAdapter(this,objects);
        chipGroup.setAdapter(chipAdapter);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        chipGroup.setLayoutManager(layoutManager);
    }
}
