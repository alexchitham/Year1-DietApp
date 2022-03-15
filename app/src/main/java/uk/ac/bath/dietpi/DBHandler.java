package uk.ac.bath.dietpi;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHandler extends SQLiteOpenHelper {
    // Constants for the database
    private static final String DB_NAME = "dietdb";
    private static final int DB_VERSION = 1;

    // Constants for the Eaten Table
    private static final String TBL_EATEN = "tblEaten";
    private static final String COLUMN_EATEN_ID = "Eaten_ID";
    private static final String COLUMN_FOOD_NAME = "Food_Name";
    private static final String COLUMN_CALORIES = "Calories";
    private static final String COLUMN_CARBOHYDRATES = "Carbohydrates";
    private static final String COLUMN_PROTEIN = "Protein";
    private static final String COLUMN_FAT = "Fat";


    // Constructor Method
    public DBHandler(@Nullable Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    // Insert Data
    public void insert(){
        SQLiteDatabase sqLiteDB = this.getWritableDatabase();

        // Temporary code just to test database, this is not the best way to insert data
        sqLiteDB.execSQL("INSERT INTO "+TBL_EATEN+" VALUES (1, 'Banana', 89, 23, 1.1, 0.3);");
    }

    // Retrieve whole table
    public void retrieveTable(){
        return;
    }

    // Calculate totals of macro-nutrients (might be split into separate methods, or use of general method)
    public void retrieveTotal(){
        return;
    }



    // Creates a database, called the first time a database is accessed
    @Override
    public void onCreate(SQLiteDatabase sqLiteDB) {
        // Creates an SQL Query to create the food table and executes it
        String query = "CREATE TABLE " + TBL_EATEN + " (" +
                COLUMN_EATEN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_FOOD_NAME + " TEXT, " +
                COLUMN_CALORIES + " REAl, " +
                COLUMN_CARBOHYDRATES + " REAl," +
                COLUMN_PROTEIN + " REAL," +
                COLUMN_FAT + " REAL" +
                ")";
        sqLiteDB.execSQL(query);
    }

    // Ensure that any users with an outdated db schema gets the new schema
    // Called if the database version changes
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDB, int oldVersion, int newVersion) {
        // If the table exists, delete it
        sqLiteDB.execSQL("DROP TABLE IF EXISTS tblFood");

        // Create table
        onCreate(sqLiteDB);
    }
}
