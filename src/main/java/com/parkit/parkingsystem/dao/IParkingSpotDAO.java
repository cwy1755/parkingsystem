package com.parkit.parkingsystem.dao;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;

public interface IParkingSpotDAO {

  public int getNextAvailableSlot(ParkingType parkingType);
  public boolean updateParking(ParkingSpot parkingSpot);

}
