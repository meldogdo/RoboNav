package com.robonav.app.utilities;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigManager {
    private static final Properties properties = new Properties();

    static {
        try {
            FileInputStream fis = new FileInputStream("config.properties");
            properties.load(fis);
            fis.close();
        } catch (IOException e) {
            System.err.println("Error loading configuration file: " + e.getMessage());
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
