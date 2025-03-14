package com.robonav.app.utilities;

import android.content.Context;
import android.util.Log;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigManager {
    private static final String TAG = "ConfigManager";
    private static final String CONFIG_FILE = "config.properties";
    private static Properties properties = new Properties();

    // Load properties from config.properties
    public static void loadConfig(Context context) {
        try {
            InputStream inputStream = context.getAssets().open(CONFIG_FILE);
            properties.load(inputStream);
            inputStream.close();
        } catch (IOException e) {
            Log.e(TAG, "Error loading configuration file", e);
        }
    }

    // Get backend base URL
    public static String getBaseUrl() {
        boolean useHttps = Boolean.parseBoolean(properties.getProperty("USE_HTTPS", "false"));
        String protocol = useHttps ? "https" : "http";
        String ip = properties.getProperty("BACKEND_IP", "10.0.2.2");
        String port = properties.getProperty("BACKEND_PORT", "8080");
        return protocol + "://" + ip + ":" + port;
    }
}
