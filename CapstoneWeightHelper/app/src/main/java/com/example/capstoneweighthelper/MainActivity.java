package com.example.capstoneweighthelper;

import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.capstoneweighthelper.database.DatabaseManager;
import com.example.capstoneweighthelper.pojo.UserProfile;

import java.awt.font.TextAttribute;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private DatabaseManager databaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_summary);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        ButtonHandler bh = new ButtonHandler();
        databaseManager = new DatabaseManager(this);

        setWeightMetrics();

        Button settings_button = findViewById(R.id.settings_nav);
        Button weight_entry_button = findViewById(R.id.weight_entry_nav);
        Button history_button = findViewById(R.id.weight_history_nav);
        settings_button.setOnClickListener(bh);
        weight_entry_button.setOnClickListener(bh);
        history_button.setOnClickListener(bh);
    }

    /**
     * Note: Switch case no longer works here, ids are not considered final
     */
    private class ButtonHandler implements View.OnClickListener {
        public void onClick( View v) {
            if (v.getId() == R.id.weight_entry_nav) {
                moveToEntry();
            } else if (v.getId() == R.id.weight_history_nav) {
                moveToHistory();
            } else if (v.getId() == R.id.settings_nav) {
                moveToSettings();
            }
        }
    }

    private void moveToSettings() {
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
    }

    private void moveToHistory() {
        Intent intent = new Intent(this, WeightHistory.class);
        startActivity(intent);
    }

    private void moveToEntry() {
        Intent intent = new Intent(this, WeightEntry.class);
        startActivity(intent);
    }

    //TODO: Update to take in user profile
    private void setWeightMetrics() {
        UserProfile userProfile = databaseManager.selectUserProfile(1);
        TextView currentWeight = findViewById(R.id.current_weight);
        TextView goalWeight = findViewById(R.id.goal_weight);
        TextView goalDate = findViewById(R.id.goal_date);

        currentWeight.setText(setWeightString(userProfile.getCurrentWeight()));
        goalWeight.setText(setWeightString(userProfile.getGoalWeight()));

        Date goal = new Date(userProfile.getGoalDate());
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yy", Locale.US);
        String goalDateAsString = df.format(goal);
        goalDate.setText(goalDateAsString);
    }

    private void setCurrentWeight() {

    }

    private String setWeightString(Integer weight) {
        return weight + " LBS";
    }
}