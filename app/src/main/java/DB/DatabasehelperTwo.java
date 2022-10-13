package DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class DatabasehelperTwo extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelperTwo";

    private static final String DATABASE_NAME = "OtherInfoDB";

    private static final String VOLUNTEERS_TABLE_NAME = "volunteers_table";
    private static final String VOLUNTEERS_COL_ID = "ID";
    private static final String VOLUNTEERS_COL_NAME = "name";
    private static final String VOLUNTEERS_COL_ADDRESS = "address";

    private static final String REQUESTS_TABLE_NAME = "requests_table";
    private static final String REQUESTS_COL_ID = "ID";
    private static final String REQUESTS_COL_SERVICEID = "service_id";
    private static final String REQUESTS_COL_RESIDENTID = "resident_id";
    private static final String REQUESTS_COL_VOLUNTEERID = "volunteer_id";



    public DatabasehelperTwo(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableVolunteers = "CREATE TABLE " + VOLUNTEERS_TABLE_NAME + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                VOLUNTEERS_COL_NAME + " TEXT, " +
                VOLUNTEERS_COL_ADDRESS + " TEXT)";
        db.execSQL(createTableVolunteers);

        String createTableRequests = "CREATE TABLE " + REQUESTS_TABLE_NAME + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                REQUESTS_COL_SERVICEID + " INTEGER, " +
                REQUESTS_COL_RESIDENTID + " INTEGER, " +
                REQUESTS_COL_VOLUNTEERID + " INTEGER)";
        db.execSQL(createTableRequests);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + VOLUNTEERS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + REQUESTS_TABLE_NAME);
        onCreate(db);

    }

    //VOLUNTEER MANAGMENT DATA - START
    public boolean addVolunteerData(String name, String address){
        SQLiteDatabase db1 = this.getWritableDatabase();
        ContentValues contentValues1 = new ContentValues();
        contentValues1.put(VOLUNTEERS_COL_NAME, name);
        contentValues1.put(VOLUNTEERS_COL_ADDRESS, address);

        Log.d(TAG, "addVolunteerData: Adding " + name + " to " + VOLUNTEERS_TABLE_NAME);

        long result = db1.insert(VOLUNTEERS_TABLE_NAME, null, contentValues1);
        // if data inserted incorrectly it will return -1
        if (result == -1){
            return false;
        } else {
            return true;
        }
    }

    //returns all data from the database
    public Cursor getAllVolunteerData(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + VOLUNTEERS_TABLE_NAME;

        Cursor data = db.rawQuery(query , null);
        return data;
    }

    //returns only the ID that matches the name passed in
    public Cursor getVolunteerItemID(String name){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + VOLUNTEERS_COL_ID + " FROM " + VOLUNTEERS_TABLE_NAME +
                " WHERE " + VOLUNTEERS_COL_NAME + " = '" + name + "'";
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    //delete volunteer from database
    public void deleteVolunteer(int id, String name){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + VOLUNTEERS_TABLE_NAME + " WHERE "
                + VOLUNTEERS_COL_ID + " = '" + id + "'" +
                " AND " + VOLUNTEERS_COL_NAME + " = '" + name + "'";
        Log.d(TAG, "deleteName: query: " + query);
        Log.d(TAG, "deleteName: Deleting " + name + "from database.");
        db.execSQL(query);
    }
    //VOLUNTEER MANAGMENT DATA - END








}
