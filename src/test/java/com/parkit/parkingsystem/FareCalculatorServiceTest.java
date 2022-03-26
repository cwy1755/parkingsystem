package com.parkit.parkingsystem;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import java.util.Date;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class FareCalculatorServiceTest {

  private static FareCalculatorService fareCalculatorService;
  private Ticket ticket;

  @BeforeAll
  private static void setUp() {
    fareCalculatorService = new FareCalculatorService();
  }

  @BeforeEach
  private void setUpPerTest() {
    ticket = new Ticket();
  }

  @Test
  public void calculateFareCar() {
    Date nowTime = new Date();

    Date inTime = new Date(nowTime.getTime() - (60 * 60 * 1000));
    Date outTime = new Date(nowTime.getTime());
    ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

    ticket.setInTime(inTime);
    ticket.setOutTime(outTime);
    ticket.setParkingSpot(parkingSpot);
    fareCalculatorService.calculateFare(ticket);

    assertThat(ticket.getPrice()).isEqualTo(Fare.CAR_RATE_PER_HOUR);
  }

  @Test
  public void calculateFareBike() {
    Date nowTime = new Date();

    Date inTime = new Date(nowTime.getTime() - (60 * 60 * 1000));
    Date outTime = new Date(nowTime.getTime());
    ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

    ticket.setInTime(inTime);
    ticket.setOutTime(outTime);
    ticket.setParkingSpot(parkingSpot);
    fareCalculatorService.calculateFare(ticket);

    assertThat(ticket.getPrice()).isEqualTo(Fare.BIKE_RATE_PER_HOUR);
  }

  @Test
  public void calculateFareNullType() {
    Date nowTime = new Date();

    Date inTime = new Date(nowTime.getTime() - (60 * 60 * 1000));
    Date outTime = new Date(nowTime.getTime());
    ParkingSpot parkingSpot = new ParkingSpot(1, null, false);

    ticket.setInTime(inTime);
    ticket.setOutTime(outTime);
    ticket.setParkingSpot(parkingSpot);

    assertThrows(NullPointerException.class, () -> fareCalculatorService.calculateFare(ticket));

  }

  @Test
  public void calculateFareUnkownType() {
    Date nowTime = new Date();

    Date inTime = new Date(nowTime.getTime() - (60 * 60 * 1000));
    Date outTime = new Date(nowTime.getTime());
    ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.TRUC, false);

    ticket.setInTime(inTime);
    ticket.setOutTime(outTime);
    ticket.setParkingSpot(parkingSpot);

    assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
      fareCalculatorService.calculateFare(ticket);
    }).withMessage("%s", "Unkown Parking Type");
  }

  @Test
  public void calculateFareBikeWithFutureInTime() {
    Date nowTime = new Date();

    Date inTime = new Date(nowTime.getTime() + (60 * 60 * 1000));
    Date outTime = new Date(nowTime.getTime());
    ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

    ticket.setInTime(inTime);
    ticket.setOutTime(outTime);
    ticket.setParkingSpot(parkingSpot);

    assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket));
  }

  @Test
  public void calculateFareBikeWithLessThanOneHourParkingTime() {
    Date nowTime = new Date();

    // 45 minutes parking time should give 3/4th
    // parking fare
    Date inTime = new Date(nowTime.getTime() - (45 * 60 * 1000));
    Date outTime = new Date(nowTime.getTime());
    ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

    ticket.setInTime(inTime);
    ticket.setOutTime(outTime);
    ticket.setParkingSpot(parkingSpot);
    fareCalculatorService.calculateFare(ticket);

    assertThat(ticket.getPrice()).isEqualTo((double) Math.round(0.75 * Fare.BIKE_RATE_PER_HOUR * 100) / 100);
  }

  @Test
  public void calculateFareCarWithLessThanOneHourParkingTime() {
    Date nowTime = new Date();

    // 45 minutes parking time should give 3/4th
    // parking fare
    Date inTime = new Date(nowTime.getTime() - (45 * 60 * 1000));

    Date outTime = new Date(nowTime.getTime());
    ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

    ticket.setInTime(inTime);
    ticket.setOutTime(outTime);
    ticket.setParkingSpot(parkingSpot);
    fareCalculatorService.calculateFare(ticket);

    assertThat(ticket.getPrice()).isEqualTo((double) Math.round(0.75 * Fare.CAR_RATE_PER_HOUR * 100) / 100);
  }

  @Test
  public void calculateFareCarWithMoreThanADayParkingTime() {
    Date nowTime = new Date();

    // 24 hours parking time should give 24 *
    // parking fare per hour
    Date inTime = new Date(nowTime.getTime() - (24 * 60 * 60 * 1000));

    Date outTime = new Date(nowTime.getTime());
    ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

    ticket.setInTime(inTime);
    ticket.setOutTime(outTime);
    ticket.setParkingSpot(parkingSpot);
    fareCalculatorService.calculateFare(ticket);

    assertEquals((24 * Fare.CAR_RATE_PER_HOUR), ticket.getPrice());
    assertThat(ticket.getPrice()).isEqualTo((double) Math.round(24 * Fare.CAR_RATE_PER_HOUR * 100) / 100);
  }

}
