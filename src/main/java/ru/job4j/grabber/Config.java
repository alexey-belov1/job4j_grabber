package ru.job4j.grabber;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
    private static final Logger LOG = LogManager.getLogger(Config.class.getName());

    private final Properties values = new Properties();

    public Config() {
        try (InputStream in = Config.class.getClassLoader().getResourceAsStream("grabber.properties")) {
            values.load(in);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    public String get(String key) {
        return this.values.getProperty(key);
    }
}