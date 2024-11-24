package be.veltri.dao;

import be.veltri.pojo.Booking;

import java.sql.*;
import java.util.List;

public class BookingDAO extends DAO<Booking> {
    public BookingDAO(Connection conn) {
        super(conn);
    }
    public boolean createDAO(Booking obj)
	{
		return false;
	}
    
    public boolean deleteDAO(Booking obj)
	{
		return false;
	}
    
    public boolean updateDAO(Booking obj)
    {
    	return false;
    }

	public Booking findDAO(int id)
    {
		return null;
    }
    
    public List<Booking> findAllDAO()
    {
    	return null;
    }
}
