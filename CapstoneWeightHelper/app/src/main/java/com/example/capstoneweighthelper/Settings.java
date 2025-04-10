package com.example.capstoneweighthelper;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.settings_activity), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        ButtonHandler bh = new ButtonHandler();

        ImageView backArrow = findViewById(R.id.back_arrow);
        backArrow.setOnClickListener(bh);
    }

    /**
     * Note: Switch case no longer works here, ids are not considered final
     */
    private class ButtonHandler implements View.OnClickListener {
        public void onClick( View v) {
            if (v.getId() == R.id.back_arrow) {
                moveToSummary();
            }
        }
    }

    private void moveToSummary() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}