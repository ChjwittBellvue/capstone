/*
* References
*
* Abhi Android (n.d.). ListView Tutorial With Example In Android Studio. Retrieved April 13, 2025, from https://abhiandroid.com/ui/listview#gsc.tab=0
*
 */
package com.example.capstoneweighthelper.util;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.capstoneweighthelper.R;
import com.example.capstoneweighthelper.WeightEntry;
import com.example.capstoneweighthelper.WeightHistory;
import com.example.capstoneweighthelper.database.DatabaseManager;
import com.example.capstoneweighthelper.pojo.WeightHistoryRecord;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CustomAdapter extends BaseAdapter {
    Context context;
    List<WeightHistoryRecord> weightHistoryRecords;
    LayoutInflater inflter;
    WeightHistory weightHistoryContext;
    private int userId;

    public CustomAdapter(WeightHistory weightHistoryContext, Context applicationContext, List<WeightHistoryRecord> weightHistoryRecords, int userId) {
        this.weightHistoryContext = weightHistoryContext;
        this.context = applicationContext;
        this.weightHistoryRecords = weightHistoryRecords;
        this.userId = userId;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return weightHistoryRecords.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    /**
     * Displays entry history rows
     *
     * @param i The position of the item within the adapter's data set of the item whose view
     *        we want.
     * @param view The old view to reuse, if possible. Note: You should check that this view
     *        is non-null and of an appropriate type before using. If it is not possible to convert
     *        this view to display the correct data, this method can create a new view.
     *        Heterogeneous lists can specify their number of view types, so that this View is
     *        always of the right type (see {@link #getViewTypeCount()} and
     *        {@link #getItemViewType(int)}).
     * @param viewGroup The parent that this view will eventually be attached to
     * @return
     */
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.activity_listview, null);
        TextView historyDate = (TextView) view.findViewById(R.id.history_date);
        TextView historyWeight = (TextView) view.findViewById(R.id.history_weight);
        ImageView picture = (ImageView) view.findViewById(R.id.history_picture);
        ImageButton editButton = (ImageButton) view.findViewById(R.id.edit_record);

        Date goal = new Date(weightHistoryRecords.get(i).getEntryDate());
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        String dateAsString = df.format(goal);

        String dateLabelAndString = "Date: " + dateAsString;
        historyDate.setText(dateLabelAndString);
        String weightLabelAndString = "Weight: " + weightHistoryRecords.get(i).getEntryWeight().toString();
        historyWeight.setText(weightLabelAndString);

        if (weightHistoryRecords.get(i).getPictureLocation() != null){
            picture.setImageURI(Uri.parse(weightHistoryRecords.get(i).getPictureLocation()));
        }

        editButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DatabaseManager databaseManager = new DatabaseManager(weightHistoryContext, userId);
                WeightHistoryRecord editRecord = weightHistoryRecords.get(i);
                editRecord.setPermanentRecord("E");

                databaseManager.updateWeightEntry(editRecord);

                Intent intent = setIntentForWeightEntry(editRecord);
                context.startActivity(intent);

            }
        });

        return view;
    }

    /**
     * Handles moving to a new page
     *
     * @param editRecord
     * @return
     */
    private Intent setIntentForWeightEntry(WeightHistoryRecord editRecord) {
        Intent intent = new Intent(weightHistoryContext, WeightEntry.class);

        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        if (editRecord.getId() != null) {
            intent.putExtra("recordId", editRecord.getId());
        }
        if (editRecord.getEntryWeight()!= null) {
            intent.putExtra("recordWeight", editRecord.getEntryWeight());
        }
        if (editRecord.getEntryDate()!= null) {
            intent.putExtra("recordDate", editRecord.getEntryDate());
        }
        if (editRecord.getPictureLocation()!= null) {
            intent.putExtra("pictureUri", editRecord.getPictureLocation());
        }

        return intent;
    }

}
