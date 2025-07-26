package com.example.monitoreoapp;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.IBinder;
import android.text.format.Formatter;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class MainActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST = 100;
    private TextView statusText, ipText;
    private Button startButton, stopButton;
    private boolean isServiceRunning = false;
    private HTTPServer server;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        statusText = findViewById(R.id.statusText);
        ipText = findViewById(R.id.ipText);
        startButton = findViewById(R.id.startButton);
        stopButton = findViewById(R.id.stopButton);

        requestPermissions();

        String ipAddress = getLocalIpAddress();
        ipText.setText("IP Local: " + ipAddress + ":8080");

        startButton.setOnClickListener(v -> {
            startService(new Intent(this, GPSService.class));
            startHTTPServer();
            isServiceRunning = true;
            statusText.setText("Servicio en ejecución...");
        });

        stopButton.setOnClickListener(v -> {
            stopService(new Intent(this, GPSService.class));
            stopHTTPServer();
            isServiceRunning = false;
            statusText.setText("Servicio detenido.");
        });
    }

    private void startHTTPServer() {
        try {
            if (server == null) {
                server = new HTTPServer(this, new DatabaseHelper(this));
                server.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopHTTPServer() {
        if (server != null) {
            server.stop();
            server = null;
        }
    }

    private String getLocalIpAddress() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        return Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress());
    }

    private void requestPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST && grantResults.length > 0 &&
                grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            statusText.setText("Permisos de ubicación requeridos.");
        }
    }
}
