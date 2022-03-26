package com.parkit.parkingsystem.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Date;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

  private static DataBaseConfig dataBaseConfig = new DataBaseConfig();
  private static ParkingSpotDAO parkingSpotDAO;
  private static TicketDAO ticketDAO;
  private static DataBasePrepareService dataBasePrepareService;

  @Mock
  private static InputReaderUtil inputReaderUtil;

  @BeforeAll
  private static void setUp() throws Exception {
    parkingSpotDAO = new ParkingSpotDAO();
    parkingSpotDAO.dataBaseConfig = dataBaseConfig;
    ticketDAO = new TicketDAO();
    ticketDAO.dataBaseConfig = dataBaseConfig;
    dataBasePrepareService = new DataBasePrepareService();
  }

  @BeforeEach
  private void setUpPerTest() throws Exception {
    dataBasePrepareService.clearDataBaseEntries();
  }

  @AfterAll
  private static void tearDown() {

  }

  @Test
  public void testParkingACar() {
    try {
      when(inputReaderUtil.readSelection()).thenReturn(1);
      when(inputReaderUtil.readVehiculeRegistrationNumber()).thenReturn("ABCDEF");
      ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

      parkingService.processIncomingVehicule();

      DataBaseConfig dataBaseConfig = new DataBaseConfig();
      Connection connection = dataBaseConfig.getConnection();
      ResultSet rs = connection
          .prepareStatement("select t.VEHICLE_REG_NUMBER from ticket t where t.VEHICLE_REG_NUMBER='ABCDEF'")
          .executeQuery();

      assertThat(rs.next()).isTrue();

      dataBaseConfig.closeResultSet(rs);
      dataBaseConfig.closeConnection(connection);

    } catch (Exception e) {
      fail("Exception not Expected");
      e.printStackTrace();
    }
  }

  @Test
  public void testParkingLotExit() {
    try {
      when(inputReaderUtil.readVehiculeRegistrationNumber()).thenReturn("ABCDEF");

      ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

      DataBaseConfig dataBaseConfig = new DataBaseConfig();
      Connection connection = dataBaseConfig.getConnection();

      PreparedStatement psTicket = connection.prepareStatement(DBConstants.SAVE_TICKET);
      // ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME)
      psTicket.setInt(1, 1);
      psTicket.setString(2, "ABCDEF");
      psTicket.setDouble(3, 0);
      psTicket.setTimestamp(4, new Timestamp(new Date(System.currentTimeMillis() - 3600 * 1000).getTime()));
      psTicket.setTimestamp(5, null);
      psTicket.execute();

      dataBaseConfig.closePreparedStatement(psTicket);

      PreparedStatement psParkingSpot = connection.prepareStatement(DBConstants.UPDATE_PARKING_SPOT);
      psParkingSpot.setBoolean(1, false);
      psParkingSpot.setInt(2, 1);
      psParkingSpot.executeUpdate();

      dataBaseConfig.closePreparedStatement(psParkingSpot);

      parkingService.processExitingVehicule();

      ResultSet rs = connection
          .prepareStatement("select t.PRICE, t.OUT_TIME from ticket t where t.VEHICLE_REG_NUMBER='ABCDEF'")
          .executeQuery();

      assertThat(rs.next()).isTrue();
      assertThat(rs.getDouble(1)).isGreaterThan(0);
      assertThat(rs.getTimestamp(2)).isNotNull();

      dataBaseConfig.closeResultSet(rs);
      dataBaseConfig.closeConnection(connection);

    } catch (Exception e) {
      fail("Exception not Expected");
      e.printStackTrace();
    }

  }

}
