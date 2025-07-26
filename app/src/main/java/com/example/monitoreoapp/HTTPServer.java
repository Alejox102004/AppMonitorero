package com.example.monitoreoapp;

import android.content.Context;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Environment;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

public class HTTPServer extends NanoHTTPD {

    private final Context context;
    private final DatabaseHelper dbHelper;

    public HTTPServer(Context context, DatabaseHelper dbHelper) throws IOException {
        super(8080);
        this.context = context;
        this.dbHelper = dbHelper;
    }

    @Override
    public Response serve(IHTTPSession session) {
        Map<String, String> headers = session.getHeaders();
        String authHeader = headers.get("authorization");

        if (!dbHelper.isValidToken(authHeader)) {
            return newFixedLengthResponse(Response.Status.UNAUTHORIZED, "application/json", "{\"error\": \"Unauthorized\"}");
        }

        String uri = session.getUri();
        Method method = session.getMethod();

        try {
            if (method == Method.GET && uri.equals("/api/sensor_data")) {
                Map<String, String> params = session.getParms();
                String start = params.get("start_time");
                String end = params.get("end_time");

                if (start == null || end == null) {
                    return newFixedLengthResponse(Response.Status.BAD_REQUEST, "application/json", "{\"error\": \"Missing parameters\"}");
                }

                JSONArray data = dbHelper.getSensorData(start, end);
                return newFixedLengthResponse(Response.Status.OK, "application/json", data.toString());

            } else if (method == Method.GET && uri.equals("/api/device_status")) {
                JSONObject status = new JSONObject();
                status.put("battery", getBatteryLevel());
                status.put("network", getNetworkStatus());
                status.put("storage", getAvailableStorage());
                status.put("os_version", Build.VERSION.RELEASE);
                status.put("device_model", Build.MODEL);
                return newFixedLengthResponse(Response.Status.OK, "application/json", status.toString());

            } else {
                return newFixedLengthResponse(Response.Status.NOT_FOUND, "application/json", "{\"error\": \"Not found\"}");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, "application/json", "{\"error\": \"Server error\"}");
        }
    }

    private int getBatteryLevel() {
        BatteryManager bm = (BatteryManager) context.getSystemService(Context.BATTERY_SERVICE);
        return bm != null ? bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY) : -1;
    }

    private String getNetworkStatus() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm != null ? cm.getActiveNetworkInfo() : null;
        return (info != null && info.isConnected()) ? info.getTypeName() : "Desconectado";
    }

    private String getAvailableStorage() {
        long bytesAvailable = Environment.getDataDirectory().getFreeSpace();
        long megabytes = bytesAvailable / (1024 * 1024);
        return megabytes + " MB libres";
    }
}
