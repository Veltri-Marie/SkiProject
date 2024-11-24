package be.veltri.pojo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
}
