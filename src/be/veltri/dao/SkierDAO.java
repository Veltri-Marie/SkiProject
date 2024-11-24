package be.veltri.dao;

import be.veltri.pojo.Skier;
import java.sql.*;
import java.util.List;

public class SkierDAO extends DAO<Skier> {

    public SkierDAO(Connection conn) {
        super(conn);
    }
    
    public boolean createDAO(Skier obj)
	{
		return false;
	}
    
    public boolean deleteDAO(Skier obj)
	{
		return false;
	}
    
    public boolean updateDAO(Skier obj)
    {
    	return false;
    }

	public Skier findDAO(int id)
    {
		return null;
    }
    
    public List<Skier> findAllDAO()
    {
    	return null;
    }
}
