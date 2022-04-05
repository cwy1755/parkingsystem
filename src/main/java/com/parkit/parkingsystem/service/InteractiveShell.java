package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.dao.IParkingSpotDAO;
import com.parkit.parkingsystem.dao.ITicketDAO;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.util.IInputReaderUtil;
import com.parkit.parkingsystem.util.InputReaderUtil;
import com.parkit.parkingsystem.util.OutputWriterlUtil;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class InteractiveShell {

  private static final Logger logger = LogManager.getLogger("InteractiveShell");
  private static OutputWriterlUtil ouputWriterUtil = new OutputWriterlUtil();

  public static void loadInterface() {
    logger.info("App initialized!!!");
    ouputWriterUtil.println("Welcome to Parking System!");

    boolean continueApp = true;
    IInputReaderUtil inputReaderUtil = new InputReaderUtil();
    IParkingSpotDAO parkingSpotDAO = new ParkingSpotDAO();
    ITicketDAO ticketDAO = new TicketDAO();
    ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

    while (continueApp) {
      loadMenu();
      int option = inputReaderUtil.readSelection();
      switch (option) {
        case 1: {
          parkingService.processIncomingVehicule();
          break;
        }
        case 2: {
          parkingService.processExitingVehicule();
          break;
        }
        case 3: {
          ouputWriterUtil.println("Exiting from the system!");
          continueApp = false;
          break;
        }
        default:
          ouputWriterUtil.println("Unsupported option. Please enter a number corresponding to the provided menu");
      }
    }
  }

  private static void loadMenu() {
    ouputWriterUtil.println("Please select an option. Simply enter the number to choose an action");
    ouputWriterUtil.println("1 New Vehicle Entering - Allocate Parking Space");
    ouputWriterUtil.println("2 Vehicle Exiting - Generate Ticket Price");
    ouputWriterUtil.println("3 Shutdown System");
  }

}
