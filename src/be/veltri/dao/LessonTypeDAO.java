package be.veltri.dao;

import be.veltri.pojo.LessonType;
import java.sql.*;
import java.util.*;

public class LessonTypeDAO extends DAO<LessonType>{

    public LessonTypeDAO(Connection conn) {
    	super(conn);
    }
    
    public boolean createDAO(LessonType obj)
	{
		return false;
	}
    
    public boolean deleteDAO(LessonType obj)
	{
		return false;
	}
    
    public boolean updateDAO(LessonType obj)
    {
    	return false;
    }

	public LessonType findDAO(int id)
    {
		return null;
    }
    
    public List<LessonType> findAllDAO()
    {
    	return null;
    }

}
