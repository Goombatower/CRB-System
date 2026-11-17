package hk.edu.polyu.comp.comp2021.crobs.model;
import java.time;


public final class Booking {
  final int BookingID;
  final User user;
  private Room room;
  private LocalDateTime bookTime;

  Booking(User us,Room r, LocalDateTime bt){
    this.user=us;
    this.Room=r;
    this.bookTime=bt;
    this.BookingID=IdGenerator.nextBookingID;
  }

  public User getUser(){
    return this.user;
  }
  public Room getRoom(){
    return this.Room
  }
  public LocalDateTime getBookingTime(){
    return this.bookTime;
  }
}
