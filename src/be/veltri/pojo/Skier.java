package be.veltri.pojo;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import be.veltri.dao.SkierDAO;

public class Skier extends Person {
	// ATTRIBUTES
    private List<Booking> bookings;
    private String phoneNumber;
    private String email;
    

    // CONSTRUCTORS
    public Skier() {
        super(); 
        if (bookings == null) {
            this.bookings = new ArrayList<>();
        }
    }

    
    public Skier(int id, String firstName, String lastName, LocalDate birthdate, String  phoneNumber, String email)       
    {
    	this(); 
        super.setId(id);
        super.setFirstName(firstName);
        super.setLastName(lastName);
        super.setBirthdate(birthdate);

        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    // PROPERTIES   
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
    public boolean create(SkierDAO skierDAO) {
        return skierDAO.createDAO(this); 
    }

    public static int getNextId(SkierDAO skierDAO) {
        return skierDAO.getNextIdDAO(); 
    }
    
    public boolean update(SkierDAO skierDAO) {
        return skierDAO.updateDAO(this);
    }
    
    public static Skier find(int id, SkierDAO skierDAO ) {
        return skierDAO.findDAO(id);
    }
    
    public boolean delete(SkierDAO skierDAO) {
        return skierDAO.deleteDAO(this);
    }

    public static List<Skier> findByLastName(String lastname, SkierDAO skierDAO) {
        try {
            return skierDAO.findByLastnameDAO(lastname);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<Skier> findAll(SkierDAO skierDAO) {
        return skierDAO.findAllDAO();
    }
    
    public void addBooking(Booking booking) {
    	if (this.bookings == null)
        	this.bookings = new ArrayList<>();
        if (booking != null && !bookings.contains(booking)) {
            bookings.add(booking);
        }
    }
    
    @Override
    public String toString() {
        return super.toString() + " " + calculateAge() + " years old";
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true; 
        if (obj == null || getClass() != obj.getClass()) return false; 

        Skier that = (Skier) obj;
        return getId() == that.getId(); 
    }
}
