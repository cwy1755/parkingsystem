package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.dao.ITicketDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.util.OutputWriterlUtil;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FareCalculatorService {

  private static final Logger logger = LogManager.getLogger(FareCalculatorService.class);
  private static OutputWriterlUtil ouputWriterUtil = new OutputWriterlUtil();

  private ITicketDAO ticketDAO;

  public FareCalculatorService(ITicketDAO ticketDAO) {
    this.ticketDAO = ticketDAO;
  }

  /**
   * calculate fare.
   * 
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

    long diff = ticket.getOutTime().getTime() - ticket.getInTime().getTime();
    float duration = ((float) diff / (1000 * 60) * 100 / 60 / 100);

    // Free first Fare.TIME_FREE
    if (duration > Fare.TIME_FREE) {
      duration -= Fare.TIME_FREE;
    } else {
      duration = 0;
    }

    if (duration > 0) {

      boolean regularRegNumber = ticketDAO.verifyRegularRegNumberOfOneMonthDuration(ticket.getVehicleRegNumber());

      float discount = 1;
      if (regularRegNumber) {
        discount = 1 - (float) Fare.RECURRENT_CUSTOMER_DISCOUNT_5;
      }

      switch (ticket.getParkingSpot().getParkingType()) {
        case CAR: {
          ticket.setPrice((double) Math.round(duration * Fare.CAR_RATE_PER_HOUR * discount * 100) / 100);
          break;
        }
        case BIKE: {
          ticket.setPrice((double) Math.round(duration * Fare.BIKE_RATE_PER_HOUR * discount * 100) / 100);
          break;
        }
        default: {
          ouputWriterUtil.println("Unkown Parking Type");
          logger.warn(
              "Ticket: " + ticket.getId() + " : Parking Type unknown: " + ticket.getParkingSpot().getParkingType());
          throw new IllegalArgumentException("Unkown Parking Type");
        }
      }
    }

    logger.debug("End calculateFare : return duration: " + duration);
  }

}
