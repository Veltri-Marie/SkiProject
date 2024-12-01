package be.veltri.pojo;

import java.sql.Connection;
import java.util.List;
import java.util.Objects;

import be.veltri.dao.LessonSessionDAO;

public class LessonSession {
	// ATTRIBUTES
	private int id_session;
    private String sessionType;
    private Booking booking;
    
    // CONSTRUCTORS
    public LessonSession() {
    }
    
    public LessonSession(int id_Session, String sessionType, Booking booking) {
    	this();
        this.id_session = id_Session;
        this.sessionType = sessionType;
        this.booking = booking;
        booking.addLessonSession(this);
    }
    
    // PROPERTIES
    public int getId() {
        return id_session;
    }

    public void setId(int id_Session) {
        this.id_session = id_Session;
    }

    public String getSessionType() {
        return sessionType;
    }

    public void setSessionType(String sessionType) {
        this.sessionType = sessionType;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }
    
	 //METHODS
    public boolean create(Connection conn) {
    	LessonSessionDAO  sessionDAO= new LessonSessionDAO(conn);
        return sessionDAO.createDAO(this); 
    }
    
    public static int getNextId(Connection conn) {
    	LessonSessionDAO sessionDAO = new LessonSessionDAO(conn);
        return sessionDAO.getNextIdDAO(); 
    }

    public boolean update(Connection conn) {
    	LessonSessionDAO sessionDAO = new LessonSessionDAO(conn);
        return sessionDAO.updateDAO(this);
    }

    public boolean delete(Connection conn) {
    	LessonSessionDAO sessionDAO = new LessonSessionDAO(conn);
        return sessionDAO.deleteDAO(this);
    }

    public static LessonSession find(int id, Connection conn) {
    	LessonSessionDAO sessionDAO = new LessonSessionDAO(conn);
        return sessionDAO.findDAO(id);
    }
    
	public static List<LessonSession> findAll(Connection conn) {
		LessonSessionDAO sessionDAO = new LessonSessionDAO(conn);
		return sessionDAO.findAllDAO();
	}

	
    @Override
    public String toString() {
        return id_session + " - " + sessionType;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id_session);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true; 
        if (obj == null || getClass() != obj.getClass()) return false; 

        LessonSession that = (LessonSession) obj;
        return getId() == that.getId(); 
    }
}
