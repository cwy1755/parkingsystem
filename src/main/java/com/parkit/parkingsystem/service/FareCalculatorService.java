package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FareCalculatorService {

  private static final Logger logger = LogManager.getLogger(FareCalculatorService.class);

  /**
   * calculate fare.
   * 

   * @param ticket input ticket
   */
  public void calculateFare(Ticket ticket) {
    logger.info("Start calculateFare: " + ticket.getId());

    logger.debug("ticket: " + ticket.toString());
    logger.debug("ParkingSpot: " + ticket.getParkingSpot().toString());

    if ((ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime()))) {
      logger.warn("Ticket: " + ticket.getId() + " : Inconsistent Date: inTime: " + ticket.getInTime().toString()
          + ", outTime: " + ticket.getOutTime().toString());
      throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
    }

    float diff = ticket.getOutTime().getTime() - ticket.getInTime().getTime();
    float duration = (diff / (1000 * 60) * 100 / 60 / 100);

    switch (ticket.getParkingSpot().getParkingType()) {
      case CAR: {
        ticket.setPrice((double) Math.round(duration * Fare.CAR_RATE_PER_HOUR * 100) / 100);
        break;
      }
      case BIKE: {
        ticket.setPrice((double) Math.round(duration * Fare.BIKE_RATE_PER_HOUR * 100) / 100);
        break;
      }
      default: {
        System.out.println("Unkown Parking Type");
        logger
            .warn("Ticket: " + ticket.getId() + " : Parking Type unknown: " + ticket.getParkingSpot().getParkingType());
        throw new IllegalArgumentException("Unkown Parking Type");
      }
    }

    logger.debug("End calculateFare : return duration: " + duration);
  }
}
