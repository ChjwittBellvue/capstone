/*
* References
*
* Stackoverflow (n.d.). Adding positive / negative Button to DialogFragment's Dialog. Retrieved April 14, 2025, from https://stackoverflow.com/questions/18601049/adding-positive-negative-button-to-dialogfragments-dialog
 */

package com.example.capstoneweighthelper.util;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.DialogFragment;

import com.example.capstoneweighthelper.R;
import com.example.capstoneweighthelper.WeightEntry;
import com.example.capstoneweighthelper.WeightHistory;
import com.example.capstoneweighthelper.database.DatabaseManager;
import com.example.capstoneweighthelper.pojo.WeightHistoryRecord;

public class ConfirmationDialogue extends DialogFragment {
    WeightHistoryRecord weightHistoryRecord;
    WeightEntry weightEntryContext;
    Integer userId;
    public ConfirmationDialogue(WeightEntry weightEntryContext, WeightHistoryRecord weightHistoryRecord, int userId) {
        this.weightHistoryRecord = weightHistoryRecord;
        this.weightEntryContext = weightEntryContext;
        this.userId = userId;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(DialogFragment.STYLE_NORMAL, 0);

    }

    /**
     * Houses logic for dialog interaction and creation
     *
     * @param savedInstanceState The last saved instance state of the Fragment,
     * or null if this is a freshly created Fragment.
     *
     * @return
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        return new AlertDialog.Builder(getActivity())
                .setTitle("title")
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                DatabaseManager databaseManager = new DatabaseManager(weightEntryContext, userId);
                                databaseManager.deleteWeightEntryById(weightHistoryRecord.getId());

                                Intent intent = new Intent(weightEntryContext, WeightHistory.class);

                                intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                                weightEntryContext.startActivity(intent);
                            }
                        }
                )
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                            }
                        }
                )
                .create();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.activity_confirmation_dialogue, container, false);
        getDialog().setTitle(R.string.do_you_really_want_to_delete_this_record);


        return v;
    }
}