package com.parkit.parkingsystem.dao;

import com.parkit.parkingsystem.model.Ticket;

public interface ITicketDAO {
  public boolean saveTicket(Ticket ticket);
  public Ticket getTicket(String vehiculeRegNumber);
  public boolean updateTicket(Ticket ticket);
  public boolean verifyRegularRegNumberOfOneMonthDuration(String vehiculeRegNumber);

}
