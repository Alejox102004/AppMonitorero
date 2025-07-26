package com.example.monitoreoapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONArray;
import org.json.JSONObject;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "monitoreo.db";
    private static final int DATABASE_VERSION = 1;

    // Tabla GPS
    private static final String TABLE_SENSOR = "sensor_data";
    private static final String CREATE_SENSOR_TABLE =
            "CREATE TABLE " + TABLE_SENSOR + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "latitude REAL," +
                    "longitude REAL," +
                    "timestamp TEXT," +
                    "device_id TEXT);";

    // Tabla autenticación
    private static final String TABLE_AUTH = "auth";
    private static final String CREATE_AUTH_TABLE =
            "CREATE TABLE " + TABLE_AUTH + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "username TEXT," +
                    "password TEXT," +
                    "token TEXT);";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_SENSOR_TABLE);
        db.execSQL(CREATE_AUTH_TABLE);

        // Insertar un token básico para pruebas
        ContentValues values = new ContentValues();
        values.put("username", "admin");
        values.put("password", "admin123");
        values.put("token", "BearerToken123");
        db.insert(TABLE_AUTH, null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SENSOR);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_AUTH);
        onCreate(db);
    }

    public void insertSensorData(double lat, double lon, String timestamp, String deviceId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("latitude", lat);
        values.put("longitude", lon);
        values.put("timestamp", timestamp);
        values.put("device_id", deviceId);
        db.insert(TABLE_SENSOR, null, values);
    }

    public JSONArray getSensorData(String startTime, String endTime) {
        SQLiteDatabase db = this.getReadableDatabase();
        JSONArray jsonArray = new JSONArray();

        String query = "SELECT * FROM " + TABLE_SENSOR + " WHERE timestamp BETWEEN ? AND ?";
        Cursor cursor = db.rawQuery(query, new String[]{startTime, endTime});

        while (cursor.moveToNext()) {
            JSONObject obj = new JSONObject();
            try {
                obj.put("id", cursor.getInt(0));
                obj.put("latitude", cursor.getDouble(1));
                obj.put("longitude", cursor.getDouble(2));
                obj.put("timestamp", cursor.getString(3));
                obj.put("device_id", cursor.getString(4));
                jsonArray.put(obj);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        cursor.close();
        return jsonArray;
    }

    public boolean isValidToken(String token) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_AUTH + " WHERE token=?", new String[]{token});
        boolean valid = cursor.getCount() > 0;
        cursor.close();
        return valid;
    }
}
