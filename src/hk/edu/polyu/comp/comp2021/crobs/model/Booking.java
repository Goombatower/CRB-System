package hk.edu.polyu.comp.comp2021.crobs.model;
import java.time;


public final class Booking {
  final int BookingId;
  final User user;
  private Room room;
  private LocalDateTime bookTime;

  Booking(User us,Room r, LocalDateTime bt){
    this.user=us;
    this.Room=r;
    this.bookTime=bt;
    this.BookingId=IdGenerator.nextBookingID;
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
  
  @Override
  public boolean equals(Object o) {
    if (this == o) {
        return true;
    }
    if (!(o instanceof Booking)) {
        return false;
    }
    return this.bookingId == ((Booking) o).bookingId;
}
