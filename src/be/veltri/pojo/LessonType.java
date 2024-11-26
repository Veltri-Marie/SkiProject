package be.veltri.pojo;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import be.veltri.dao.LessonTypeDAO;

public class LessonType {
	// ATTRIBUTES
		private int id_LessonType;          
	    private String level;                       
	    private double price;     
	    private Accreditation accreditation; 
	    private List<Lesson> lessons;   

	    // CONSTRUCTORS
	    public LessonType() 
	    {
	    	if (lessons == null) {
	    		lessons = new ArrayList<>();
	    	}
	    	
	    }
	    
	    public LessonType(int id_LessonType, String level, double price, Accreditation accreditation) {
	    	this();
	        if (level == null || level.isEmpty()) {
	            throw new IllegalArgumentException("Level cannot be null or empty.");
	        }
	        if (price < 0) {
	            throw new IllegalArgumentException("Price cannot be negative.");
	        }
	        
	        this.id_LessonType = id_LessonType;
	        this.level = level;
	        this.price = price;
	        this.accreditation = accreditation;        
	    }

	    // PROPERTIES
	    public int getId() {
	        return id_LessonType; 
	    }

	    public void setId(int id) {
	        this.id_LessonType = id; 
	    }
	    
	    public String getLevel() {
	        return level;
	    }

	    public void setLevel(String level) {
	        if (level == null || level.isEmpty()) {
	            throw new IllegalArgumentException("Level cannot be null or empty.");
	        }
	        this.level = level;
	    }

	    public double getPrice() {
	        return price;
	    }

	    public void setPrice(double price) {
	        if (price < 0) {
	            throw new IllegalArgumentException("Price cannot be negative.");
	        }
	        this.price = price;
	    }
	    
	    public Accreditation getAccreditation() {
	        return accreditation;
	    }

	    public void setAccreditation(Accreditation accreditation) {
	    	if (accreditation == null) {
	            throw new IllegalArgumentException("Accreditation cannot be null.");
	        }
	        if (this.accreditation != accreditation) {
	            this.accreditation = accreditation;
	            accreditation.addLessonType(this);
	        }
	    }
	    
	    public List<Lesson> getLessons() {
	        return new ArrayList<>(lessons); 
	    }

	    public void setLessons(List<Lesson> lessons) {
	        if (lessons != null) {
	            this.lessons = new ArrayList<>(lessons);
	        } else {
	            throw new IllegalArgumentException("Lessons cannot be null.");
	        }
	    }
	    
	    // METHODS
	    public boolean create(Connection conn) {
	        LessonTypeDAO lessonTypeDAO = new LessonTypeDAO(conn);
	        return lessonTypeDAO.createDAO(this); 
	    }
	    
	    public static int getNextId(Connection conn) {
	        LessonTypeDAO lessonTypeDAO = new LessonTypeDAO(conn);
	        return lessonTypeDAO.getNextIdDAO(); 
	    }

	    public boolean update(Connection conn) {
	    	LessonTypeDAO lessonTypeDAO = new LessonTypeDAO(conn);
	        return lessonTypeDAO.updateDAO(this);
	    }

	    public boolean delete(Connection conn) {
	    	LessonTypeDAO lessonTypeDAO = new LessonTypeDAO(conn);
	        return lessonTypeDAO.deleteDAO(this);
	    }

	    public static LessonType find(int id, Connection conn) {
	    	LessonTypeDAO lessonTypeDAO = new LessonTypeDAO(conn);
	        return lessonTypeDAO.findDAO(id);
	    }
	    

	    public static List<LessonType> findAll(Connection conn) {
	    	LessonTypeDAO lessonTypeDAO = new LessonTypeDAO(conn);
	        return lessonTypeDAO.findAllDAO();
	    }
	    
	    public void addLesson(Lesson lesson) {
	    	if(lessons == null)
	    		lessons = new ArrayList<>();
	        if (lesson != null) {
	            lessons.add(lesson);
	        } else {
	            throw new IllegalArgumentException("Lesson cannot be null.");
	        }
	    }

	    @Override
	    public String toString() {
	        return accreditation + " " + level + " " + price + "€";
	    }

	    @Override
	    public int hashCode() {
	        return Objects.hash(id_LessonType);
	    }

	    @Override
	    public boolean equals(Object obj) {
	        if (this == obj) return true;
	        if (obj == null || getClass() != obj.getClass()) return false;
	        LessonType that = (LessonType) obj;
	        return id_LessonType == that.id_LessonType;
	    }

}
