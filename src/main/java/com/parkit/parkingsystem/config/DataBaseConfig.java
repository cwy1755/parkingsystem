package com.parkit.parkingsystem.config;

import com.parkit.parkingsystem.util.PropertiesReader;
import java.io.IOException;
import java.sql.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DataBaseConfig {

  private static final Logger logger = LogManager.getLogger("DataBaseConfig");

  public Connection getConnection() throws ClassNotFoundException, SQLException, IOException {
    logger.trace("Create DB connection");

    PropertiesReader reader = new PropertiesReader("application.properties");

    String env = reader.getProperty("database.env");
    logger.trace("DB application.properties: " + env);

    String url = reader.getProperty("database.url");
    String user = reader.getProperty("database.user");
    String password = reader.getProperty("database.password");

    Class.forName("com.mysql.cj.jdbc.Driver");
    return DriverManager.getConnection(url, user, password);

  }

  public void closeConnection(Connection con) {
    if (con != null) {
      try {
        con.close();
        logger.trace("Closing DB connection");
      } catch (SQLException e) {
        logger.error("Error while closing connection", e);
      }
    }
  }

  public void closePreparedStatement(PreparedStatement ps) {
    if (ps != null) {
      try {
        ps.close();
        logger.trace("Closing Prepared Statement");
      } catch (SQLException e) {
        logger.error("Error while closing prepared statement", e);
      }
    }
  }

  public void closeResultSet(ResultSet rs) {
    if (rs != null) {
      try {
        rs.close();
        logger.trace("Closing Result Set");
      } catch (SQLException e) {
        logger.error("Error while closing result set", e);
      }
    }
  }
}
