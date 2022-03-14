package uk.ac.bath.dietpi;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHandler extends SQLiteOpenHelper {
    private static final String DB_NAME = "dietdb";
    private static final int DB_VERSION = 1;

    // Constructor Method
    public DBHandler(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    // Creates a database
    @Override
    public void onCreate(SQLiteDatabase sqLiteDB) {
        // Creates an SQL Query to create the food table and executes it
        String query = "CREATE TABLE tblFood (Food_ID INTEGER PRIMARY KEY AUTOINCREMENT)";
        sqLiteDB.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDB, int i, int i1) {
        // If the table exists, delete it
        sqLiteDB.execSQL("DROP TABLE IF EXISTS tblFood");

        // Create table
        onCreate(sqLiteDB);
    }
}
