package be.veltri.dao;


import be.veltri.pojo.Period;

import java.sql.*;
import java.util.*;

public class PeriodDAO extends DAO<Period>{

    public PeriodDAO(Connection conn) {
    	super(conn);
    }
    
    public boolean createDAO(Period obj)
	{
		return false;
	}
    
    public boolean deleteDAO(Period obj)
	{
		return false;
	}
    
    public boolean updateDAO(Period obj)
    {
    	return false;
    }

	public Period findDAO(int id)
    {
		return null;
    }
    
    public List<Period> findAllDAO()
    {
    	return null;
    }


}
