package be.veltri.pojo;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import be.veltri.dao.BookingDAO;

public class Booking {
	// ATTRIBUTES
	private int id_Booking;
	private LocalDate reservation_date;
	private boolean insuranceOpt; 
    private Skier skier;            
    private Instructor instructor;   
    private Lesson lesson;         
    private Period period;   
    private List<LessonSession> lessonSessions;
    
    // CONSTRUCTORS
    public Booking() {
		if (lessonSessions == null) 
			lessonSessions = new ArrayList<>();    	
    }
    
    public Booking(int id_Booking, LocalDate reservation_date, Lesson lesson, Instructor instructor, Period period, Skier skier, boolean insuranceOpt) {
    	this();
    	this.id_Booking = id_Booking;
    	this.reservation_date = reservation_date;
    	this.lesson = lesson; 
        this.instructor = instructor;
        this.period = period;    
        this.skier = skier;    
        this.insuranceOpt = insuranceOpt;
    }

    // PROPERTIES
    public int getId() {
        return id_Booking; 
    }

    public void setId(int id) {
        this.id_Booking = id; 
    }
    
	public LocalDate getReservationDate() {
		return reservation_date;
	}
	
	public void setReservationDate(LocalDate reservation_date) {
		this.reservation_date = reservation_date;
	}

    
    public Skier getSkier() {
        return skier; 
    }

    public void setSkier(Skier skier) {
        this.skier = skier; 
    }

    public Instructor getInstructor() {
        return instructor; 
    }

    public void setInstructor(Instructor instructor) {
        this.instructor = instructor; 
    }

    public Lesson getLesson() {
        return lesson; 
    }

    public void setLesson(Lesson lesson) {
        this.lesson = lesson; 
    }

    public Period getPeriod() {
        return period; 
    }

    public void setPeriod(Period period) {
        this.period = period; 
    }

    public boolean getInsuranceOpt() {
        return insuranceOpt; 
    }

    public void setInsuranceOpt(boolean insuranceOpt) {
        this.insuranceOpt = insuranceOpt; 
    }
    
    public List<LessonSession> getLessonSessions() {
    	        return lessonSessions;
    }
    
	public void setLessonSessions(List<LessonSession> lessonSessions) {
		this.lessonSessions = lessonSessions;
	}
    
    // METHODS
    public static int getNextId(Connection conn) {
        BookingDAO bookingDAO = new BookingDAO(conn);
        return bookingDAO.getNextIdDAO(); 
    }
    
    public boolean create(Connection conn) {
        BookingDAO bookingDAO = new BookingDAO(conn);
        return bookingDAO.createDAO(this); 
    }
    
    public boolean update(Connection conn) {
    	BookingDAO bookingDAO = new BookingDAO(conn);
        return bookingDAO.updateDAO(this);
    }

    public boolean delete(Connection conn) {
    	BookingDAO bookingDAO = new BookingDAO(conn);
        return bookingDAO.deleteDAO(this);
    }

    public static Booking find(int id, Connection conn) {
    	BookingDAO bookingDAO = new BookingDAO(conn);
        return bookingDAO.findDAO(id);
    }

    public static List<Booking> findAll(Connection conn) {
    	BookingDAO bookingDAO = new BookingDAO(conn);
        return bookingDAO.findAllDAO();
    }
    
    public double calculatePrice() {
        double price = 0.0;
        int countMorning = 0;
        int countAfternoon = 0;

        if (lesson != null) {
        	
    		if(lesson.getNb_hours() == 1 )
    			price += 60;
    		else if (lesson.getNb_hours() == 2)
				price += 90;
    		else
    			price += lesson.getLessonPrice();
        }
        

		for (LessonSession lessonSession : lessonSessions) {
			if (lessonSession.getSessionType().toLowerCase().equals("morning"))
				countMorning++;
			if (lessonSession.getSessionType().toLowerCase().equals("afternoon"))
				countAfternoon++;
		}

		if (countMorning > 0 && countAfternoon > 0) {
            price *= 0.85; 
        }

        if (insuranceOpt) {
            price += 20.0;
        }
        
        return price;
    }
    
    public void addLessonSession(LessonSession lessonSession) {
		if (lessonSessions == null) {
            lessonSessions = new ArrayList<>();
        }
		if (lessonSession != null && !lessonSessions.contains(lessonSession)) {
			lessonSessions.add(lessonSession);
		} else {
			throw new IllegalArgumentException("Lesson session cannot be null.");
		}
	}
    
    @Override
    public String toString() {
        return "Booking{" +
                "id_Booking=" + id_Booking ;
    }

    // hashCode
    @Override
    public int hashCode() {
        return Objects.hash(id_Booking);
    }

    // equals
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true; 
        if (obj == null || getClass() != obj.getClass()) return false; 
        Booking booking = (Booking) obj; 
        return id_Booking == booking.id_Booking; 
    }
   
}
