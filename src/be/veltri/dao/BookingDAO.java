package be.veltri.dao;

import be.veltri.pojo.Accreditation;
import be.veltri.pojo.Booking;
import be.veltri.pojo.Instructor;
import be.veltri.pojo.Lesson;
import be.veltri.pojo.LessonSession;
import be.veltri.pojo.LessonType;
import be.veltri.pojo.Period;
import be.veltri.pojo.Skier;

import java.sql.*;
import java.util.ArrayList;
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
    
	@Override
	public List<Booking> findAllDAO() {
        List<Booking> bookings = new ArrayList<>();

        String sql = """
            SELECT B.id_booking, B.insurance_opt, B.reservation_date,
                   S.id_skier, P1.firstName AS skierFirstName, P1.lastName AS skierLastName, P1.Birthdate AS skierBirthdate, S.skier_phoneNumber, S.skier_email,
                   I.id_instructor, P2.firstName AS instructorFirstName, P2.lastName AS instructorLastName, P2.Birthdate AS instructorBirthdate, I.instructor_hireDate,
                   L.id_lesson, L.lessonDate, L.minBookings, L.maxBookings, L.nb_hours, L.isCollective,
                   LT.id_lessonType, LT.lesson_level, LT.lesson_price, LT.id_accreditation AS ltAccreditationId, LT_A.accreditation_name AS ltAccreditationName,
                   P.id_period, P.startDate, P.endDate, P.isVacation, 
                   LISTAGG(A.accreditation_name || ':' || A.id_accreditation, ', ') WITHIN GROUP (ORDER BY A.id_accreditation) AS instructorAccreditations, 
                   LISTAGG(LS.id_session_ || ':' || LS.session_type, ',') WITHIN GROUP (ORDER BY LS.id_session_) AS lessonSession_list
            FROM Booking B
            INNER JOIN Skier S ON B.id_skier = S.id_skier
            INNER JOIN Person P1 ON S.id_Person = P1.id_Person
            INNER JOIN Period P ON B.id_period = P.id_period
            INNER JOIN Lesson L ON B.id_lesson = L.id_lesson
            INNER JOIN Instructor I ON L.id_instructor = I.id_instructor
            INNER JOIN Person P2 ON I.id_Person = P2.id_Person
            LEFT JOIN LessonType LT ON L.id_lessonType = LT.id_lessonType
            LEFT JOIN Accreditation LT_A ON LT.id_accreditation = LT_A.id_accreditation
            LEFT JOIN InstructorAccreditation IA ON I.id_instructor = IA.id_instructor
            LEFT JOIN Accreditation A ON IA.id_accreditation = A.id_accreditation
            LEFT JOIN LessonSession LS ON LS.id_booking = B.id_booking
            GROUP BY B.id_booking, B.insurance_opt, B.reservation_date,
                     S.id_skier, P1.firstName, P1.lastName, P1.Birthdate, S.skier_phoneNumber, S.skier_email,
                     I.id_instructor, P2.firstName, P2.lastName, P2.Birthdate, I.instructor_hireDate,
                     L.id_lesson, L.lessonDate, L.minBookings, L.maxBookings, L.nb_hours, L.isCollective,
                     LT.id_lessonType, LT.lesson_level, LT.lesson_price, LT.id_accreditation, LT_A.accreditation_name,
                     P.id_period, P.startDate, P.endDate, P.isVacation
        """;

        try (Statement stmt = this.connect.createStatement(
                ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY);
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Booking booking = setBookingDAO(rs);
                bookings.add(booking);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return bookings;
    }

    private Booking setBookingDAO(ResultSet rs) throws SQLException {
        Skier skier = new Skier(
            rs.getInt("id_skier"),
            rs.getString("skierFirstName"),
            rs.getString("skierLastName"),
            rs.getDate("skierBirthdate").toLocalDate(),
            rs.getString("skier_phoneNumber"),
            rs.getString("skier_email")
        );

        String accreditationNames = rs.getString("instructorAccreditations");
        Instructor instructor = null;

        if (accreditationNames != null) {
            String[] accreditations = accreditationNames.split(", ");
            Accreditation firstAccreditation = null;

            for (String acc : accreditations) {
                String[] parts = acc.split(":");
                if (parts.length == 2) {
                    int accId = Integer.parseInt(parts[1].trim());
                    String accName = parts[0].trim();

                    Accreditation accreditation = new Accreditation(accId, accName);

                    if (firstAccreditation == null) {
                        firstAccreditation = accreditation;
                        instructor = new Instructor(
                            rs.getInt("id_instructor"),
                            rs.getString("instructorFirstName"),
                            rs.getString("instructorLastName"),
                            rs.getDate("instructorBirthdate").toLocalDate(),
                            rs.getDate("instructor_hireDate").toLocalDate(),
                            firstAccreditation
                        );
                    } else if (!instructor.getAccreditations().contains(accreditation)) {
                        instructor.addAccreditation(accreditation);
                    }
                }
            }
        }

        Accreditation lessonTypeAccreditation = new Accreditation(
            rs.getInt("ltAccreditationId"),
            rs.getString("ltAccreditationName")
        );

        LessonType lessonType = new LessonType(
            rs.getInt("id_lessonType"),
            rs.getString("lesson_level"),
            rs.getDouble("lesson_price"),
            lessonTypeAccreditation
        );

        Lesson lesson = new Lesson(
            rs.getInt("id_lesson"),
            rs.getDate("lessonDate").toLocalDate(),
            rs.getInt("minBookings"),
            rs.getInt("maxBookings"),
            rs.getInt("nb_hours"),
            rs.getBoolean("isCollective"),
            instructor,
            lessonType
        );

        Period period = new Period(
            rs.getInt("id_period"),
            rs.getDate("startDate").toLocalDate(),
            rs.getDate("endDate").toLocalDate(),
            rs.getBoolean("isVacation")
        );

        Booking booking = new Booking(
            rs.getInt("id_booking"),
            rs.getDate("reservation_date").toLocalDate(),
            lesson,
            instructor,
            period,
            skier,
            rs.getBoolean("insurance_opt")
        );

        String lessonSessionList = rs.getString("lessonSession_list");
        if (lessonSessionList != null && !lessonSessionList.isEmpty()) {
            String[] sessionEntries = lessonSessionList.split(",");
            for (String entry : sessionEntries) {
                String[] parts = entry.split(":");
                if (parts.length == 2) {
                        int sessionId = Integer.parseInt(parts[0].trim());
                        String sessionType = parts[1].trim();

                        LessonSession lessonSession = new LessonSession(
                            sessionId,
                            sessionType,
                            booking 
                        );
                        booking.addLessonSession(lessonSession);
                }
            }
        }
        return booking;
    }
}
