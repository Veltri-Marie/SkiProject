package be.veltri.dao;


import be.veltri.pojo.Lesson;
import java.sql.*;
import java.sql.Date;
import java.util.*;;

public class LessonDAO extends DAO<Lesson>{

    public LessonDAO(Connection conn) {
    	super(conn);
    }
    
    public int getNextIdDAO() {
        String idSql = "SELECT LESSON_SEQ.NEXTVAL FROM DUAL";
        try (PreparedStatement idPstmt = this.connect.prepareStatement(idSql);
             ResultSet rs = idPstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; 
    }
    
    @Override
    public boolean createDAO(Lesson lesson) {
    	String sql = "INSERT INTO Lesson (id_lesson, lessonDate, minBookings, maxBookings, isCollective, nb_hours, id_lessonType, id_instructor) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = this.connect.prepareStatement(sql)) {
        	pstmt.setInt(1, lesson.getId());
        	pstmt.setDate(2, Date.valueOf(lesson.getLessonDate()));
        	pstmt.setInt(3, lesson.getMinBookings());
            pstmt.setInt(4, lesson.getMaxBookings());
            pstmt.setBoolean(5, lesson.getIsCollective());
            pstmt.setInt(6, lesson.getNb_hours()); 
            pstmt.setInt(7, lesson.getLessonType().getId());  
            pstmt.setInt(8, lesson.getInstructor().getId());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0; 
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false; 
    }
    
    @Override
    public boolean deleteDAO(Lesson obj)
	{
		return false;
	}
    
    @Override
    public boolean updateDAO(Lesson obj)
    {
    	return false;
    }

    @Override
	public Lesson findDAO(int id)
    {
		return null;
    }
    
    @Override
    public List<Lesson> findAllDAO()
    {
    	return null;
    }
}