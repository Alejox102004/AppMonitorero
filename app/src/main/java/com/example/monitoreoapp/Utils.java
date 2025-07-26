package com.example.monitoreoapp;

import android.content.Context;
import android.provider.Settings;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utils {

    // Obtiene un identificador único del dispositivo (no requiere permisos especiales)
    public static String getDeviceId(Context context) {
        return Settings.Secure.getString(
                context.getContentResolver(), Settings.Secure.ANDROID_ID
        );
    }

    // Formatea una fecha actual como string: "yyyy-MM-dd HH:mm:ss"
    public static String getCurrentTimestamp() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                .format(new Date());
    }

    // (Opcional) Convierte bytes a MB
    public static String bytesToMB(long bytes) {
        return String.format(Locale.getDefault(), "%.2f MB", bytes / (1024.0 * 1024.0));
    }
}
