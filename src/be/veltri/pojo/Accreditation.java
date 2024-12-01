package be.veltri.pojo;

import java.util.*;

import be.veltri.dao.AccreditationDAO;

public class Accreditation {
    // ATTRIBUTES
    private List<Instructor> instructors;    
    private List<LessonType> lessonTypes;     
    private String name;     
    private int id_accreditation;

    // CONSTRUCTORS
    public Accreditation() {	
    	if (this.instructors == null) {
            this.instructors = new ArrayList<>();
    	}
		if (this.lessonTypes == null) {
			this.lessonTypes = new ArrayList<>();
		}
    	
    }
    
    public Accreditation(int id_Accreditation, String name) {
        this();
        this.id_accreditation = id_Accreditation;
        this.name = name;
    }
    
    public Accreditation(int id_Accreditation, String name, LessonType lessonType) {
        this.id_accreditation = id_Accreditation;
        this.name = name;  
        addLessonType(lessonType);
    }
    
    public Accreditation(int id_Accreditation, String name, int id_LessonType, String level, double price) {
        this(id_Accreditation, name);
        LessonType lessonType = new LessonType(id_LessonType, level, price, this);
        addLessonType(lessonType);
    }

    // PROPERTIES
    public int getId() {
        return id_accreditation; 
    }

    public void setId(int id) {
        this.id_accreditation = id; 
    }
    
    public String getName() {
        return name; 
    }

    public void setName(String name) {
        this.name = name; 
    }

    public List<Instructor> getInstructors() {
        return instructors; 
    }

    public void setInstructors(List<Instructor> instructors) {
        this.instructors = instructors; 
    }

    public List<LessonType> getLessonTypes() {
        return lessonTypes; 
    }

    public void setLessonTypes(List<LessonType> lessonTypes) {
        this.lessonTypes = lessonTypes; 
    }

    //METHODS
    public boolean create(AccreditationDAO accreditationDAO) {
        return accreditationDAO.createDAO(this); 
    }
    
    public static int getNextId(AccreditationDAO accreditationDAO) {
        return accreditationDAO.getNextIdDAO(); 
    }

    public boolean update(AccreditationDAO accreditationDAO) {
        return accreditationDAO.updateDAO(this);
    }

    public boolean delete(AccreditationDAO accreditationDAO) {
        return accreditationDAO.deleteDAO(this);
    }

    public static Accreditation find(int id, AccreditationDAO accreditationDAO) {
        return accreditationDAO.findDAO(id);
    }
    
    public static List<Accreditation> findAll(AccreditationDAO accreditationDAO) {
        return accreditationDAO.findAllDAO();
    }
    
    public static List<Accreditation> findByInstructor(AccreditationDAO accreditationDAO, Instructor instructor) {
        return accreditationDAO.findByInstructorDAO(instructor);
    }

    
    public void addInstructor(Instructor instructor) {
    	if (this.instructors == null) {
			this.instructors = new ArrayList<>();
    	}
        if (instructor != null && !instructors.contains(instructor)) {
            instructors.add(instructor); 
        }
    }
    
    public void addLessonType(int id_LessonType, String level, double price)
    {
        new LessonType(id_LessonType, level, price, this);
    }
    
    public void addLessonType(LessonType lessonType) {
    	if (this.lessonTypes == null) {
	        this.lessonTypes = new ArrayList<>(); 

		}
        if (lessonType != null && !lessonTypes.contains(lessonType)) {
            lessonTypes.add(lessonType);
            lessonType.setAccreditation(this); 
        }
    }

    
    @Override
    public String toString() {
        return name;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id_accreditation);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)  return true;
        
        if (obj == null || getClass() != obj.getClass()) return false;

        Accreditation that = (Accreditation) obj;
        return id_accreditation == that.id_accreditation;
    }

}
