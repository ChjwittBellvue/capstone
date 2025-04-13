package com.example.capstoneweighthelper;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.capstoneweighthelper.util.DatePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Settings extends AppCompatActivity {

    private TextView dateTextView;
    private Button pickDateButton;
    private Calendar calendar;
    private DatePickerDialog datePickerDialog;

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

        // Set initial date
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        dateTextView.setText(dateFormat.format(calendar.getTime()));

        pickDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });
    }

    /**
     * Note: Switch case no longer works here, ids are not considered final
     */
    private class ButtonHandler implements View.OnClickListener {
        public void onClick( View v) {
            if (v.getId() == R.id.back_arrow) {
                moveToSummary();
            } else if (v.getId() == R.id.pickDateButton) {
                DatePicker datePicker = new DatePicker();
            }
        }
    }

    private void moveToSummary() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void showDatePickerDialog() {
        // Get current date
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Create DatePickerDialog
        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(android.widget.DatePicker view, int year, int month, int dayOfMonth) {
                    // Update the TextView with the selected date
                    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                    calendar.set(year, month, dayOfMonth);
                    dateTextView.setText(dateFormat.format(calendar.getTime()));
            }
        }, year, month, day);

        // Show DatePickerDialog
        datePickerDialog.show();
    }
}