package edu.llapp.infra;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Configuration loader
 * Read settings from application.properties
 */
public class ConfigLoader {
    private static Properties properties;

    static {
        properties = new Properties();
        try (InputStream input = ConfigLoader.class.getClassLoader()
                .getResourceAsStream("application.properties")) {
            if (input == null) {
                System.err.println("Unable to find application.properties");
            } else {
                properties.load(input);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getYouTubeApiKey() {
        return properties.getProperty("youtube.api.key");
    }

    public static String getYouTubeBaseUrl() {
        return properties.getProperty("youtube.api.base.url",
                "https://www.googleapis.com/youtube/v3");
    }

    public static int getApiTimeout() {
        return Integer.parseInt(properties.getProperty("youtube.api.timeout.ms", "3000"));
    }

    public static int getMaxRetries() {
        return Integer.parseInt(properties.getProperty("youtube.api.max.retries", "2"));
    }

    public static int getCacheTTL() {
        return Integer.parseInt(properties.getProperty("cache.ttl.minutes", "30"));
    }

    public static boolean isFallbackEnabled() {
        return Boolean.parseBoolean(properties.getProperty("fallback.enabled", "true"));
    }
}