package pl.krawczyk.maciej.GdzieBylem.datasource;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LocationsDataSource {

    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] allColumns = {MySQLiteHelper.COLUMN_ID, MySQLiteHelper.COLUMN_LAT,
            MySQLiteHelper.COLUMN_LNG, MySQLiteHelper.COLUMN_NAME};

    public LocationsDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public MyLocation createLocation(MyLocation myLocation) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_LAT, myLocation.getLat());
        values.put(MySQLiteHelper.COLUMN_LNG, myLocation.getLng());
        values.put(MySQLiteHelper.COLUMN_NAME, myLocation.getName());
        long insertId = database.insert(MySQLiteHelper.TABLE_LOCATIONS, null, values);
        Cursor cursor = database.query(MySQLiteHelper.TABLE_LOCATIONS,
                allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        MyLocation newMyLocation = cursorToLocation(cursor);
        cursor.close();
        return newMyLocation;
    }

    public List<MyLocation> getAllLocations() {
        List<MyLocation> myLocations = new ArrayList<MyLocation>();
        Cursor cursor = database.query(MySQLiteHelper.TABLE_LOCATIONS,
                allColumns, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            MyLocation myLocation = cursorToLocation(cursor);
            myLocations.add(myLocation);
            cursor.moveToNext();
        }
        cursor.close();
        return myLocations;
    }

    public void editLocation(long idOfLocation, String name){
        ContentValues newValues = new ContentValues();
        newValues.put(MySQLiteHelper.COLUMN_NAME, name);

        database.update(MySQLiteHelper.TABLE_LOCATIONS, newValues, MySQLiteHelper.COLUMN_ID + "=" + idOfLocation, null);
    }

    public void deleteLocationWithId(long idOfLocation) {
        database.delete(MySQLiteHelper.TABLE_LOCATIONS, MySQLiteHelper.COLUMN_ID + " = " + idOfLocation, null);
    }

    public MyLocation getLocationWithId(long idOfLocation){
        MyLocation myLocation = null;
        Cursor cursor = null;
        try {
            cursor = database.query(MySQLiteHelper.TABLE_LOCATIONS, allColumns, MySQLiteHelper.COLUMN_ID + "=" + idOfLocation, null, null, null, null);
            if (cursor.moveToFirst()) {
                myLocation = cursorToLocation(cursor);
            }
            return (myLocation);
        } finally {
            cursor.close();
        }
    }

    private MyLocation cursorToLocation(Cursor cursor) {
        MyLocation myLocation = new MyLocation();
        myLocation.setId(cursor.getLong(0));
        myLocation.setLat(cursor.getDouble(1));
        myLocation.setLng(cursor.getDouble(2));
        myLocation.setName(cursor.getString(3));
        return myLocation;
    }
}