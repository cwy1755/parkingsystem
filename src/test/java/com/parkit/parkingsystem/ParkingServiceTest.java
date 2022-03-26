package com.parkit.parkingsystem;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ParkingServiceTest {

  private static ParkingService parkingService;

  @Mock
  private static InputReaderUtil inputReaderUtil;
  @Mock
  private static ParkingSpotDAO parkingSpotDAO;
  @Mock
  private static TicketDAO ticketDAO;

  @BeforeEach
  private void setUpPerTest() {
    try {

      parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException("Failed to set up test mock objects");
    }
  }

  @Test
  public void processIncomingVehicule_vehiculeTypeUnexisting_Test() {
    try {
      when(inputReaderUtil.readSelection()).thenReturn(0);

      parkingService.getNextParkingNumberIfAvailable();
      fail("Expected exception was not thrown");
    } catch (Exception e) {
      // e.printStackTrace();
      assertTrue(true);
    }
  }

  @Test
  public void processIncomingVehicule_SlotUnAvailable_CAR_Test() {
    try {
      when(inputReaderUtil.readSelection()).thenReturn(1);
      when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(0);

      parkingService.getNextParkingNumberIfAvailable();
      fail("Expected exception was not thrown");
    } catch (Exception e) {
      // e.printStackTrace();
      assertTrue(true);
    }
  }

  @Test
  public void processIncomingVehicule_slotAvailable_CAR_Test() {
    try {
      when(inputReaderUtil.readSelection()).thenReturn(1);
      when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(1);
      when(inputReaderUtil.readVehiculeRegistrationNumber()).thenReturn("ABCDEF");

      parkingService.processIncomingVehicule();
      verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
    } catch (Exception e) {
      fail("Exception not Expected");
      e.printStackTrace();
    }
  }

  @Test
  public void processIncomingVehicule_slotAvailable_BIKE_Test() {
    try {
      when(inputReaderUtil.readSelection()).thenReturn(2);
      when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(1);
      when(inputReaderUtil.readVehiculeRegistrationNumber()).thenReturn("ABCDEF");

      parkingService.processIncomingVehicule();
      verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
    } catch (Exception e) {
      fail("Exception not Expected");
      e.printStackTrace();
    }
  }

  @Test
  public void processExitingVehiculeCAROKTest() {
    try {
      ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

      Ticket ticket = new Ticket();
      ticket.setInTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000)));
      ticket.setParkingSpot(parkingSpot);
      ticket.setVehicleRegNumber("ABCDEF");

      when(inputReaderUtil.readVehiculeRegistrationNumber()).thenReturn("ABCDEF");
      when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
      when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);
      when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);

      parkingService.processExitingVehicule();
      verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
    } catch (Exception e) {
      fail("Exception not Expected");
      e.printStackTrace();
    }
  }

  @Test
  public void processExitingVehiculeCARKOTest() {
    try {
      ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

      Ticket ticket = new Ticket();
      ticket.setInTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000)));
      ticket.setParkingSpot(parkingSpot);
      ticket.setVehicleRegNumber("ABCDEF");

      when(inputReaderUtil.readVehiculeRegistrationNumber()).thenReturn("ABCDEF");
      when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
      when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(false);

      parkingService.processExitingVehicule();
    } catch (Exception e) {
      fail("Exception not Expected");
      e.printStackTrace();
    }
  }

}
