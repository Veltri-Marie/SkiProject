package be.veltri.dao;

import be.veltri.pojo.Instructor;
import be.veltri.pojo.Lesson;
import be.veltri.pojo.LessonType;
import be.veltri.pojo.Period;
import be.veltri.pojo.Skier;
import be.veltri.pojo.Accreditation;
import be.veltri.pojo.Booking;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class InstructorDAO extends DAO<Instructor> {

    public InstructorDAO(Connection conn) {
        super(conn);
    }
    
    public int getNextIdDAO() {
        String idSql = "SELECT INSTRUCTOR_SEQ.NEXTVAL FROM DUAL";
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
    public boolean createDAO(Instructor instructor) {
        String personSql = "INSERT INTO Person (id_Person, firstName, lastName, Birthdate) VALUES (PERSON_SEQ.NEXTVAL, ?, ?, ?)";
        String personIdSql = "SELECT PERSON_SEQ.CURRVAL FROM DUAL";
        String instructorSql = "INSERT INTO Instructor (id_instructor, instructor_hireDate, id_Person) VALUES (?, ?, ?)";
        String accreditationSql = "INSERT INTO InstructorAccreditation (id_instructor, id_accreditation) VALUES (?, ?)"; 

        try (
            PreparedStatement pstmtPerson = this.connect.prepareStatement(personSql);
            PreparedStatement pstmtPersonId = this.connect.prepareStatement(personIdSql);
            PreparedStatement pstmtInstructor = this.connect.prepareStatement(instructorSql);
            PreparedStatement pstmtAccreditation = this.connect.prepareStatement(accreditationSql)
        ) {
            pstmtPerson.setString(1, instructor.getFirstName());
            pstmtPerson.setString(2, instructor.getLastName());
            pstmtPerson.setDate(3, Date.valueOf(instructor.getBirthdate()));
            pstmtPerson.executeUpdate();

            ResultSet rsPersonId = pstmtPersonId.executeQuery();
            int personId = -1;
            if (rsPersonId.next()) {
                personId = rsPersonId.getInt(1);
            }
            rsPersonId.close();

            if (personId == -1) {
                throw new SQLException("Failed to retrieve generated Person ID.");
            }

            pstmtInstructor.setInt(1, instructor.getId());
            pstmtInstructor.setDate(2, Date.valueOf(instructor.getHireDate()));
            pstmtInstructor.setInt(3, personId);
            pstmtInstructor.executeUpdate();
            
            if (instructor.getAccreditations() != null) {
                for (Accreditation accreditation : instructor.getAccreditations()) {
                	pstmtAccreditation.setInt(1, instructor.getId()); 
                    pstmtAccreditation.setInt(2, accreditation.getId()); 
                    pstmtAccreditation.executeUpdate();
                }
                
            }

            return true; 

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false; 
    }
    
    @Override
    public boolean deleteDAO(Instructor obj)
	{
		return false;
	}
    
    @Override
    public boolean updateDAO(Instructor obj)
    {
    	return false;
    }
    
    @Override
    public Instructor findDAO(int id) {
        String sql = """
    		    SELECT 
			    I.id_instructor, 
			    I.instructor_hireDate,
			    P.firstName, 
			    P.lastName, 
			    P.Birthdate,
			    LISTAGG(A.accreditation_name || ':' || A.id_accreditation, ', ') 
			        WITHIN GROUP (ORDER BY A.id_accreditation) AS accreditation_data,
			    LISTAGG(B.id_booking || ':' || B.reservation_date || ':' || B.insurance_opt, ',') 
			        WITHIN GROUP (ORDER BY B.id_booking) AS bookings_list,
			    LISTAGG(
			        L.id_lesson || ';' || L.lessonDate || ';' || L.minBookings || ';' || 
			        L.maxBookings || ';' || L.isCollective || ';' || L.nb_hours, '-'
			    ) 
			        WITHIN GROUP (ORDER BY L.id_lesson) AS lesson_list
			FROM 
			    Instructor I
			INNER JOIN 
			    Person P ON I.id_Person = P.id_Person
			INNER JOIN 
			    InstructorAccreditation IA ON I.id_instructor = IA.id_instructor
			INNER JOIN 
			    Accreditation A ON IA.id_accreditation = A.id_accreditation
			LEFT JOIN 
			    Lesson L ON I.id_instructor = L.id_instructor
			LEFT JOIN 
			    Booking B ON L.id_lesson = B.id_lesson
			WHERE I.id_instructor = ?
			GROUP BY 
			    I.id_instructor, 
			    I.instructor_hireDate, 
			    P.firstName, 
			    P.lastName, 
			    P.Birthdate
			    """;

        try (PreparedStatement pstmt = this.connect.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Instructor instructor = setInstructorDAO(rs);
                    return instructor;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding instructor by ID: " + e.getMessage());
        }
        return null;
    }



    public List<Instructor> findByLastnameDAO(String lastname) {
        List<Instructor> instructors = new ArrayList<>();
        String sql = """
        	    SELECT 
        	        I.id_instructor, 
        	        I.instructor_hireDate,
        	        P.firstName, 
        	        P.lastName, 
        	        P.Birthdate,
        	        LISTAGG(A.accreditation_name || ':' || A.id_accreditation, ', ') 
        	            WITHIN GROUP (ORDER BY A.id_accreditation) AS accreditation_data,
        	        LISTAGG(B.id_booking || ':' || B.reservation_date || ':' || B.insurance_opt, ',') 
        	            WITHIN GROUP (ORDER BY B.id_booking) AS bookings_list,
        	        LISTAGG(
        	            L.id_lesson || ';' || L.lessonDate || ';' || L.minBookings || ';' || 
        	            L.maxBookings || ';' || L.isCollective || ';' || L.nb_hours, '-'
        	        ) 
        	            WITHIN GROUP (ORDER BY L.id_lesson) AS lesson_list
        	    FROM 
        	        Instructor I
        	    INNER JOIN 
        	        Person P ON I.id_Person = P.id_Person
        	    INNER JOIN 
        	        InstructorAccreditation IA ON I.id_instructor = IA.id_instructor
        	    INNER JOIN 
        	        Accreditation A ON IA.id_accreditation = A.id_accreditation
        	    LEFT JOIN 
        	        Lesson L ON I.id_instructor = L.id_instructor
        	    LEFT JOIN 
        	        Booking B ON L.id_lesson = B.id_lesson
        	    WHERE P.Lastname LIKE ?
        	    GROUP BY 
        	        I.id_instructor, 
        	        I.instructor_hireDate, 
        	        P.firstName, 
        	        P.lastName, 
        	        P.Birthdate
        	""";
        try (PreparedStatement pstmt = this.connect.prepareStatement(sql)) {
            pstmt.setString(1, "%" + lastname + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
					Instructor instructor = setInstructorDAO(rs);
					instructors.add(instructor);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding instructors by last name: " + e.getMessage());
        }
        return instructors;
    }
   
    @Override
    public List<Instructor> findAllDAO() {
        List<Instructor> instructors = new ArrayList<>();
        String sql = """
        		SELECT 
			    I.id_instructor, 
			    I.instructor_hireDate,
			    P.firstName, 
			    P.lastName, 
			    P.Birthdate,
			    LISTAGG(A.accreditation_name || ':' || A.id_accreditation, ', ') 
			        WITHIN GROUP (ORDER BY A.id_accreditation) AS accreditation_data,
			    LISTAGG(B.id_booking || ':' || B.reservation_date || ':' || B.insurance_opt, ',') 
			        WITHIN GROUP (ORDER BY B.id_booking) AS bookings_list,
			    LISTAGG(
			        L.id_lesson || ';' || L.lessonDate || ';' || L.minBookings || ';' || 
			        L.maxBookings || ';' || L.isCollective || ';' || L.nb_hours, '-'
			    ) 
			        WITHIN GROUP (ORDER BY L.id_lesson) AS lesson_list
			FROM 
			    Instructor I
			INNER JOIN 
			    Person P ON I.id_Person = P.id_Person
			INNER JOIN 
			    InstructorAccreditation IA ON I.id_instructor = IA.id_instructor
			INNER JOIN 
			    Accreditation A ON IA.id_accreditation = A.id_accreditation
			LEFT JOIN 
			    Lesson L ON I.id_instructor = L.id_instructor
			LEFT JOIN 
			    Booking B ON L.id_lesson = B.id_lesson
			GROUP BY 
			    I.id_instructor, 
			    I.instructor_hireDate, 
			    P.firstName, 
			    P.lastName, 
			    P.Birthdate
        	""";



        try (Statement stmt = this.connect.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                instructors.add(setInstructorDAO(rs)); 
            }
        } catch (SQLException e) {
            System.err.println("Error finding all instructors: " + e.getMessage());
        }
        return instructors;
    }
    
    private Instructor setInstructorDAO(ResultSet rs) throws SQLException {
        String accreditationData = rs.getString("accreditation_data");
        Accreditation firstAccreditation = null;

        if (accreditationData != null && !accreditationData.isEmpty()) {
            String[] accreditations = accreditationData.split(", ");
            if (accreditations.length > 0) {
                String[] firstAcc = accreditations[0].split(";");
                if (firstAcc.length == 2) {
                    int firstAccId = Integer.parseInt(firstAcc[1].trim());
                    String firstAccName = firstAcc[0].trim();
                    firstAccreditation = new Accreditation(firstAccId, firstAccName);
                }
            }
        }

        Instructor instructor = new Instructor(
            rs.getInt("id_instructor"),
            rs.getString("firstName"),
            rs.getString("lastName"),
            rs.getDate("Birthdate").toLocalDate(),
            rs.getDate("instructor_hireDate").toLocalDate(),
            firstAccreditation
        );

        if (accreditationData != null && !accreditationData.isEmpty()) {
            String[] accreditations = accreditationData.split(", ");
            for (String accreditationEntry : accreditations) {
                String[] parts = accreditationEntry.split(":");
                if (parts.length == 2) {
                    int accId = Integer.parseInt(parts[1].trim());
                    String accName = parts[0].trim();
                    Accreditation accreditation = new Accreditation(accId, accName);

                    if (!instructor.getAccreditations().contains(accreditation)) {
                        instructor.addAccreditation(accreditation);
                    }
                }
            }
        }

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
                        new Lesson(),
                        instructor,
                        new Period(),
                        new Skier(),
                        insuranceOpt
                    );

                    if (booking != null && !instructor.getBookings().contains(booking)) {
                        instructor.addBooking(booking);
                    }
                }
            }
        }

        String lessonList = rs.getString("lesson_list");
        if (lessonList != null && !lessonList.isEmpty()) {
            String[] lessonEntries = lessonList.split("-");
            for (String entry : lessonEntries) {
                String[] parts = entry.split(";");
                if (parts.length == 6) {
                        int lessonId = Integer.parseInt(parts[0]);
                        String dateTime = parts[1].split(" ")[0]; 
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy");
                        LocalDate lessonDate = LocalDate.parse(dateTime, formatter);
                        int minBookings = Integer.parseInt(parts[2]);
                        int maxBookings = Integer.parseInt(parts[3]);
                        boolean isCollective = Integer.parseInt(parts[4]) == 1;
                        int nbHours = Integer.parseInt(parts[5]);

                        Lesson lesson = new Lesson(
                            lessonId,
                            lessonDate,
                            minBookings,
                            maxBookings,
                            nbHours,
                            isCollective,
                            instructor,
                            new LessonType()
                        );

                        if (instructor != null && !instructor.getLessons().contains(lesson)) {
                            instructor.addLesson(lesson);
                        } 
                } 
            }
        }

        

        return instructor;
    }
}
