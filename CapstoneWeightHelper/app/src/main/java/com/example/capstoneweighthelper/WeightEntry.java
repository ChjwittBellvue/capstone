package com.example.capstoneweighthelper;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.capstoneweighthelper.database.DatabaseManager;
import com.example.capstoneweighthelper.pojo.UserProfile;

import java.util.Date;

public class WeightEntry extends AppCompatActivity {
    private DatabaseManager databaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_weight_entry);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        databaseManager = new DatabaseManager(this);
        ButtonHandler bh = new ButtonHandler();

        ImageView backArrow = findViewById(R.id.back_arrow);
        Button cancel_button = findViewById(R.id.cancel_button);
        Button save_button = findViewById(R.id.save_button);
        backArrow.setOnClickListener(bh);
        cancel_button.setOnClickListener(bh);
        save_button.setOnClickListener(bh);
    }

    private class ButtonHandler implements View.OnClickListener {
        public void onClick( View v) {
            if (v.getId() == R.id.back_arrow) {
                moveToSummary();
            } else if (v.getId() == R.id.cancel_button) {

            } else if (v.getId() == R.id.save_button) {

            }
        }
    }

    private void moveToSummary() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }




}