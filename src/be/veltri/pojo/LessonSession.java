package be.veltri.pojo;

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
}
