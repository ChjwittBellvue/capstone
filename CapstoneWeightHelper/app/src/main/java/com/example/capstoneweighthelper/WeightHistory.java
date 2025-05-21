/*
* References
*
* Abhi Android (n.d.). ListView Tutorial With Example In Android Studio. Retrieved April 13, 2025, from https://abhiandroid.com/ui/listview#gsc.tab=0
 */
package com.example.capstoneweighthelper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.capstoneweighthelper.database.DatabaseManager;
import com.example.capstoneweighthelper.pojo.WeightHistoryRecord;
import com.example.capstoneweighthelper.util.CustomAdapter;

import java.util.List;

public class WeightHistory extends AppCompatActivity {
    DatabaseManager databaseManager;
    ListView simpleList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_weight_history);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        SharedPreferences sharedPreferences = getSharedPreferences("UserLogin", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("ActiveUserId", -1);
        databaseManager = new DatabaseManager(this,userId);

        ButtonHandler bh = new ButtonHandler();

        ImageView backArrow = findViewById(R.id.back_arrow);
        backArrow.setOnClickListener(bh);

        List<WeightHistoryRecord> records = getHistoryRecords();

        // Starts the Custom Adapter, which holds the list
        simpleList = (ListView)findViewById(R.id.simpleListView);
        CustomAdapter customAdapter = new CustomAdapter(this, getApplicationContext(), records, userId);
        simpleList.setAdapter(customAdapter);

    }

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

    /**
     * Retrieves record list
     * @return
     */
    private List<WeightHistoryRecord> getHistoryRecords() {
        return databaseManager.getPermanentWeightEntriesForHistory();
    }

}