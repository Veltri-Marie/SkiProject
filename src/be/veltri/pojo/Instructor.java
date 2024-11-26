package be.veltri.pojo;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.*;

import be.veltri.dao.InstructorDAO;

public class Instructor extends Person {
    // ATTRIBUTES
	private LocalDate hireDate;
    private List<Booking> bookings;       
    private List<Accreditation> accreditations;
    private List<Lesson> lessons;     
    

    // CONSTRUCTORS
    public Instructor() {
        super(); 
		if (bookings == null) {
			this.bookings = new ArrayList<>();
		}
		if (accreditations == null) {
			this.accreditations = new ArrayList<>();
		}
		if (lessons == null) {
			this.lessons = new ArrayList<>();
		}
    }    

    public Instructor(int id, String firstName, String lastName, LocalDate birthdate, LocalDate hireDate, Accreditation accreditation) {
        this(); 
        super.setId(id);
        super.setFirstName(firstName);
        super.setLastName(lastName);
        super.setBirthdate(birthdate);

        this.hireDate = hireDate;

        if (accreditation != null) {
            addAccreditation(accreditation); 
        }
    }
    
    // PROPERTIES
    public List<Booking> getBookings() {
        return new ArrayList<>(bookings); 
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }

    public List<Accreditation> getAccreditations() {
        return new ArrayList<>(accreditations); 
    }

    public void setAccreditations(List<Accreditation> accreditations) {
        this.accreditations = accreditations;
    }

    public List<Lesson> getLessons() {
        return new ArrayList<>(lessons); 
    }

    public void setLessons(List<Lesson> lessons) {
        this.lessons = lessons;
    }
    
    public LocalDate getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }

    // METHODS
    public boolean create(Connection conn) {
        InstructorDAO instructorDAO = new InstructorDAO(conn);
        return instructorDAO.createDAO(this); 
    }
    
    public static int getNextId(Connection conn) {
        InstructorDAO instructorDAO = new InstructorDAO(conn);
        return instructorDAO.getNextIdDAO(); 
    }
    
    public boolean update(Connection conn) {
        InstructorDAO instructorDAO = new InstructorDAO(conn);
        return instructorDAO.updateDAO(this);
    }
    
    public boolean delete(Connection conn) {
        InstructorDAO instructorDAO = new InstructorDAO(conn);
        return instructorDAO.deleteDAO(this);
    }
    
    public static Instructor find(int id, Connection conn) {
        InstructorDAO instructorDAO = new InstructorDAO(conn);
        return instructorDAO.findDAO(id);
    }

    public static List<Instructor> findByLastName(Connection conn, String lastname) {
        InstructorDAO instructorDAO = new InstructorDAO(conn);
        return instructorDAO.findByLastnameDAO(lastname);
    }

    public static List<Instructor> findAll(Connection conn) {
        InstructorDAO instructorDAO = new InstructorDAO(conn);
        return instructorDAO.findAllDAO();
    }
    
    public boolean isAvailable(LocalDate date) {
        if (date == null) throw new IllegalArgumentException("Lesson cannot be null.");
        for (Lesson existingLesson : lessons) {
            if (existingLesson.getLessonDate().equals(date)) {
                return false; 
            }
        }
        return true; 
    }
    
    public void addAccreditation(Connection conn, Accreditation accreditation) {
		
        if (accreditation != null && !accreditations.contains(accreditation)) {
            accreditations.add(accreditation);
            InstructorDAO instructorDAO = new InstructorDAO(conn);
            instructorDAO.addAccreditationDAO(this, accreditation);
        }
    }
    
    public boolean removeAccreditation(Connection conn, Accreditation accreditation) 
    {
    	if (accreditation != null && accreditations.contains(accreditation)) {
            accreditations.remove(accreditation);        
            InstructorDAO instructorDAO = new InstructorDAO(conn);
           
            return instructorDAO.removeAccreditationDAO(this, accreditation);
        } 
        return false;
    }

    
    public void addAccreditation(Accreditation accreditation) {
    	if (accreditations == null) {
			accreditations = new ArrayList<>();
		}
        if (accreditation != null && !accreditations.contains(accreditation)) {
            accreditations.add(accreditation);
        }
        else
        {
            throw new IllegalArgumentException("Accreditation cannot be null or already exists.");
        }
    }

    public void addBooking(Booking booking) {
		if (bookings == null) {
			bookings = new ArrayList<>();
		}
        if (booking != null && !bookings.contains(booking)) {
            bookings.add(booking);
        }
        else
        {
            throw new IllegalArgumentException("Booking cannot be null or already exists.");
        }
        
    }

    public void addLesson(Lesson lesson) {
    	if (lessons == null) {
	        this.lessons = new ArrayList<>();
		}
        if (lesson != null && !lessons.contains(lesson)) {
            lessons.add(lesson);
        }
        else
        {
            throw new IllegalArgumentException("Lesson cannot be null or already exists.");
        }
    }
    
    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true; 
        if (obj == null || getClass() != obj.getClass()) return false; 

        Instructor that = (Instructor) obj;
        return getId() == that.getId(); 
    }
    
}
