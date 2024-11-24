package be.veltri.pojo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Period {
	 // ATTRIBUTES
    private int id_period;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean isVacation;
    private List<Booking> bookings;

    // CONSTRUCTOR
    public Period() {
    	if (bookings == null) 
    	{
    		this.bookings = new ArrayList<>();
    	}
    	
    }

    public Period(int id_period, LocalDate startDate, LocalDate endDate, boolean isVacation) {
    	this();
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Start date and end date cannot be null.");
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date.");
        }

        this.id_period = id_period;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isVacation = isVacation;

    }

    // PROPERTIES
    public int getId() {
        return id_period;
    }

    public void setId(int id) {
        this.id_period = id;
    }

    public LocalDate getStartDate() {
        return startDate;  
    }

    public void setStartDate(LocalDate startDate) {
        if (startDate == null) {
            throw new IllegalArgumentException("Start date cannot be null.");
        }
        if (startDate.isAfter(this.endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date.");
        }
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        if (endDate == null) {
            throw new IllegalArgumentException("End date cannot be null.");
        }
        this.endDate = endDate;
    }

    public boolean getIsVacation() {
        return isVacation;
    }

    public void setIsVacation(boolean isVacation) {
        this.isVacation = isVacation;
    }

    public List<Booking> getBookings() {
        return new ArrayList<>(bookings);  
    }

    public void setBookings(List<Booking> bookings) {
        if (bookings != null) {
            this.bookings = new ArrayList<>(bookings);
        } else {
            throw new IllegalArgumentException("Bookings cannot be null.");
        }
    }
}
