package com.parkit.parkingsystem.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

	private static final Logger logger = LogManager.getLogger(FareCalculatorService.class);
	
	/**
	 * calculate fare
	 * @param ticket
	 */
    public void calculateFare(Ticket ticket)
    {
    	logger.debug("Start calculateFare: " + ticket.getId());
    	
    	logger.trace("ticket: " + ticket.toString() );
    	logger.trace("ParkingSpot: " + ticket.getParkingSpot().toString() );
    	
        if( (ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime())) ){
        	logger.warn("Ticket: " + ticket.getId() + " : Inconsistent Date: inTime: " + ticket.getInTime().toString() + ", outTime: " + ticket.getOutTime().toString() );
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }

        float diff = ticket.getOutTime().getTime() - ticket.getInTime().getTime();
        float duration =  ( diff / (1000 *60) * 100 / 60 / 100);

        switch (ticket.getParkingSpot().getParkingType()){
            case CAR: {
                ticket.setPrice(duration * Fare.CAR_RATE_PER_HOUR);
                break;
            }
            case BIKE: {
                ticket.setPrice(duration * Fare.BIKE_RATE_PER_HOUR);
                break;
            }
            default: {
            	logger.warn("Ticket: " + ticket.getId() + " : Parking Type unknown: " + ticket.getParkingSpot().getParkingType());
            	throw new IllegalArgumentException("Unkown Parking Type");
            }
        }
        
        logger.debug("End calculateFare : return duration: " + duration);
    }
}
