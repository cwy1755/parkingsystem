package com.parkit.parkingsystem;

import static org.assertj.core.api.Assertions.assertThat;
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
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
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
  @DisplayName("L'utilisateur ne saisi pas le bon type de vehicule")
  public void processIncomingVehicule_vehiculeTypeUnexisting() {
    try {
      when(inputReaderUtil.readSelection()).thenReturn(0);

      assertThat(parkingService.getNextParkingNumberIfAvailable()).isNull();

    } catch (Exception e) {
      e.printStackTrace();
      fail("Expected exception was not thrown");
    }
  }

  @Test
  @DisplayName("Une voiture entre, il n'y a plus de place pour les voitures")
  public void processIncomingVehicule_CAR_SlotUnAvailable() {
    try {
      when(inputReaderUtil.readSelection()).thenReturn(1);
      when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(0);

      assertThat(parkingService.getNextParkingNumberIfAvailable()).isNull();

    } catch (Exception e) {
      e.printStackTrace();
      fail("Expected exception was not thrown");
    }
  }

  @Test
  @DisplayName("Une voiture entre, il y a une place, saisi immat, place parking maj, ticket créé")
  public void processIncomingVehicule_CAR_slotAvailable() {
    try {
      when(inputReaderUtil.readSelection()).thenReturn(1);
      when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(1);
      when(inputReaderUtil.readVehiculeRegistrationNumber()).thenReturn("ABCDEF");
      when(ticketDAO.verifyExistingRegNumber("ABCDEF")).thenReturn(false);

      parkingService.processIncomingVehicule();
      
      verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
      verify(ticketDAO, Mockito.times(1)).saveTicket(any(Ticket.class));
    } catch (Exception e) {
      e.printStackTrace();
      fail("Exception not Expected");
    }
  }

  @Test
  @DisplayName("Une moto entre, il y a une place, saisi immat, place parking maj, ticket créé")
  public void processIncomingVehicule_BIKE_slotAvailable() {
    try {
      when(inputReaderUtil.readSelection()).thenReturn(2);
      when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(1);
      when(inputReaderUtil.readVehiculeRegistrationNumber()).thenReturn("ABCDEF");
      when(ticketDAO.verifyExistingRegNumber("ABCDEF")).thenReturn(false);

      parkingService.processIncomingVehicule();
      
      verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
      verify(ticketDAO, Mockito.times(1)).saveTicket(any(Ticket.class));
    } catch (Exception e) {
      e.printStackTrace();
      fail("Exception not Expected");
    }
  }

  @Test
  @DisplayName("Une voiture entre, il y a une place, saisi immat, mais le reg number existe déjà")
  public void processIncomingVehicule_CAR_Ano_existingRegNumber() {
    try {
      when(inputReaderUtil.readSelection()).thenReturn(1);
      when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(1);
      when(inputReaderUtil.readVehiculeRegistrationNumber()).thenReturn("ABCDEF");
      when(ticketDAO.verifyExistingRegNumber("ABCDEF")).thenReturn(true);

      parkingService.processIncomingVehicule();
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Exception Expected");
    }
  }


  
  @Test
  @DisplayName("Une voiture sort et tout est OK")
  public void processExitingVehicule_CAR_OK() {
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
      e.printStackTrace();
      fail("Exception not Expected");
    }
  }

  @Test
  @DisplayName("Une voiture sort, mais pb de maj du ticket")
  public void processExitingVehicule_CAR_AnoUpdateTicket() {
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
      e.printStackTrace();
      fail("Exception not Expected");
    }
  }

}
