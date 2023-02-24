package ru.job4j.utils;

import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtil {

    private Properties properties = new Properties();
    private String path;

    public String get(String key) {
        return this.properties.getProperty(key);
    }

    public PropertiesUtil(String path) {
        this.path = path;
        loadProperties();
    }

    public void loadProperties() {
        try (InputStream in = PropertiesUtil.class.getClassLoader().getResourceAsStream(this.path)) {
            this.properties.load(in);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}