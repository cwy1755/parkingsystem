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
import org.junit.jupiter.api.DisplayName;
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
  @DisplayName("Validation que le prix/heure pour une voiture est celle paramétré ")
  public void calculateFareCar() {
    Date nowTime = new Date();

    Date inTime = new Date(nowTime.getTime() - (60 * 60 * 1000));
    Date outTime = new Date(nowTime.getTime());
    ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

    ticket.setInTime(inTime);
    ticket.setOutTime(outTime);
    ticket.setParkingSpot(parkingSpot);

    fareCalculatorService.calculateFare(ticket);

    assertThat(ticket.getPrice()).isEqualTo(Fare.CAR_RATE_PER_HOUR - Fare.CAR_RATE_PER_HOUR * Fare.TIME_FREE);
  }

  @Test
  @DisplayName("Validation que le prix/heure pour une moto est celle paramétré ")
  public void calculateFareBike() {
    Date nowTime = new Date();

    Date inTime = new Date(nowTime.getTime() - (60 * 60 * 1000));
    Date outTime = new Date(nowTime.getTime());
    ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

    ticket.setInTime(inTime);
    ticket.setOutTime(outTime);
    ticket.setParkingSpot(parkingSpot);

    fareCalculatorService.calculateFare(ticket);

    assertThat(ticket.getPrice()).isEqualTo(Fare.BIKE_RATE_PER_HOUR - Fare.BIKE_RATE_PER_HOUR * Fare.TIME_FREE);
  }

  @Test
  @DisplayName("Validation qu'il n'y a pas calcul s'il y a un type de parking à null")
  public void calculateFareTypeIsNull() {
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
  @DisplayName("Validation que le type de vehicule n'est pas géré")
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
  @DisplayName("Validation qu'il y a une erreur quand la date/heure d'entrée en postérieur à la date/heure de sortie")
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
  @DisplayName("Calcule du prix pour 45 minutes pour une moto")
  public void calculateFareBikeWithLessThanOneHourParkingTime() {
    Date nowTime = new Date();

    Date inTime = new Date(nowTime.getTime() - (45 * 60 * 1000));
    Date outTime = new Date(nowTime.getTime());
    ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

    ticket.setInTime(inTime);
    ticket.setOutTime(outTime);
    ticket.setParkingSpot(parkingSpot);
    fareCalculatorService.calculateFare(ticket);

    assertThat(ticket.getPrice()).isEqualTo((double) Math.round(0.25 * Fare.BIKE_RATE_PER_HOUR * 100) / 100);
  }

  @Test
  @DisplayName("Calcule du prix pour 45 minutes pour une voiture")
  public void calculateFareCarWithLessThanOneHourParkingTime() {
    Date nowTime = new Date();

    Date inTime = new Date(nowTime.getTime() - (45 * 60 * 1000));
    Date outTime = new Date(nowTime.getTime());
    ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

    ticket.setInTime(inTime);
    ticket.setOutTime(outTime);
    ticket.setParkingSpot(parkingSpot);
    fareCalculatorService.calculateFare(ticket);

    assertThat(ticket.getPrice()).isEqualTo((double) Math.round(0.25 * Fare.CAR_RATE_PER_HOUR * 100) / 100);
  }

  @Test
  @DisplayName("Calcule du prix pour 24h pour une voiture")
  public void calculateFareCarWithADayParkingTime() {
    Date nowTime = new Date();

    Date inTime = new Date(nowTime.getTime() - (24 * 60 * 60 * 1000));
    Date outTime = new Date(nowTime.getTime());
    ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

    ticket.setInTime(inTime);
    ticket.setOutTime(outTime);
    ticket.setParkingSpot(parkingSpot);
    fareCalculatorService.calculateFare(ticket);

    assertThat(ticket.getPrice()).isEqualTo((double) Math.round(23.5 * Fare.CAR_RATE_PER_HOUR * 100) / 100);
  }

  @Test
  @DisplayName("Calcule du prix pour 23h pour une voiture")
  public void calculateFareCarWith23hoursParkingTime() {
    Date nowTime = new Date();

    Date inTime = new Date(nowTime.getTime() - (23 * 60 * 60 * 1000));
    Date outTime = new Date(nowTime.getTime());
    ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

    ticket.setInTime(inTime);
    ticket.setOutTime(outTime);
    ticket.setParkingSpot(parkingSpot);

    fareCalculatorService.calculateFare(ticket);

    assertThat(ticket.getPrice()).isEqualTo((double) Math.round(22.5 * Fare.CAR_RATE_PER_HOUR * 100) / 100);
  }

  @Test
  @DisplayName("Calcule du prix pour 15 minutes pour une moto")
  public void calculateFareCarWith15minParkingTime() {
    Date nowTime = new Date();

    Date inTime = new Date(nowTime.getTime() - (15 * 60 * 1000));
    Date outTime = new Date(nowTime.getTime());
    ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

    ticket.setInTime(inTime);
    ticket.setOutTime(outTime);
    ticket.setParkingSpot(parkingSpot);

    fareCalculatorService.calculateFare(ticket);

    assertThat(ticket.getPrice()).isEqualTo(0);
  }

  @Test
  @DisplayName("Calcule du prix pour 30 minutes pour une moto")
  public void calculateFareCarWith30minParkingTime() {
    Date nowTime = new Date();

    Date inTime = new Date(nowTime.getTime() - (30 * 60 * 1000));
    Date outTime = new Date(nowTime.getTime());
    ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

    ticket.setInTime(inTime);
    ticket.setOutTime(outTime);
    ticket.setParkingSpot(parkingSpot);

    fareCalculatorService.calculateFare(ticket);

    assertThat(ticket.getPrice()).isEqualTo(0);
  }

}
