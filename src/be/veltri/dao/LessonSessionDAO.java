package be.veltri.dao;

import java.sql.*;
import java.util.List;

import be.veltri.pojo.LessonSession;


public class LessonSessionDAO extends DAO<LessonSession> {

    public LessonSessionDAO(Connection conn) {
        super(conn);
    }
    
    public boolean createDAO(LessonSession obj)
	{
		return false;
	}
    
    public boolean deleteDAO(LessonSession obj)
	{
		return false;
	}
    
    public boolean updateDAO(LessonSession obj)
    {
    	return false;
    }

	public LessonSession findDAO(int id)
    {
		return null;
    }
    
    public List<LessonSession> findAllDAO()
    {
    	return null;
    }
}
