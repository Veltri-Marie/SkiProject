package be.veltri.dao;

import be.veltri.pojo.Booking;
import be.veltri.pojo.Instructor;
import be.veltri.pojo.Lesson;
import be.veltri.pojo.Period;
import be.veltri.pojo.Skier;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class PeriodDAO extends DAO<Period>{

    public PeriodDAO(Connection conn) {
    	super(conn);
    }
    
    public int getNextIdDAO() {
        String idSql = "SELECT PERIOD_SEQ.NEXTVAL FROM DUAL";
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
    public boolean createDAO(Period period) {
        String sql = "INSERT INTO Period (id_period, startDate, endDate, isVacation) VALUES (?, ?, ?, ?)";

        try (PreparedStatement pstmt = this.connect.prepareStatement(sql)) { 
        	 pstmt.setInt(1, period.getId());
        	 pstmt.setDate(2, Date.valueOf(period.getStartDate()));
             pstmt.setDate(3, Date.valueOf(period.getEndDate()));
             pstmt.setBoolean(4, period.getIsVacation());

             int rowsAffected = pstmt.executeUpdate();
             return rowsAffected > 0; 
         } catch (SQLException e) {
             e.printStackTrace();
         }

         return false; 
     }

    @Override
    public boolean deleteDAO(Period period) {
    	 String deleteBookingsSql = "DELETE FROM Booking WHERE id_period = ?";
         String deletePeriodSql = "DELETE FROM Period WHERE id_period = ?";
         
         try (
             PreparedStatement pstmtBooking = this.connect.prepareStatement(deleteBookingsSql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
             PreparedStatement pstmtPeriod = this.connect.prepareStatement(deletePeriodSql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)
         ) {
             pstmtBooking.setInt(1, period.getId());
             pstmtBooking.executeUpdate();

             pstmtPeriod.setInt(1, period.getId());
             return pstmtPeriod.executeUpdate() > 0;

         } catch (SQLException e) {
             System.err.println("Error deleting skier: " + e.getMessage());
             return false;
         }
    }

    @Override
    public boolean updateDAO(Period period) {
        String sql = "UPDATE Period SET startDate = ?, endDate = ?, isVacation = ? WHERE id_period = ?";
        try (PreparedStatement pstmt = this.connect.prepareStatement(
                sql, 
                ResultSet.TYPE_SCROLL_INSENSITIVE, 
                ResultSet.CONCUR_UPDATABLE)) { 

            pstmt.setDate(1, Date.valueOf(period.getStartDate()));
            pstmt.setDate(2, Date.valueOf(period.getEndDate()));
            pstmt.setBoolean(3, period.getIsVacation());  
            pstmt.setInt(4, period.getId());  
            
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Period findDAO(int id) {
        Period period = null;
        String sql = """
                SELECT P.id_period, P.startDate, P.endDate, P.isVacation,
                       LISTAGG(B.id_booking || ':' || B.reservation_date || ':' || B.insurance_opt, ',') 
                       WITHIN GROUP (ORDER BY B.id_booking) AS bookings_list
                FROM Period P
                LEFT JOIN Booking B ON P.id_period = B.id_period
                WHERE id_period = ?
                GROUP BY P.id_period, P.startDate, P.endDate, P.isVacation
                """;
        
        try (PreparedStatement pstmt = this.connect.prepareStatement(
        		sql, 
                ResultSet.TYPE_SCROLL_INSENSITIVE, 
                ResultSet.CONCUR_READ_ONLY)) {
            
            pstmt.setInt(1, id); 
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {  
                    period = setPeriodDAO(rs); 
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return period; 
    }
    
    
    public Period findByDateDAO(LocalDate date) {
    	String sql = """
                SELECT P.id_period, P.startDate, P.endDate, P.isVacation,
                       LISTAGG(B.id_booking || ':' || B.reservation_date || ':' || B.insurance_opt, ',') 
                       WITHIN GROUP (ORDER BY B.id_booking) AS bookings_list
                FROM Period P
                LEFT JOIN Booking B ON P.id_period = B.id_period
                WHERE ? BETWEEN startDate AND endDate
                GROUP BY P.id_period, P.startDate, P.endDate, P.isVacation
                """;

        try (PreparedStatement pstmt = this.connect.prepareStatement(
                sql,
                ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY)) {

            pstmt.setDate(1, java.sql.Date.valueOf(date));
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Period(
                        rs.getInt("id_period"),
                        rs.getDate("startDate").toLocalDate(),
                        rs.getDate("endDate").toLocalDate(),
                        rs.getBoolean("isVacation")
                    );
                } else {
                    System.err.println("No Period found for date: " + date);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null; 
    }


    @Override
    public List<Period> findAllDAO() {
        List<Period> periods = new ArrayList<>();
        String sql = """
            SELECT P.id_period, P.startDate, P.endDate, P.isVacation,
                   LISTAGG(B.id_booking || ':' || B.reservation_date || ':' || B.insurance_opt, ',') 
                   WITHIN GROUP (ORDER BY B.id_booking) AS bookings_list
            FROM Period P
            LEFT JOIN Booking B ON P.id_period = B.id_period
            GROUP BY P.id_period, P.startDate, P.endDate, P.isVacation
            """;

        try (Statement stmt = this.connect.createStatement(
                ResultSet.TYPE_SCROLL_INSENSITIVE, 
                ResultSet.CONCUR_READ_ONLY);
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Period period = setPeriodDAO(rs); 
                periods.add(period);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return periods;
    }


    private Period setPeriodDAO(ResultSet rs) throws SQLException {
        Period period = new Period(
            rs.getInt("id_period"), 
            rs.getDate("startDate").toLocalDate(), 
            rs.getDate("endDate").toLocalDate(),
            rs.getBoolean("isVacation")
        );

        String bookingsList = rs.getString("bookings_list");
        if (bookingsList != null && !bookingsList.isEmpty()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy");
            String[] bookingEntries = bookingsList.split(",");
            for (String entry : bookingEntries) {
                String[] parts = entry.split(":");
                if (parts.length == 3) {
                    int bookingId = Integer.parseInt(parts[0].trim());
                    LocalDate reservationDate = LocalDate.parse(parts[1].trim(), formatter);
                    boolean insuranceOpt = Boolean.parseBoolean(parts[2].trim());

                    Booking booking = new Booking(
                        bookingId,
                        reservationDate,
                        new Lesson(),       
                        new Instructor(),   
                        period,
                        new Skier(),      
                        insuranceOpt
                    );

                    period.addBooking(booking);
                }
            }
        }

        return period;
    }


}
