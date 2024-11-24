package be.veltri.pojo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Lesson {
	 // ATTRIBUTES
    private int id_Lesson;
    private LocalDate lessonDate;
    private int minBookings;
    private int maxBookings;
    private int nb_hours;
    private boolean isCollective;
    private LessonType lessonType;
    private Instructor instructor;
    private List<Booking> bookings = new ArrayList<>();

    // CONSTRUCTORS
    public Lesson() {
      }

    public Lesson(int id_Lesson, LocalDate lessonDate, int minBookings, int maxBookings, int nb_hours, boolean isCollective, Instructor instructor, LessonType lessonType) {
        this();
        if (minBookings < 0 || maxBookings < 0) {
            throw new IllegalArgumentException("Minimum and maximum bookings must be non-negative.");
        }
        if (minBookings > maxBookings) {
            throw new IllegalArgumentException("Minimum bookings cannot exceed maximum bookings.");
        }
        this.id_Lesson = id_Lesson;
        this.lessonDate = lessonDate;
        this.minBookings = minBookings;
        this.maxBookings = maxBookings;
        this.nb_hours = nb_hours;
        this.isCollective = isCollective;
        this.instructor = instructor;
        this.lessonType = lessonType;
    }

    // PROPERTIES
    public int getId() {
        return id_Lesson;
    }

    public void setId(int id) {
        this.id_Lesson = id;
    }

    public LocalDate getLessonDate() {
        return lessonDate;
    }

    public void setLessonDate(LocalDate date) {
        this.lessonDate = date;
    }

    public int getMinBookings() {
        return minBookings;
    }

    public void setMinBookings(int minBookings) {
        this.minBookings = minBookings;
    }

    public int getMaxBookings() {
        return maxBookings;
    }

    public void setMaxBookings(int maxBookings) {
        this.maxBookings = maxBookings;
    }

    public int getNb_hours() {
        return nb_hours;
    }

    public void setNb_hours(int nb_hours) {
        this.nb_hours = nb_hours;
    }

    public boolean getIsCollective() {
        return isCollective;
    }

    public void setIsCollective(boolean isCollective) {
        this.isCollective = isCollective;
    }

    public LessonType getLessonType() {
        return lessonType;
    }

    public void setLessonType(LessonType lessonType) {
        this.lessonType = lessonType;
    }

    public Instructor getInstructor() {
        return instructor;
    }

    public void setInstructor(Instructor instructor) {
        this.instructor = instructor;
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
