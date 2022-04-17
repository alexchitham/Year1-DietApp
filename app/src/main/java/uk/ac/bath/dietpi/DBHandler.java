

package uk.ac.bath.dietpi;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

public class DBHandler extends SQLiteOpenHelper {
    // Constants for the database
    private static final String DB_NAME = "dietdb";
    private static final int DB_VERSION = 5;

    // Constants for the Eaten Table
    private static final String TBL_EATEN = "tblEaten";
    private static final String COLUMN_EATEN_ID = "Eaten_ID";
    private static final String COLUMN_DATE = "Date"; //Date is text in form YYYY/MM/DD

    // Constants for both Eaten and Food Table
    private static final String COLUMN_FOOD_ID = "Food_ID";

    // Constants for Food Table
    private static final String TBL_FOOD = "tblFood";
    private static final String COLUMN_FOOD_NAME = "Food_Name";
    private static final String COLUMN_CALORIES = "Calories";
    private static final String COLUMN_CARBOHYDRATES = "Carbohydrates";
    private static final String COLUMN_PROTEIN = "Protein";
    private static final String COLUMN_FAT = "Fat";

    // Constant for View
    private static final String VIEW_EATEN_DETAILS = "viewEatenDetails";

    // Constants for User Details Table
    private static final String TBL_USER_DETAILS = "tblUserDetails";
    private static final String COLUMN_FIRST_NAME = "First_Name";
    private static final String COLUMN_AGE = "Age";
    private static final String COLUMN_GENDER = "Gender";
    private static final String COLUMN_HEIGHT = "Height";
    private static final String COLUMN_WEIGHT = "Weight";
    private static final String COLUMN_DETAILS_ID = "detailsID";

    // Constructor Method
    public DBHandler(@Nullable Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    // Insert new food into tblFood
    public void insertNewFood(String food_name, double calories, double carbohydrates,
                              double protein, double fat){
        SQLiteDatabase sqLiteDB = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        // Set record values
        values.put(COLUMN_FOOD_NAME, food_name);
        values.put(COLUMN_CALORIES, calories);
        values.put(COLUMN_CARBOHYDRATES, carbohydrates);
        values.put(COLUMN_PROTEIN, protein);
        values.put(COLUMN_FAT, fat);

        // Insert record into database
        sqLiteDB.insert(TBL_FOOD, null, values);

        sqLiteDB.close();
    }

    // Return food_id (or -1) of matching food in tblFood
    public int getFoodID(String food_name){
        SQLiteDatabase sqLiteDB = this.getReadableDatabase();
        int food_id = -1;

        // Query to select food_id of matching foodname
        String query = "SELECT "+ COLUMN_FOOD_ID +
                " FROM " + TBL_FOOD +
                " WHERE " + COLUMN_FOOD_NAME + "='" + food_name + "';";

        Cursor cursor = sqLiteDB.rawQuery(query, null);

        if (cursor.moveToFirst()){
            // Take food_id of first record that matches
            food_id = cursor.getInt(0);
        }
        cursor.close();
        sqLiteDB.close();
        return food_id;
    }



    // Insert Data
    public void insert(String food_name, double calories, double carbohydrates,
                       double protein, double fat){
        // Get food id of the food (or insert new food into record)
        int food_id = getFoodID(food_name);
        if (food_id == -1){
            insertNewFood(food_name, calories, carbohydrates, protein, fat);
            food_id = getFoodID(food_name);
        }

        // Get current date
        String date = getDate();

        // Insert Food record into eaten
        SQLiteDatabase sqLiteDB = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        Log.d("Test", "Reached here");
        // Set record values
        values.put(COLUMN_FOOD_ID, food_id);
        values.put(COLUMN_DATE, date);

        // Insert record into database
        sqLiteDB.insert(TBL_EATEN, null, values);

        sqLiteDB.close();
    }

    // Retrieve whole table
    public List<ContentValues> retrieveTable() {
        SQLiteDatabase db = this.getWritableDatabase();


        // Create temporary view with all columns joined from TBL_Eaten and TBL_Food
        String viewQuery = "CREATE TEMP VIEW " + VIEW_EATEN_DETAILS + " AS " +
                "SELECT " + COLUMN_EATEN_ID + "," + COLUMN_FOOD_NAME + "," + COLUMN_CALORIES + "," + COLUMN_CARBOHYDRATES + "," +
                COLUMN_PROTEIN + "," + COLUMN_PROTEIN + "," + COLUMN_FAT + "," + COLUMN_DATE +
                " FROM " + TBL_EATEN + ", "+ TBL_FOOD +
                " WHERE " + TBL_EATEN + "." + COLUMN_FOOD_ID + "=" + TBL_FOOD + "." + COLUMN_FOOD_ID + ";";

        db.execSQL(viewQuery);

        // Select All Query
        String selectQuery = "SELECT  * FROM " + VIEW_EATEN_DETAILS;
        List<ContentValues> contentList = new ArrayList<>();

        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ContentValues values = new ContentValues();
                values.put(COLUMN_EATEN_ID, Integer.parseInt(cursor.getString(0)));
                values.put(COLUMN_FOOD_NAME, cursor.getString(1));
                values.put(COLUMN_CALORIES, cursor.getString(2));
                values.put(COLUMN_CARBOHYDRATES, cursor.getString(3));
                values.put(COLUMN_PROTEIN, cursor.getString(4));
                values.put(COLUMN_FAT, cursor.getString(5));
                values.put(COLUMN_DATE, cursor.getString(6));
                // Adding contact to list
                contentList.add(values);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return contentList;
    }

    // Calculate totals of macro-nutrients (might be split into separate methods, or use of general method)
    public Hashtable<String,Float> retrieveTotal(){
        // Retrieve the whole database
        SQLiteDatabase db = this.getWritableDatabase();

        // Get current date
        String date = getDate();

        // Initialize the query
        String sumQuery = "SELECT SUM(" + TBL_FOOD + "." + COLUMN_CALORIES +"), SUM("+ TBL_FOOD +"." + COLUMN_CARBOHYDRATES +"), SUM("+ TBL_FOOD +"." + COLUMN_PROTEIN +"), SUM(" + TBL_FOOD +"." + COLUMN_FAT + ") " +
                "FROM " + TBL_EATEN +" INNER JOIN " + TBL_FOOD + " ON " + TBL_EATEN + "." + COLUMN_FOOD_ID +"="+TBL_FOOD + "." + COLUMN_FOOD_ID +
                " WHERE " + TBL_EATEN + "." + COLUMN_DATE + "= '" + date + "';";
        Hashtable<String,Float> result = new Hashtable<String,Float>();

        Cursor cursor = db.rawQuery(sumQuery, null);

        // Adds the sum of each column into the hashtable
        if (cursor.moveToFirst()) {
            if (cursor.getString(0) == null) {
                result.put(COLUMN_CALORIES, 0f);
            } else {
                result.put(COLUMN_CALORIES, Float.parseFloat(cursor.getString(0)));
            }

            if (cursor.getString(1) == null) {
                result.put(COLUMN_CARBOHYDRATES, 0f);
            } else {
                result.put(COLUMN_CARBOHYDRATES, Float.parseFloat(cursor.getString(1)));
            }

            if (cursor.getString(2) == null) {
                result.put(COLUMN_PROTEIN, 0f);
            } else {
                result.put(COLUMN_PROTEIN, Float.parseFloat(cursor.getString(2)));
            }

            if (cursor.getString(3) == null) {
                result.put(COLUMN_FAT, 0f);
            } else {
                result.put(COLUMN_FAT, Float.parseFloat(cursor.getString(3)));
            }
        }
        else{
            result.put(COLUMN_CALORIES, 0f);
            result.put(COLUMN_CARBOHYDRATES, 0f);
            result.put(COLUMN_PROTEIN, 0f);
            result.put(COLUMN_FAT,0f);
        }

        // Closes the cursor and database connection
        cursor.close();
        db.close();
        return result;
    }

    // Inserts new record into user details
    public void newUserDetails(String firstName, int age, int gender, double height, double weight){
        // Get current date
        String date = getDate();

        // Insert new user details into record
        SQLiteDatabase sqLiteDB = this.getWritableDatabase();
        ContentValues values = new ContentValues();
;
        // Set record values
        values.put(COLUMN_DATE, date);
        values.put(COLUMN_FIRST_NAME, firstName);
        values.put(COLUMN_AGE, age);
        values.put(COLUMN_GENDER, gender); // 0 = Male, 1 = Female, 2 = Other
        values.put(COLUMN_HEIGHT, height);
        values.put(COLUMN_WEIGHT, weight);

        // Insert record into database
        sqLiteDB.insert(TBL_USER_DETAILS, null, values);

        sqLiteDB.close();
    }

    // Returns latest users details
    public ContentValues getCurrentUserDetails(){
        // Setup Query to get latest users details
        SQLiteDatabase db = this.getWritableDatabase();
        String sumQuery = "SELECT " + COLUMN_FIRST_NAME + "," + COLUMN_AGE + "," + COLUMN_GENDER + "," + COLUMN_HEIGHT + "," + COLUMN_WEIGHT + " " +
                "FROM " + TBL_USER_DETAILS + " " +
                "WHERE " + COLUMN_DETAILS_ID + "= ( SELECT MAX(" + COLUMN_DETAILS_ID + ") FROM " + TBL_USER_DETAILS +");";

        // Execute Query
        Cursor cursor = db.rawQuery(sumQuery, null);

        // Get user details into ContentsValues
        ContentValues userDetails = new ContentValues();
        // If there's a result
        if (cursor.moveToFirst()) {
            userDetails.put(COLUMN_FIRST_NAME, cursor.getString(0));
            userDetails.put(COLUMN_AGE, cursor.getInt(1));
            userDetails.put(COLUMN_GENDER, cursor.getInt(2));
            userDetails.put(COLUMN_HEIGHT, cursor.getDouble(3));
            userDetails.put(COLUMN_WEIGHT, cursor.getDouble(4));
        }
        else{
            return null;
        }
        return userDetails;
    }

    public String getDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date currentDate = new Date();
        return dateFormat.format(currentDate);
    }


    // Creates a database, called the first time a database is accessed
    @Override
    public void onCreate(SQLiteDatabase sqLiteDB) {
        // Creates an SQL Query to create the food table and executes it
        String query1 = "CREATE TABLE " + TBL_FOOD + " (" +
                COLUMN_FOOD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_FOOD_NAME + " TEXT, " +
                COLUMN_CALORIES + " REAL, " +
                COLUMN_CARBOHYDRATES + " REAL," +
                COLUMN_PROTEIN + " REAL," +
                COLUMN_FAT + " REAL" +
                ");";
        sqLiteDB.execSQL(query1);

        // Creates an SQL Query to create the eaten table and executes it
        String query2 = "CREATE TABLE " + TBL_EATEN + "( " +
                COLUMN_EATEN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_FOOD_ID + " INTEGER, " +
                COLUMN_DATE + " TEXT, " +
                "FOREIGN KEY(" + COLUMN_FOOD_ID + ") REFERENCES " + TBL_FOOD + "(" + COLUMN_FOOD_ID + ")" +
                ");";
        sqLiteDB.execSQL(query2);

        // Creates an SQL Query to create the user details table and executes it
        String query3 = "CREATE TABLE " + TBL_USER_DETAILS + "( " +
                COLUMN_DETAILS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_DATE + " TEXT, " +
                COLUMN_FIRST_NAME + " TEXT, " +
                COLUMN_AGE + " INTEGER, " +
                COLUMN_GENDER + " INTEGER, " +
                COLUMN_HEIGHT + " REAL, " +
                COLUMN_WEIGHT + " REAL);";
        sqLiteDB.execSQL(query3);
    }

    // Ensure that any users with an outdated db schema gets the new schema
    // Called if the database version changes
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDB, int oldVersion, int newVersion) {
        // If the table exists, delete it
        sqLiteDB.execSQL("DROP TABLE IF EXISTS " + TBL_EATEN);
        sqLiteDB.execSQL("DROP TABLE IF EXISTS " + TBL_FOOD);
        sqLiteDB.execSQL("DROP TABLE IF EXISTS " + TBL_USER_DETAILS);

        // Create table
        onCreate(sqLiteDB);
    }

    public static String getDbName() {
        return DB_NAME;
    }

    public static int getDbVersion() {
        return DB_VERSION;
    }

    public static String getTblEaten() {
        return TBL_EATEN;
    }

    public static String getColumnEatenId() {
        return COLUMN_EATEN_ID;
    }

    public static String getColumnFoodName() {
        return COLUMN_FOOD_NAME;
    }

    public static String getColumnCalories() {
        return COLUMN_CALORIES;
    }

    public static String getColumnCarbohydrates() {
        return COLUMN_CARBOHYDRATES;
    }

    public static String getColumnProtein() {
        return COLUMN_PROTEIN;
    }

    public static String getColumnFat() {
        return COLUMN_FAT;
    }

    public static String getColumnDate() {
        return COLUMN_DATE;
    }

    public static String getColumnFirstName() {
        return COLUMN_FIRST_NAME;
    }

    public static String getColumnAge() {
        return COLUMN_AGE;
    }

    public static String getColumnGender() {
        return COLUMN_GENDER;
    }

    public static String getColumnHeight() {
        return COLUMN_HEIGHT;
    }

    public static String getColumnWeight() {
        return COLUMN_WEIGHT;
    }

}
