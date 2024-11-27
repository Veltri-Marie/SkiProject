package be.veltri.dao;

import be.veltri.pojo.Booking;

import java.sql.*;
import java.util.List;

public class BookingDAO extends DAO<Booking> {
	public BookingDAO(Connection conn) {
        super(conn);
    }
    
    public int getNextIdDAO() {
        String idSql = "SELECT booking_seq.NEXTVAL FROM DUAL";
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
    public boolean createDAO(Booking booking) {
        String sql = "INSERT INTO Booking (id_booking, insurance_opt, reservation_date, id_lesson, id_period, id_skier) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = this.connect.prepareStatement(sql)) {
            pstmt.setInt(1, booking.getId());
            pstmt.setBoolean(2, booking.getInsuranceOpt());
            pstmt.setDate(3, Date.valueOf(booking.getReservationDate()));
            pstmt.setInt(4, booking.getLesson().getId());
            pstmt.setInt(5, booking.getPeriod().getId());
            pstmt.setInt(6, booking.getSkier().getId());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0; 
        } catch (SQLException e) {
            e.printStackTrace();
        }

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
