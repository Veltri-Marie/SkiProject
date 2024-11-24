package be.veltri.pojo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
   
}
