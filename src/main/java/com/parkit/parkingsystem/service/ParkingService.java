package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.util.InputReaderUtil;
import java.util.Date;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ParkingService {

  private static final Logger logger = LogManager.getLogger("ParkingService");

  private static FareCalculatorService fareCalculatorService = new FareCalculatorService();

  private InputReaderUtil inputReaderUtil;
  private ParkingSpotDAO parkingSpotDAO;
  private TicketDAO ticketDAO;

  public ParkingService(InputReaderUtil inputReaderUtil, ParkingSpotDAO parkingSpotDAO, TicketDAO ticketDAO) {
    this.inputReaderUtil = inputReaderUtil;
    this.parkingSpotDAO = parkingSpotDAO;
    this.ticketDAO = ticketDAO;
  }

  /**
   * Incoming vehicule.
   */
  public void processIncomingVehicule() {
    logger.debug("Start processIncomingVehicle");
    try {
      ParkingSpot parkingSpot = getNextParkingNumberIfAvailable();

      if (parkingSpot != null && parkingSpot.getId() > 0) {
        String vehiculeRegNumber = getVehiculeRegNumber();
        parkingSpot.setAvailable(false);
        parkingSpotDAO.updateParking(parkingSpot); // allow this parking space and mark it's availability as false

        Date inTime = new Date();
        Ticket ticket = new Ticket();

        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber(vehiculeRegNumber);
        ticket.setPrice(0);
        ticket.setInTime(inTime);
        ticket.setOutTime(null);
        ticketDAO.saveTicket(ticket);

        System.out.println("Generated Ticket and saved in DB");
        System.out.println("Please park your vehicle in spot number:" + parkingSpot.getId());
        System.out.println("Recorded in-time for vehicle number:" + vehiculeRegNumber + " is:" + inTime);
      }
    } catch (Exception e) {
      System.out.println("Unable to process incoming vehicle");
      logger.error("Unable to process incoming vehicle", e);
    }
    logger.debug("End processIncomingVehicle");
  }

  /**
   * Get the the vehicule register number.
   * 

   * @return vehicule register number
   * 
   * @throws Exception not treat
   */
  private String getVehiculeRegNumber() throws Exception {
    System.out.println("Please type the vehicle registration number and press enter key");
    String vehiculeRegNumber = inputReaderUtil.readVehiculeRegistrationNumber();
    logger.debug("vehiculeRegNumber: " + vehiculeRegNumber);
    return vehiculeRegNumber;
  }

  /**
   * Find an available spot for a vehicule type.
   * 

   * @return free parking spot
   */
  public ParkingSpot getNextParkingNumberIfAvailable() {
    int parkingNumber = 0;
    ParkingSpot parkingSpot = null;
    try {
      ParkingType parkingType = getVehiculeType();
      parkingNumber = parkingSpotDAO.getNextAvailableSlot(parkingType);
      if (parkingNumber > 0) {
        parkingSpot = new ParkingSpot(parkingNumber, parkingType, true);
      } else {
        throw new Exception("Error fetching parking number from DB. Parking slots might be full");
      }
    } catch (IllegalArgumentException ie) {
      System.out.println("Error parsing user input for type of vehicule");
      logger.error("Error parsing user input for type of vehicule", ie);
    } catch (Exception e) {
      System.out.println("Error fetching next available parking slot");
      logger.error("Error fetching next available parking slot", e);
    }
    logger.debug("parkingSpot: " + parkingSpot.getId());
    return parkingSpot;
  }

  /**
   * get the vehicule type.
   * 

   * @return vehicule type
   */
  private ParkingType getVehiculeType() {
    System.out.println("Please select vehicle type from menu");
    System.out.println("1 CAR");
    System.out.println("2 BIKE");
    int input = inputReaderUtil.readSelection();
    logger.debug("input vehicule type: " + input);
    switch (input) {
      case 1: {
        return ParkingType.CAR;
      }
      case 2: {
        return ParkingType.BIKE;
      }
      default: {
        System.out.println("Incorrect input provided");
        throw new IllegalArgumentException("Entered input is invalid");
      }
    }
  }

  /**
   * Exiting Vehicule.
   */
  public void processExitingVehicule() {
    logger.debug("Start processExitingVehicle");
    try {
      String vehiculeRegNumber = getVehiculeRegNumber();
      Ticket ticket = ticketDAO.getTicket(vehiculeRegNumber);
      logger.debug("ticket id: " + ticket.getId());
      Date outTime = new Date();
      ticket.setOutTime(outTime);
      fareCalculatorService.calculateFare(ticket);
      if (ticketDAO.updateTicket(ticket)) {
        ParkingSpot parkingSpot = ticket.getParkingSpot();
        parkingSpot.setAvailable(true);
        parkingSpotDAO.updateParking(parkingSpot);
        System.out.println("Please pay the parking fare:" + ticket.getPrice());
        System.out.println("Recorded out-time for vehicle number:" + ticket.getVehicleRegNumber() + " is:" + outTime);
      } else {
        System.out.println("Unable to update ticket information. Error occurred");
      }
    } catch (Exception e) {
      System.out.println("Unable to process exiting vehicle");
      logger.error("Unable to process exiting vehicle", e);
    }
    logger.debug("End processExitingVehicle");
  }
}
