package com.parkit.parkingsystem.dao;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.config.IDataBaseConfig;
import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.util.OutputWriterlUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Calendar;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class TicketDAO implements ITicketDAO{

  private static final Logger logger = LogManager.getLogger("TicketDAO");
  private static OutputWriterlUtil outputWriterUtil = new OutputWriterlUtil(); 

  public IDataBaseConfig dataBaseConfig = new DataBaseConfig();

  public boolean saveTicket(Ticket ticket) {
    Connection con = null;
    PreparedStatement ps = null;
    boolean rcPsExecute = false;
    try {
      con = dataBaseConfig.getConnection();
      ps = con.prepareStatement(DBConstants.SAVE_TICKET);
      // ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME)
      // ps.setInt(1,ticket.getId());
      // ID is not compute here, it is auto-compute by mysql
      ps.setInt(1, ticket.getParkingSpot().getId());
      ps.setString(2, ticket.getVehicleRegNumber());
      ps.setDouble(3, ticket.getPrice());
      ps.setTimestamp(4, new Timestamp(ticket.getInTime().getTime()));
      ps.setTimestamp(5, (ticket.getOutTime() == null) ? null : (new Timestamp(ticket.getOutTime().getTime())));
      rcPsExecute = ps.execute();
    } catch (Exception ex) {
      outputWriterUtil.println("Error saving ticket");
      logger.error("Error saving ticket", ex);
    } finally {
      dataBaseConfig.closePreparedStatement(ps);
      dataBaseConfig.closeConnection(con);
    }
    return rcPsExecute;
  }

  public Ticket getTicket(String vehiculeRegNumber) {
    Connection con = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    Ticket ticket = null;
    try {
      con = dataBaseConfig.getConnection();
      ps = con.prepareStatement(DBConstants.GET_TICKET);
      // ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME)
      ps.setString(1, vehiculeRegNumber);
      rs = ps.executeQuery();
      if (rs.next()) {
        ticket = new Ticket();
        ParkingSpot parkingSpot = new ParkingSpot(rs.getInt(1), ParkingType.valueOf(rs.getString(6)), false);
        ticket.setParkingSpot(parkingSpot);
        ticket.setId(rs.getInt(2));
        ticket.setVehicleRegNumber(vehiculeRegNumber);
        ticket.setPrice(rs.getDouble(3));
        ticket.setInTime(rs.getTimestamp(4));
        ticket.setOutTime(rs.getTimestamp(5));
      }
    } catch (Exception ex) {
      outputWriterUtil.println("Error getting ticket");
      logger.error("Error getting ticket", ex);
    } finally {
      dataBaseConfig.closeResultSet(rs);
      dataBaseConfig.closePreparedStatement(ps);
      dataBaseConfig.closeConnection(con);
    }
    return ticket;
  }

  public boolean updateTicket(Ticket ticket) {
    Connection con = null;
    PreparedStatement ps = null;
    try {
      con = dataBaseConfig.getConnection();
      ps = con.prepareStatement(DBConstants.UPDATE_TICKET);
      ps.setDouble(1, ticket.getPrice());
      ps.setTimestamp(2, new Timestamp(ticket.getOutTime().getTime()));
      ps.setInt(3, ticket.getId());
      ps.execute();
      return true;
    } catch (Exception ex) {
      outputWriterUtil.println("Error updating ticket info");
      logger.error("Error updating ticket info", ex);
    } finally {
      dataBaseConfig.closePreparedStatement(ps);
      dataBaseConfig.closeConnection(con);
    }
    return false;
  }

  public boolean verifyRegularRegNumberOfOneMonthDuration(String vehiculeRegNumber) {
    Connection con = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    
    Calendar dateMinus1Month = Calendar.getInstance();
    dateMinus1Month.add(Calendar.MONTH, -1);
    
    long countRegNumber = 0;
    try {
      con = dataBaseConfig.getConnection();
      ps = con.prepareStatement(DBConstants.GET_COUNT_REGNUMBER_LAST_TIME);
      ps.setString(1, vehiculeRegNumber);
      ps.setTimestamp(2, new Timestamp(dateMinus1Month.getTimeInMillis()));
      rs = ps.executeQuery();
      if (rs.next()) {
        countRegNumber = rs.getBigDecimal(1).longValue();
      }
    } catch (Exception ex) {
      outputWriterUtil.println("Error getting RegNumberFromDate");
      logger.error("Error getting RegNumberFromDate", ex);
    } finally {
      dataBaseConfig.closeResultSet(rs);
      dataBaseConfig.closePreparedStatement(ps);
      dataBaseConfig.closeConnection(con);
    }
    return (countRegNumber >= 1);
  }


}
