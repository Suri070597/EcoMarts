/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package db;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author HuuDuc
 */

/**
 * Loads environment variables from a .env file in resources.
 */
public class Dotenv {
    private static final Logger LOGGER = Logger.getLogger(Dotenv.class.getName());
    private final Map<String, String> env;

    private Dotenv() {
        this.env = new HashMap<>();
    }

    /**
     * Creates a Dotenv instance configured to load .env from resources.
     *
     * @return A configured Dotenv instance.
     */
    public static Dotenv configure() {
        return new Dotenv();
    }

    /**
     * Loads .env file from resources (WEB-INF/classes/.env).
     *
     * @throws IllegalStateException if .env file is not found or cannot be read.
     */
    public Dotenv load() {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(".env")) {
            if (is == null) {
                LOGGER.severe("No .env file found in src/main/resources/");
                throw new IllegalStateException("No .env file found in resources");
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("#")) {
                        continue;
                    }
                    String[] parts = line.split("=", 2);
                    if (parts.length == 2) {
                        String key = parts[0].trim();
                        String value = parts[1].trim();
                        if (!key.isEmpty() && !value.isEmpty()) {
                            env.put(key, value);
                            LOGGER.info("Loaded env variable: " + key + "=****");
                        } else {
                            LOGGER.warning("Invalid env entry: " + line);
                        }
                    } else {
                        LOGGER.warning("Skipping malformed env entry: " + line);
                    }
                }
                // Validate required keys
                String[] requiredKeys = {"JDBC_URL", "DB_USER", "DB_PASSWORD", "SMTP_HOST", "SMTP_PORT", "SMTP_USER", "SMTP_PASSWORD"};
                for (String key : requiredKeys) {
                    if (!env.containsKey(key)) {
                        LOGGER.severe("Missing required env variable: " + key);
                        throw new IllegalStateException("Missing required env variable: " + key);
                    }
                }
                LOGGER.info(".env file loaded successfully with " + env.size() + " variables");
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to load .env file: " + e.getMessage(), e);
            throw new IllegalStateException("Failed to load .env file: " + e.getMessage(), e);
        }
        return this;
    }

    /**
     * Retrieves the value of an environment variable.
     *
     * @param key The key of the environment variable.
     * @return The value, or null if not found.
     */
    public String get(String key) {
        String value = env.get(key);
        if (value == null) {
            LOGGER.warning("Environment variable not found: " + key);
        }
        return value;
    }
}