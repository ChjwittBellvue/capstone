package com.example.capstoneweighthelper.util;

import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class Validation {

    /**
     * Provides validation for weight entry
     *
     * @param textView
     * @param errorTextView
     * @return
     */
    public boolean validateWeight(TextView textView, TextView errorTextView) {
        errorTextView.setText("");
        try {
            String entryWeight = textView.getText().toString();
            Double numericEntryWeight = Double.parseDouble(entryWeight);

            if (numericEntryWeight>0) {
                if (entryWeight.toString().contains(".")) {
                    String[] integerAndDecimal = entryWeight.toString().split("\\.");
                    try {
                        if (integerAndDecimal[1].length() < 2) {
                            return true;
                        }
                        errorTextView.setText("Enter weight to the tenth");
                        return false;
                    } catch (ArrayIndexOutOfBoundsException e) {
                        errorTextView.setText("Enter valid decimal");
                        return false;
                    }
                }
            } else {
                errorTextView.setText("Enter a positive number");
                return false;
            }

        } catch (NumberFormatException  e){
            errorTextView.setText("Enter a number");
            return false;
        }
        return true;
    }

    /**
     * Provides validation for dates
     *
     * @param textView
     * @param errorTextView
     * @return
     */
    public boolean validateDate(TextView textView, TextView errorTextView) {
        errorTextView.setText("");
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        sdf.setLenient(false);
        try {
            String dateString = textView.getText().toString();
            sdf.parse(dateString);
        } catch (ParseException e) {
            errorTextView.setText("Enter valid date as MM/dd/yyyy");
            return false;
        }
        return true;
    }

    /**
     * Provides validation for Integers in entries
     * @param textView
     * @param errorTextView
     * @param textBoxLabel
     * @return
     */
    public boolean validateInteger(TextView textView, TextView errorTextView, String textBoxLabel) {
        errorTextView.setText("");
        try {
            String textString = textView.getText().toString();
            Integer.parseInt(textString);
        } catch (NumberFormatException e) {
            String errorString = "Use integer for " + textBoxLabel;
            errorTextView.setText(errorString);
            return false;
        }
        return true;
    }

    /**
     * Provides string validation
     *
     * @param textView
     * @param errorTextView
     * @param textBoxLabel
     * @return
     */
    public boolean validateStringExists(TextView textView, TextView errorTextView, String textBoxLabel) {
        errorTextView.setText("");
        if (!(textView.getText()==null)){
            String textString = textView.getText().toString();
            if (!textString.isEmpty()) {
                return true;
            } else {
                String errorMessage = "Enter a " + textBoxLabel;
                errorTextView.setText(errorMessage);
                return false;
            }
        } else {
            String errorMessage = "Enter a " + textBoxLabel;
            errorTextView.setText(errorMessage);
            return false;
        }
    }

}
