package com.parkit.parkingsystem.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesReaderUtil {
  private Properties properties;

  public PropertiesReaderUtil(String propertyFileName) throws IOException {
    InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propertyFileName);
    this.properties = new Properties();
    this.properties.load(inputStream);
  }

  public String getProperty(String propertyName) {
    return this.properties.getProperty(propertyName);
  }
}
