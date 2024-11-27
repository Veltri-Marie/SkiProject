package be.veltri.dao;


import be.veltri.pojo.Accreditation;
import be.veltri.pojo.Booking;
import be.veltri.pojo.Instructor;
import be.veltri.pojo.Lesson;
import be.veltri.pojo.LessonType;
import be.veltri.pojo.Period;
import be.veltri.pojo.Skier;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
    public boolean deleteDAO(Lesson lesson) {
   	 String deleteBookingsSql = "DELETE FROM Booking WHERE id_lesson = ?";
        String deleteLessonSql = "DELETE FROM Lesson WHERE id_lesson = ?";
        
        
        try (
                PreparedStatement pstmtBooking = this.connect.prepareStatement(deleteBookingsSql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                PreparedStatement pstmtLesson = this.connect.prepareStatement(deleteLessonSql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)
            ) {
                pstmtBooking.setInt(1, lesson.getId());
                pstmtBooking.executeUpdate();

                pstmtLesson.setInt(1, lesson.getId());
                return pstmtLesson.executeUpdate() > 0;

            } catch (SQLException e) {
                System.err.println("Error deleting lesson: " + e.getMessage());
                return false;
            }
   }
    
    @Override
    public boolean updateDAO(Lesson lesson) {
        String sql = "UPDATE Lesson SET lessonDate = ?, minBookings = ?, maxBookings = ?, nb_hours = ?, isCollective = ?, id_instructor = ?, id_lessonType = ? WHERE id_lesson = ?";
        try (PreparedStatement pstmt = this.connect.prepareStatement(sql)) { 

            pstmt.setDate(1, Date.valueOf(lesson.getLessonDate())); 
            pstmt.setInt(2, lesson.getMinBookings());              
            pstmt.setInt(3, lesson.getMaxBookings());              
            pstmt.setInt(4, lesson.getNb_hours());                 
            pstmt.setBoolean(5, lesson.getIsCollective());         
            pstmt.setInt(6, lesson.getInstructor().getId());      
            pstmt.setInt(7, lesson.getLessonType().getId());       
            pstmt.setInt(8, lesson.getId());                      

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Lesson findDAO(int id) {
        Lesson lesson = null;
        String sql = """
        	    SELECT 
			    L.id_lesson, L.lessonDate, L.minBookings, L.maxBookings, L.isCollective, L.nb_hours, L.id_lessonType, L.id_instructor, 
			    LT.lesson_level, LT.lesson_price, 
			    I.id_instructor, I.instructor_hireDate, I.id_Person,
			    P.firstName, P.lastName, P.Birthdate,  
			    A.id_accreditation, A.accreditation_name,
			    LISTAGG(B.id_booking || ':' || B.reservation_date || ':' || B.insurance_opt, ',') 
			        WITHIN GROUP (ORDER BY B.id_booking) AS bookings_list
			FROM 
			    Lesson L
			INNER JOIN 
			    LessonType LT ON L.id_lessonType = LT.id_lessonType
			INNER JOIN 
			    Accreditation A ON LT.id_accreditation = A.id_accreditation
			INNER JOIN 
			    Instructor I ON L.id_instructor = I.id_instructor
			INNER JOIN 
			    Person P ON I.id_Person = P.id_Person
			LEFT JOIN 
			    Booking B ON L.id_lesson = B.id_lesson
			WHERE
				 L.id_lesson = ?
			GROUP BY 
			    L.id_lesson, L.lessonDate, L.minBookings, L.maxBookings, L.isCollective, L.nb_hours, L.id_lessonType, L.id_instructor,
			    LT.lesson_level, LT.lesson_price, 
			    I.id_instructor, I.instructor_hireDate, I.id_Person,
			    P.firstName, P.lastName, P.Birthdate, 
			    A.id_accreditation, A.accreditation_name
        	""";

        try (PreparedStatement pstmt = this.connect.prepareStatement(
                sql, 
                ResultSet.TYPE_SCROLL_INSENSITIVE, 
                ResultSet.CONCUR_READ_ONLY)) {
            
            pstmt.setInt(1, id);  
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    lesson = setLessonDAO(rs); 
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lesson;
    }
    
    @Override
    public List<Lesson> findAllDAO() {
        List<Lesson> lessons = new ArrayList<>();
        String sql = """
                SELECT 
			    L.id_lesson, L.lessonDate, L.minBookings, L.maxBookings, L.isCollective, L.nb_hours, L.id_lessonType, L.id_instructor, 
			    LT.lesson_level, LT.lesson_price, 
			    I.id_instructor, I.instructor_hireDate, I.id_Person,
			    P.firstName, P.lastName, P.Birthdate,  
			    A.id_accreditation, A.accreditation_name,
			    LISTAGG(B.id_booking || ':' || B.reservation_date || ':' || B.insurance_opt, ',') 
			        WITHIN GROUP (ORDER BY B.id_booking) AS bookings_list
			FROM 
			    Lesson L
			INNER JOIN 
			    LessonType LT ON L.id_lessonType = LT.id_lessonType
			INNER JOIN 
			    Accreditation A ON LT.id_accreditation = A.id_accreditation
			INNER JOIN 
			    Instructor I ON L.id_instructor = I.id_instructor
			INNER JOIN 
			    Person P ON I.id_Person = P.id_Person
			LEFT JOIN 
			    Booking B ON L.id_lesson = B.id_lesson
			GROUP BY 
			    L.id_lesson, L.lessonDate, L.minBookings, L.maxBookings, L.isCollective, L.nb_hours, L.id_lessonType, L.id_instructor,
			    LT.lesson_level, LT.lesson_price, 
			    I.id_instructor, I.instructor_hireDate, I.id_Person,
			    P.firstName, P.lastName, P.Birthdate, 
			    A.id_accreditation, A.accreditation_name
         """;


        try (Statement stmt = this.connect.createStatement(
                ResultSet.TYPE_SCROLL_INSENSITIVE, 
                ResultSet.CONCUR_READ_ONLY);
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Lesson lesson = setLessonDAO(rs); 
                lessons.add(lesson);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lessons;
    }
    
    private Lesson setLessonDAO(ResultSet rs) throws SQLException {
        Accreditation accreditation = new Accreditation(
                rs.getInt("id_accreditation"),
                rs.getString("accreditation_name"));

        Instructor instructor = new Instructor(
                rs.getInt("id_instructor"),
                rs.getString("firstName"),
                rs.getString("lastName"),
                rs.getDate("Birthdate").toLocalDate(),
                rs.getDate("instructor_hireDate").toLocalDate(),
                accreditation);

        LessonType lessonType = new LessonType(
                rs.getInt("id_lessonType"),
                rs.getString("lesson_level"),
                rs.getDouble("lesson_price"),
                accreditation);

        Lesson lesson = new Lesson(
                rs.getInt("id_lesson"),
                rs.getDate("lessonDate").toLocalDate(),
                rs.getInt("minBookings"),
                rs.getInt("maxBookings"),
                rs.getInt("nb_hours"),
                rs.getBoolean("isCollective"),
                instructor,
                lessonType);


        String bookingsList = rs.getString("bookings_list");
        if (bookingsList != null && !bookingsList.isEmpty()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy");
            String[] bookingEntries = bookingsList.split(",");
            for (String entry : bookingEntries) {
                String[] parts = entry.split(":");
                if (parts.length == 3) {
                    int bookingId = Integer.parseInt(parts[0]);
                    LocalDate reservationDate = LocalDate.parse(parts[1], formatter);
                    boolean insuranceOpt = Boolean.parseBoolean(parts[2]);

                    Booking booking = new Booking(
                        bookingId,
                        reservationDate,
                        lesson,
                        instructor,
                        new Period(),
                        new Skier(),
                        insuranceOpt
                    );

                    lesson.addBooking(booking);

                }
            }
        }

        return lesson;
    }
    
}