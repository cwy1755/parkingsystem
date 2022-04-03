package com.parkit.parkingsystem.config;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface IDataBaseConfig {

  public Connection getConnection() throws ClassNotFoundException, SQLException, IOException;
  public void closeConnection(Connection con);
  public void closePreparedStatement(PreparedStatement ps);
  public void closeResultSet(ResultSet rs);

}
