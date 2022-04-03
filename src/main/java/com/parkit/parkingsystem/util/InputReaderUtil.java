package com.parkit.parkingsystem.util;

import java.util.Scanner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class InputReaderUtil implements IInputReaderUtil{

  private static Scanner scan = new Scanner(System.in);
  private static final Logger logger = LogManager.getLogger("InputReaderUtil");
  private static OutputWriterlUtil outputWriterUtil = new OutputWriterlUtil();

  public int readSelection() {
    int input = -1;
    try {
      input = Integer.parseInt(scan.nextLine());
    } catch (Exception e) {
      logger.error("Error while reading user input from Shell", e);
      outputWriterUtil.println("Error reading input. Please enter valid number for proceeding further");
    }
    return input;
  }

  public String readVehiculeRegistrationNumber() throws Exception {
    try {
      String vehicleRegNumber = scan.nextLine();
      if (vehicleRegNumber == null || vehicleRegNumber.trim().length() == 0) {
        throw new IllegalArgumentException("Invalid input provided");
      }
      return vehicleRegNumber;
    } catch (Exception e) {
      logger.error("Error while reading user input from Shell", e);
      outputWriterUtil.println("Error reading input. Please enter a valid string for vehicle registration number");
      throw e;
    }
  }

}
