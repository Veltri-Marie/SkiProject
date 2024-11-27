package be.veltri.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import be.veltri.pojo.Booking;
import be.veltri.pojo.Instructor;
import be.veltri.pojo.Lesson;
import be.veltri.pojo.LessonSession;
import be.veltri.pojo.Period;
import be.veltri.pojo.Skier;

public class LessonSessionDAO extends DAO<LessonSession> {

    public LessonSessionDAO(Connection conn) {
        super(conn);
    }
    
    public int getNextIdDAO() {
        String idSql = "SELECT lessonSession_seq.NEXTVAL FROM DUAL";
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
    public boolean createDAO(LessonSession lessonSession) {
        String sql = "INSERT INTO LessonSession (id_session_, session_type, id_booking) VALUES (?, ?, ?)";

        try (PreparedStatement pstmt = this.connect.prepareStatement(sql)) {
        	pstmt.setInt(1, lessonSession.getId());
            pstmt.setString(2, lessonSession.getSessionType());
            pstmt.setInt(3, lessonSession.getBooking().getId());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0; 
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false; 
    }

    @Override
    public boolean deleteDAO(LessonSession lessonSession) {
        String sql = "DELETE FROM LessonSession WHERE id_session = ?";

        try (PreparedStatement pstmt = this.connect.prepareStatement(sql)) {
            pstmt.setInt(1, lessonSession.getId());
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting session: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean updateDAO(LessonSession lessonSession) {
        String sql = "UPDATE LessonSession SET session_type = ?, id_booking = ? WHERE id_session = ?";

        try (PreparedStatement pstmt = this.connect.prepareStatement(sql)) {
            pstmt.setString(1, lessonSession.getSessionType());
            pstmt.setInt(2, lessonSession.getBooking().getId());
            pstmt.setInt(3, lessonSession.getId());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating session: " + e.getMessage());
            return false;
        }
    }

    @Override
    public LessonSession findDAO(int id) {
        String sql = "SELECT * FROM LessonSession WHERE id_session = ?";
        LessonSession lessonSession = null;

        try (PreparedStatement pstmt = this.connect.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    lessonSession = setLessonSessionDAO(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lessonSession;
    }

    @Override
    public List<LessonSession> findAllDAO() {
        List<LessonSession> lessonSessions = new ArrayList<>();
        String sql = """
                SELECT LS.id_session_, LS.session_type, LS.id_booking,
                       B.id_booking, B.insurance_opt, B.reservation_date, 
                       B.id_lesson, B.id_period, B.id_skier
                FROM LessonSession LS
                INNER JOIN Booking B ON LS.id_booking = B.id_booking
            """;

        try (Statement stmt = this.connect.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                lessonSessions.add(setLessonSessionDAO(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lessonSessions;
    }

    private LessonSession setLessonSessionDAO(ResultSet rs) throws SQLException {
        
        Lesson lesson = Lesson.find(rs.getInt("id_lesson"), this.connect);
        Instructor instructor = Instructor.find(rs.getInt("id_instructor"), this.connect);
        Period period = Period.find(rs.getInt("id_period"), this.connect);
        Skier skier = Skier.find(rs.getInt("id_skier"), this.connect);
        
        
        Booking booking = new Booking(
        		rs.getInt("id_booking"),
                rs.getDate("reservation_date").toLocalDate(),
                lesson,
                instructor,
                period,
                skier,
                rs.getBoolean("insurance_opt"));

        LessonSession lessonSession = new LessonSession(
        		rs.getInt("id_session_"),
        		rs.getString("session_type"),
        		booking);     

        return lessonSession;
    }
}
