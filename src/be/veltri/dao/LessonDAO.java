package be.veltri.dao;


import be.veltri.pojo.Lesson;
import java.sql.*;
import java.util.*;;

public class LessonDAO extends DAO<Lesson>{

    public LessonDAO(Connection conn) {
    	super(conn);
    }
    
    public boolean createDAO(Lesson obj)
	{
		return false;
	}
    
    public boolean deleteDAO(Lesson obj)
	{
		return false;
	}
    
    public boolean updateDAO(Lesson obj)
    {
    	return false;
    }

	public Lesson findDAO(int id)
    {
		return null;
    }
    
    public List<Lesson> findAllDAO()
    {
    	return null;
    }
}