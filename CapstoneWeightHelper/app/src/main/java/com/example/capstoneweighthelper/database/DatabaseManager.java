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

import com.example.capstoneweighthelper.pojo.LoginObject;
import com.example.capstoneweighthelper.pojo.UserProfile;
import com.example.capstoneweighthelper.pojo.WeightHistoryRecord;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class DatabaseManager extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "capstoneWeightHelperDb";
    private static final int DATABASE_VERSION = 1;

    //Tables
    private static final String USER_PROFILE_TABLE = "UserProfile";
    private static final String WEIGHT_HISTORY_TABLE = "WeightHistory";
    private static final String LOGIN_TABLE = "Login";

    private Integer userId;

    public DatabaseManager(Context context, Integer userId) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.userId = userId;
    }

    /**
     * Creates the databases
     * @param db The database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createUserProfileTable());
        db.execSQL(createWeightHistoryTable());
        db.execSQL(createLoginTable());
    }

    /**
     * Removes and re-creates databases when changes are made.
     * @param db The database.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + USER_PROFILE_TABLE);
        db.execSQL("drop table if exists " + WEIGHT_HISTORY_TABLE);
        db.execSQL("drop table if exists " + LOGIN_TABLE);
        onCreate(db);
    }

    /**
     * User profile table creation sql
     * @return
     */
    public String createUserProfileTable() {
        return "CREATE TABLE " + USER_PROFILE_TABLE + " ("
            + "id integer primary key autoincrement,"
            + "name text,"
            + "currentWeight real,"
            + "goalWeight real,"
            + "goalDate text,"
            + "gender text,"
            + "heightFeet integer,"
            + "heightInches integer,"
            + "activeUser text)";
    }

    /**
     * Weight History table creation sql
     * @return
     */
    public String createWeightHistoryTable() {
        return "CREATE TABLE " + WEIGHT_HISTORY_TABLE + " ("
            + "id integer primary key autoincrement,"
            + "userId integer,"
            + "entryWeight real,"
            + "entryDate text,"
            + "pictureLocation text,"
            + "permanentRecord text)";
    }

    /**
     * Login table creation sql
     * @return
     */
    public String createLoginTable() {
        return "CREATE TABLE " + LOGIN_TABLE + " ("
                + "id integer,"
                + "userName text,"
                + "password text)";
    }

    // USER PROFILE

    /**
     * Inserts new user into UserProfile
     * @param userProfile - object definition of a user
     */
    public void insertIntoUserProfile(UserProfile userProfile) {
        String sqlInsert = "insert into " + USER_PROFILE_TABLE + " VALUES " +
                "( null, '" + userProfile.getName() + "', " + userProfile.getCurrentWeight()  + ", "
                + userProfile.getGoalWeight() + ", '" + userProfile.getGoalDate().toString()  + "', '"
                + userProfile.getGender() + "', " + userProfile.getHeightFeet()  + ", "
                + userProfile.getHeightInches()  + ", '" +"Y')";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(sqlInsert);
        System.out.println("User insert: " + sqlInsert);
        db.close();
    }

    /**
     * Selects user profile based on userId passed in class instantiation
     */
    public UserProfile selectActiveUserProfile() {
        String sqlUserProfileSelect = "SELECT * FROM " + USER_PROFILE_TABLE + " WHERE id=" + userId;
        System.out.println("sqlUserProfileSelect: " + sqlUserProfileSelect);

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(sqlUserProfileSelect, null);
        cursor.moveToFirst();

        UserProfile userProfile = new UserProfile(cursor.getInt(0), cursor.getString(1), cursor.getDouble(2),
                cursor.getDouble(3), Long.parseLong(cursor.getString(4)), cursor.getString(5), cursor.getInt(6),
                cursor.getInt(7));

        cursor.close();

        return userProfile;
    }

    /**
     * Finds the latest profile id
     * @return id
     */
    public Integer selectUserProfileMaxId() {
        String sqlUserProfileSelect = "SELECT max(id) FROM " + USER_PROFILE_TABLE;

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(sqlUserProfileSelect, null);
        cursor.moveToFirst();

        Integer maxId = cursor.getInt(0);

        cursor.close();
        db.close();

        return maxId;
    }

    /**
     * Updates a user profile
     * @param userProfile
     */
    public void updateUserProfile(UserProfile userProfile) {
        String sqlUserProfileUpdate = "UPDATE " + USER_PROFILE_TABLE + " SET " +
                "name='" + userProfile.getName() + "', " +
                "currentWeight=" + userProfile.getCurrentWeight() + ", " +
                "goalWeight=" + userProfile.getGoalWeight() + ", " +
                "goalDate=" + userProfile.getGoalDate() + ", " +
                "gender='" + userProfile.getGender() + "', " +
                "heightFeet=" + userProfile.getHeightFeet() + ", " +
                "heightInches=" + userProfile.getHeightInches() + " " +
                "WHERE id=" +userId;

        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL(sqlUserProfileUpdate);
        db.close();
    }

    // LOGIN

    /**
     * Inserts new entry into login table
     * @param login - LoginObject definition
     */
    public void insertIntoLogin(LoginObject login) {
        String insertString = "INSERT INTO " + LOGIN_TABLE + " VALUES (" + login.getId() + ", '" +
                login.getUserName() + "', '" + login.getPassword() + "')";

        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL(insertString);
        db.close();
    }

    /**
     * Inserts new user and related login
     * @param userProfile
     * @param login
     * @return id
     */
    public Integer createNewUser(UserProfile userProfile, LoginObject login) {
        WeightHistoryRecord weightHistoryRecord = new WeightHistoryRecord();
        insertIntoUserProfile(userProfile);

        Integer newId = selectUserProfileMaxId();
        login.setId(newId);
        weightHistoryRecord.setUserId(newId);

        insertIntoLogin(login);

        weightHistoryRecord.setEntryWeight(userProfile.getCurrentWeight());

        insertInitialWeightEntry(weightHistoryRecord);

        return newId;
    }

    /**
     * Returns id from user matching username and password
     * @param username
     * @param password
     * @return id
     */
    public Integer checkUserCredentials(String username, String password) {
        String sqlUserProfileSelect = "SELECT id FROM " + LOGIN_TABLE
                + " WHERE username= '" + username
                + "' AND password= '" + password + "'";

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(sqlUserProfileSelect, null);
        if (cursor.getCount()>0) {
            cursor.moveToFirst();

            Integer maxId = cursor.getInt(0);

            cursor.close();
            db.close();

            return maxId;
        }
        return -1;
    }

    /**
     * Returns a list of users
     * @return
     */
    public List<LoginObject> getAllUsers() {
        String sqlUserProfileSelect = "SELECT * FROM " + LOGIN_TABLE;

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(sqlUserProfileSelect, null);
        if (cursor.getCount()>0) {
            cursor.moveToFirst();

            List<LoginObject> users = new ArrayList<>();

            do {
                LoginObject user = getLoginObjectFromCursor(cursor);

                users.add(user);
            } while (cursor.moveToNext());

            cursor.close();
            db.close();

            return users;
        }
        return null;
    }

    /**
     * Utility to create a module out of login object cursor definition
     * @param cursor
     * @return
     */
    public LoginObject getLoginObjectFromCursor(Cursor cursor) {
        LoginObject loginObject = new LoginObject(cursor.getInt(0), cursor.getString(1), cursor.getString(2));

        return loginObject;
    }

    // WEIGHT HISTORY

    /**
     * Inserts a new weight history record
     * @param weightHistoryRecord
     * @return newest weight history record
     */
    public Integer insertIntoWeightHistory(WeightHistoryRecord weightHistoryRecord) {
        String weightHistoryString;
        if (weightHistoryRecord.getEntryDate() != null && !weightHistoryRecord.getEntryDate().toString().isEmpty()) {
            weightHistoryString = weightHistoryRecord.getEntryDate().toString();
        } else {
            weightHistoryString = null;
        }

        String pictureLocationString = null;
        if (weightHistoryRecord.getPictureLocation()!=null){
            pictureLocationString = "\'" + weightHistoryRecord.getPictureLocation() +"\'";
        }

        if (weightHistoryRecord.getUserId()==null) {
            weightHistoryRecord.setUserId(userId);
        }

        System.out.println("Weight history string: " + weightHistoryRecord);

        String sqlInsert = "insert into " + WEIGHT_HISTORY_TABLE + " VALUES " +
                "( null, "
                + weightHistoryRecord.getUserId() + ", "
                + weightHistoryRecord.getEntryWeight() + ", "
                + weightHistoryString + ", "
                + pictureLocationString + ", '"
                + weightHistoryRecord.getPermanentRecord()  + "')";
        System.out.println(sqlInsert);
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL(sqlInsert);
        db.close();

        return selectLatestWeightEntry();
    }

    /**
     * Returns newest weight entry
     * @return id
     */
    private Integer selectLatestWeightEntry() {
        String sqlUserProfileSelect = "SELECT max(id) FROM " + WEIGHT_HISTORY_TABLE;

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(sqlUserProfileSelect, null);
        cursor.moveToFirst();

        Integer maxId = cursor.getInt(0);

        cursor.close();
        db.close();

        return maxId;
    }

    /**
     * Creates a new weight entry for new users
     * @param weightHistoryRecord
     */
    public void insertInitialWeightEntry(WeightHistoryRecord weightHistoryRecord) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        try {
            Date dateWithoutTime = sdf.parse(sdf.format(new Date()));
            weightHistoryRecord.setEntryDate(dateWithoutTime.getTime());
            weightHistoryRecord.setPermanentRecord("Y");

            insertIntoWeightHistory(weightHistoryRecord);
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    public List<WeightHistoryRecord> getPermanentWeightEntriesForHistory() {
        ArrayList<WeightHistoryRecord> weightEntries = new ArrayList<>();

        String sqlUserProfileSelect = "SELECT * FROM " + WEIGHT_HISTORY_TABLE + " WHERE permanentRecord ='Y'" +
                " AND userId = " + userId + " " +
                "ORDER BY entryDate desc";

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(sqlUserProfileSelect, null);
        cursor.moveToFirst();
        System.out.println("count: " + cursor.getCount());

        if (cursor.getCount() > 0) {
            do {
                WeightHistoryRecord weightHistoryRecord = getWeightEntryFromCursor(cursor);

                weightEntries.add(weightHistoryRecord);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return weightEntries;
    }

    public void updateWeightEntry(WeightHistoryRecord weightHistoryRecord) {
       System.out.println("permanent record: " + weightHistoryRecord.getPermanentRecord());
        String pictureLocationAsString = null;
       if (weightHistoryRecord.getPictureLocation() != null) {
           pictureLocationAsString = "'" + weightHistoryRecord.getPictureLocation() + "'";
       }

        String sqlWeightHistoryUpdate = "UPDATE " + WEIGHT_HISTORY_TABLE + " SET " +
                "userId=" + userId + ", " +
                "entryWeight=" + weightHistoryRecord.getEntryWeight() + ", " +
                "entryDate=" + weightHistoryRecord.getEntryDate() + ", " +
                "pictureLocation=" + pictureLocationAsString + ", " +
                "permanentRecord='" + weightHistoryRecord.getPermanentRecord() + "' " +
                "WHERE id=" + weightHistoryRecord.getId();

        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL(sqlWeightHistoryUpdate);
        db.close();
    }

    /**
     * Resets new weight entry state when user navigates away from the entry page
     */
    public void resetEditState() {
        deleteInProgressWeightEntry();
        resetEditStatusForWeightEntries();
    }

    /**
     * Deletes a weight entry by the id
     * @param recordId
     */
    public void deleteWeightEntryById(Integer recordId) {
        String sqlDelete = "delete from " + WEIGHT_HISTORY_TABLE + " WHERE id = " + recordId;
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(sqlDelete);
    }

    /**
     * Deletes a weight entry with an in-progress status (permanentRecord = N)
     */
    public void deleteInProgressWeightEntry() {
        String sqlDelete = "delete from " + WEIGHT_HISTORY_TABLE + " WHERE permanentRecord = 'N'";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(sqlDelete);
    }

    /**
     * Sets an edit state to permanent where it is in edit
     */
    public void resetEditStatusForWeightEntries() {
        String sqlDelete = "UPDATE " + WEIGHT_HISTORY_TABLE + " SET permanentRecord = 'Y' WHERE permanentRecord = 'E'";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(sqlDelete);
    }

    /**
     * Returns Difference between min and max weight difference based on weight entries
     * @return weight difference
     */
    public Double getMinAndMaxWeightDifference() {
        getAllWeightEntriesForUser();
        String sqlWeightStart = "SELECT entryweight FROM " + WEIGHT_HISTORY_TABLE +
                " WHERE userId = " + userId + " " +
                " AND entryDate = (SELECT min(entryDate) FROM " + WEIGHT_HISTORY_TABLE +
                " WHERE userId = " + userId + ")";

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(sqlWeightStart, null);
        cursor.moveToFirst();
        if (cursor.getCount()>0) {
            Long strStartWeight = cursor.getLong(0);
            Double startWeight = Double.parseDouble(strStartWeight.toString());

            String sqlEndWeight = "SELECT entryweight FROM " + WEIGHT_HISTORY_TABLE +
                    " WHERE userId = " + userId + " " +
                    " AND entryDate = (SELECT max(entryDate) FROM " + WEIGHT_HISTORY_TABLE +
                    " WHERE userId = " + userId + ")";

            cursor = db.rawQuery(sqlEndWeight, null);
            cursor.moveToFirst();

            Long strEndWeight = cursor.getLong(0);
            Double endWeight = Double.parseDouble(strEndWeight.toString());

            return endWeight - startWeight;
        }
        return 0.0;
    }

    /**
     * Returns the difference between the earliest and latest history dates
     * @return date difference
     */
    public HashMap<String, Long> selectMinAndMaxHistoryDate() {
        HashMap<String, Long> hashMap = new HashMap<>();

        String sqlUserProfileSelect = "SELECT min(entryDate), max(entryDate) FROM " + WEIGHT_HISTORY_TABLE + " WHERE userId = " + userId +
                " AND permanentRecord ='Y'";

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(sqlUserProfileSelect, null);
        cursor.moveToFirst();

        if (cursor.getCount() > 0) {
            hashMap.put("min",cursor.getLong(0));
            hashMap.put("max",cursor.getLong(1));
        }

        cursor.close();
        return hashMap;
    }

    /**
     * Updates current weight in user profile based on weight entry
     */
    public void updateCurrentWeight() {
        String sqlUpdateCurrentWeight = "UPDATE " + USER_PROFILE_TABLE + " SET currentWeight ="
                 +  " (SELECT entryWeight FROM " + WEIGHT_HISTORY_TABLE
                + " WHERE userId=" + userId
                + " AND entryDate = (SELECT max(entryDate) FROM " + WEIGHT_HISTORY_TABLE
                + " WHERE userId=" + userId + "))"
                + " WHERE id=" + userId;
        SQLiteDatabase db = this.getWritableDatabase();


        db.execSQL(sqlUpdateCurrentWeight);
    }

    /**
     * Util to get weight entry from a cursor
     * @param cursor
     * @return
     */
    public WeightHistoryRecord getWeightEntryFromCursor(Cursor cursor) {
        Long dateMillis = null;
        if (cursor.getString(3) != null && !cursor.getString(3).isEmpty()) {
            dateMillis = Long.parseLong(cursor.getString(3));
        }

        WeightHistoryRecord weightHistoryRecord = new WeightHistoryRecord(cursor.getInt(0), cursor.getInt(1), cursor.getDouble(2), dateMillis,
                cursor.getString(4), cursor.getString(5));

        return weightHistoryRecord;
    }


    // Testing purposes only
    public void cleanEnvironment() {
        resetDb();
    }
    public void resetDb() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("drop table if exists " + USER_PROFILE_TABLE);
        db.execSQL("drop table if exists " + WEIGHT_HISTORY_TABLE);
        db.execSQL("drop table if exists " + LOGIN_TABLE);

        db.execSQL(createUserProfileTable());
        db.execSQL(createWeightHistoryTable());
        db.execSQL(createLoginTable());
    }

    public void createFirstUser() throws ParseException {
        SQLiteDatabase db = this.getWritableDatabase();

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        Date goal = sdf.parse("06/01/25");
        Long goalDate = goal.getTime();

        UserProfile userProfile = new UserProfile(null, "Chris", 240.5, 200.5, goalDate, "male", 6, 2);
        insertIntoUserProfile(userProfile);

        String makeActiveUser = "UPDATE UserProfile SET activeUser = 'Y'";
        db.execSQL(makeActiveUser);

        String verifyCount = "SELECT count(*) FROM UserProfile";
        Cursor cursor = db.rawQuery(verifyCount, null);
        cursor.moveToFirst();

        cursor.close();
        db.close();
    }




    //TEST
    public List<WeightHistoryRecord> getAllWeightEntriesForUser() {
        ArrayList<WeightHistoryRecord> weightEntries = new ArrayList<>();

        String sqlUserProfileSelect = "SELECT * FROM " + WEIGHT_HISTORY_TABLE + " WHERE userid=" +userId;

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(sqlUserProfileSelect, null);
        cursor.moveToFirst();

        if (cursor.getCount()>0) {
            do {

                WeightHistoryRecord weightHistoryRecord = getWeightEntryFromCursor(cursor);

                weightEntries.add(weightHistoryRecord);
                System.out.println("Weight entry: " + weightHistoryRecord);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return weightEntries;
    }

}
