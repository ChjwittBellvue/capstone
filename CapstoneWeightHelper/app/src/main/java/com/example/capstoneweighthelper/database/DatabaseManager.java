/*
 * References
 *
 * Franceschi, H. J. (2016). Android App Development. Jones & Bartlett Learning.
 */
package com.example.capstoneweighthelper.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.capstoneweighthelper.pojo.UserProfile;

public class DatabaseManager extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "capstoneWeightHelperDb";
    private static final int DATABASE_VERSION = 1;

    //Tables
    private static final String USER_PROFILE_TABLE = "UserProfile";

    public DatabaseManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createUserProfileTable());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + USER_PROFILE_TABLE);
        onCreate(db);
    }

    public String createUserProfileTable() {
        return "CREATE TABLE " + USER_PROFILE_TABLE + " ("
        + "id integer primary key autoincrement,"
        + "name text,"
        + "currentWeight integer,"
        + "goalWeight integer,"
        + "goalDate string,"
        + "gender text,"
        + "heightFeet integer,"
        + "heightInches integer)";
    }

    public void insertIntoUserProfile(UserProfile userProfile) {
        String sqlInsert = "insert into " + USER_PROFILE_TABLE + " VALUES " +
                "( null, '" + userProfile.getName() + "', " + userProfile.getCurrentWeight()  + ", "
                + userProfile.getGoalWeight() + ", '" + userProfile.getGoalDate().toString()  + "', '"
                + userProfile.getGender() + "', " + userProfile.getHeightFeet()  + ", "
                + userProfile.getHeightInches()  + ")";
        System.out.println(sqlInsert);
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(sqlInsert);
    }

    public UserProfile selectUserProfile(Integer userId) {
        String sqlUserProfileSelect = "SELECT * FROM " + USER_PROFILE_TABLE + " WHERE id=" + userId;


        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(sqlUserProfileSelect, null);
        cursor.moveToFirst();

        UserProfile userProfile = new UserProfile(cursor.getInt(0), cursor.getString(1), cursor.getInt(2),
                cursor.getInt(3), Long.parseLong(cursor.getString(4)), cursor.getString(5), cursor.getInt(6),
                cursor.getInt(7));

        System.out.println(cursor.getInt(0));
        cursor.close();

        return userProfile;
    }

    // Testing purposes only
//    public void clearUserProfiles() {
//        String sqlUserProfilesDelete= "DELETE FROM " + USER_PROFILE_TABLE;
//        SQLiteDatabase db = this.getWritableDatabase();
//        db.execSQL(sqlUserProfilesDelete);
//    }

}
