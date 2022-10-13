package DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.newprojectmishanxx.Model.Service;

public class DatabaseHelper  extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";

    private static final String DATABASE_NAME = "MyInfoDB";

    private static final String SERVICES_TABLE_NAME = "services_table";
    private static final String SERVICES_COL_ID = "ID";
    private static final String SERVICES_COL_NAME = "name";

    private static final String[] TABLE_SERVICES_COLUMNS = {SERVICES_COL_ID,  SERVICES_COL_NAME};


    private static final String RESIDNENTS_TABLE_NAME = "residents_table";
    private static final String RESIDNENTS_COL_ID = "ID";
    private static final String RESIDNENTS_COL_NAME = "name";
    private static final String RESIDNENTS_COL_ADDRESS = "address";


    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            // SQL statement to create services table
            String CREATE_SERVICE_TABLE = "create table if not exists " + SERVICES_TABLE_NAME + " ( "
                    + SERVICES_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + SERVICES_COL_NAME + " TEXT)";
            db.execSQL(CREATE_SERVICE_TABLE);

            // SQL statement to create resident table
            String CREATE_RESIDENT_TABLE = "create table if not exists " + RESIDNENTS_TABLE_NAME + " ( "
                    + RESIDNENTS_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + RESIDNENTS_COL_NAME + " TEXT, "
                    + RESIDNENTS_COL_ADDRESS + " TEXT)";
            db.execSQL(CREATE_RESIDENT_TABLE);
        }
        catch (Throwable t) {
            t.printStackTrace();
        }


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

        //droping services table
        db.execSQL("DROP TABLE IF EXISTS " + SERVICES_TABLE_NAME);

        //droping residents table
        db.execSQL("DROP TABLE IF EXISTS " + RESIDNENTS_TABLE_NAME);

        onCreate(db);
    }

    //services table data management - START
    public boolean addServiceData(Service item) {
            // make values to be inserted
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(SERVICES_COL_ID, item.getId());
            values.put(SERVICES_COL_NAME, item.getName());

            // insert item
            long result = db.insert(SERVICES_TABLE_NAME, null, values);

        if (result == -1){
            return false;
        } else {
            return true;
        }
    }




    //returns all data from the database
    public Cursor getAllServicesData(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + SERVICES_TABLE_NAME;

        Cursor data = db.rawQuery(query , null);
        return data;
    }

    //returns only the ID that matches the name passed in
    public Cursor getServiceItemID(String name){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + SERVICES_COL_ID + " FROM " + SERVICES_TABLE_NAME +
                " WHERE " + SERVICES_COL_NAME + " = '" + name + "'";
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    //delete service from database
    public void deleteService(int id, String name){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + SERVICES_TABLE_NAME + " WHERE "
                + SERVICES_COL_ID + " = '" + id + "'" +
                " AND " + SERVICES_COL_NAME + " = '" + name + "'";
        Log.d(TAG, "deleteName: query: " + query);
        Log.d(TAG, "deleteName: Deleting " + name + "from database.");
        db.execSQL(query);
    }
    //services table data management - END




    //Residents table data management - START
    public boolean addResidentData(String name, String address){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(RESIDNENTS_COL_NAME, name);
        contentValues.put(RESIDNENTS_COL_ADDRESS, address);

        Log.d(TAG, "addResidentData: Adding " + name + " to " + RESIDNENTS_TABLE_NAME);

        long result = db.insert(RESIDNENTS_TABLE_NAME, null, contentValues);
        // if data inserted incorrectly it will return -1
        if (result == -1){
            return false;
        } else {
            return true;
        }
    }

    //returns all data from the database
    public Cursor getAllResidentData(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + RESIDNENTS_TABLE_NAME;

        Cursor data = db.rawQuery(query , null);
        return data;
    }

    //returns only the ID that matches the name passed in
    public Cursor getResidentItemID(String name){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + RESIDNENTS_COL_ID + " FROM " + RESIDNENTS_TABLE_NAME +
                " WHERE " + RESIDNENTS_COL_NAME + " = '" + name + "'";
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    //delete service from database
    public void deleteResident(int id, String name){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + RESIDNENTS_TABLE_NAME + " WHERE "
                + RESIDNENTS_COL_ID + " = '" + id + "'" +
                " AND " + RESIDNENTS_COL_NAME + " = '" + name + "'";
        Log.d(TAG, "deleteName: query: " + query);
        Log.d(TAG, "deleteName: Deleting " + name + "from database.");
        db.execSQL(query);
    }
    //Residents table data management - END

}