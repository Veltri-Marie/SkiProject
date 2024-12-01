package be.veltri.pojo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import be.veltri.dao.PeriodDAO;

public class Period {
	 // ATTRIBUTES
    private int id_period;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean isVacation;
    private List<Booking> bookings;

    // CONSTRUCTOR
    public Period() {
    	if (bookings == null) {
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
    
 // METHODS
    public boolean create(PeriodDAO periodDAO) {
        return periodDAO.createDAO(this); 
    }
    
    public static int getNextId(PeriodDAO periodDAO) {
        return periodDAO.getNextIdDAO(); 
    }
    
    public boolean update(PeriodDAO periodDAO) {
        return periodDAO.updateDAO(this);
    }

    public boolean delete(PeriodDAO periodDAO) {
        return periodDAO.deleteDAO(this);
    }

    public static Period find(int id, PeriodDAO periodDAO) {
        return periodDAO.findDAO(id);
    }
    
	public static Period findByDate(LocalDate date, PeriodDAO periodDAO) {
		return periodDAO.findByDateDAO(date);
	}


    public static List<Period> findAll(PeriodDAO periodDAO) {
        return periodDAO.findAllDAO();
    }

    public int getCurrentBookingsCount() {
        return bookings.size();
    }
    
    public void addBooking(Booking booking) {
    	if (bookings == null) 
    	{
    		this.bookings = new ArrayList<>();
    	}
        if (booking != null) {
            bookings.add(booking);
        }
    }
    @Override
    public String toString() {
        return "id : " + id_period;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id_period, startDate, endDate, isVacation);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Period that = (Period) obj;
        return id_period == that.id_period;
    }    
}
