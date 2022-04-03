package com.parkit.parkingsystem.dao;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.config.IDataBaseConfig;
import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.util.OutputWriterlUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class ParkingSpotDAO implements IParkingSpotDAO{
  private static final Logger logger = LogManager.getLogger("ParkingSpotDAO");
  private static OutputWriterlUtil outputWriterUtil = new OutputWriterlUtil(); 

  public IDataBaseConfig dataBaseConfig = new DataBaseConfig();

  public int getNextAvailableSlot(ParkingType parkingType) {
    Connection con = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    int result = -1;
    try {
      con = dataBaseConfig.getConnection();
      ps = con.prepareStatement(DBConstants.GET_NEXT_PARKING_SPOT);
      ps.setString(1, parkingType.toString());
      rs = ps.executeQuery();
      if (rs.next()) {
        result = rs.getInt(1);
      }
    } catch (Exception ex) {
      outputWriterUtil.println("Error fetching next available slot");
      logger.error("Error fetching next available slot", ex);
    } finally {
      dataBaseConfig.closeResultSet(rs);
      dataBaseConfig.closePreparedStatement(ps);
      dataBaseConfig.closeConnection(con);
    }
    return result;
  }

  public boolean updateParking(ParkingSpot parkingSpot) {
    // update the availability for that parking slot
    Connection con = null;
    PreparedStatement ps = null;
    int updateRowCount = 0;
    try {
      con = dataBaseConfig.getConnection();
      ps = con.prepareStatement(DBConstants.UPDATE_PARKING_SPOT);
      ps.setBoolean(1, parkingSpot.isAvailable());
      ps.setInt(2, parkingSpot.getId());
      updateRowCount = ps.executeUpdate();
    } catch (Exception ex) {
      outputWriterUtil.println("Error updating parking info");
      logger.error("Error updating parking info", ex);
    } finally {
      dataBaseConfig.closePreparedStatement(ps);
      dataBaseConfig.closeConnection(con);
    }
    return (updateRowCount == 1);
  }

}
