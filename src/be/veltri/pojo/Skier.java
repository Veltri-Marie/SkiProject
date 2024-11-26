package be.veltri.pojo;

import java.sql.Connection;
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
    public boolean create(Connection conn) {
        SkierDAO skierDAO = new SkierDAO(conn);
        return skierDAO.createDAO(this); 
    }

    public static int getNextId(Connection conn) {
        SkierDAO skierDAO = new SkierDAO(conn);
        return skierDAO.getNextIdDAO(); 
    }
    
    public boolean update(Connection conn) {
        SkierDAO skierDAO = new SkierDAO(conn);
        return skierDAO.updateDAO(this);
    }
    
    public static Skier find(int id, Connection conn) {
        SkierDAO skierDAO = new SkierDAO(conn);
        return skierDAO.findDAO(id);
    }
    
    public boolean delete(Connection conn) {
        SkierDAO skierDAO = new SkierDAO(conn);
        return skierDAO.deleteDAO(this);
    }

    public static List<Skier> findByLastName(String lastname, Connection conn) {
        SkierDAO skierDAO = new SkierDAO(conn);
        try {
            return skierDAO.findByLastnameDAO(lastname);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<Skier> findAll(Connection conn) {
        SkierDAO skierDAO = new SkierDAO(conn);
        return skierDAO.findAllDAO();
    }
    
    public void addBooking(Booking booking) {
    	if (this.bookings == null)
        	this.bookings = new ArrayList<>();
        if (booking != null && !bookings.contains(booking)) {
            bookings.add(booking);
        }
        else {
            throw new IllegalArgumentException("Booking cannot be null.");
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
