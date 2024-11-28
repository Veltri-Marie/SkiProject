package be.veltri.dao;

import be.veltri.pojo.Accreditation;
import be.veltri.pojo.Instructor;
import be.veltri.pojo.LessonType;

import java.sql.*;
import java.time.LocalDate;
import java.time.chrono.IsoEra;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.*;

public class AccreditationDAO extends DAO<Accreditation>{

    public AccreditationDAO(Connection conn) {
    	super(conn);
    }
    
    public int getNextIdDAO() {
        String idSql = "SELECT accreditation_seq.NEXTVAL FROM DUAL";
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
    public boolean createDAO(Accreditation accreditation) {
        String sql = "INSERT INTO Accreditation (id_accreditation, accreditation_name) VALUES (?, ?)";

        try (PreparedStatement pstmt = this.connect.prepareStatement(sql)) {
        	pstmt.setInt(1, accreditation.getId());
            pstmt.setString(2, accreditation.getName());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0; 
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false; 
    }

    @Override
    public boolean deleteDAO(Accreditation accreditation) {
        String sql = "DELETE FROM Accreditation WHERE id_accreditation = ?";
        try (PreparedStatement pstmt = this.connect.prepareStatement(
                sql, 
                ResultSet.TYPE_SCROLL_INSENSITIVE, 
                ResultSet.CONCUR_UPDATABLE)) { 

            pstmt.setInt(1, accreditation.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateDAO(Accreditation accreditation) {
        String sql = "UPDATE Accreditation SET accreditation_name = ? WHERE id_accreditation = ?";
        try (PreparedStatement pstmt = this.connect.prepareStatement(
                sql, 
                ResultSet.TYPE_SCROLL_INSENSITIVE, 
                ResultSet.CONCUR_UPDATABLE)) { 

        	pstmt.setString(1, accreditation.getName());
        	pstmt.setInt(2, accreditation.getId());
        	return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Accreditation findDAO(int id) {
    	Accreditation accreditation = null;
        String sql = """
        		SELECT A.id_accreditation,  A.accreditation_name,
                LISTAGG(LT.id_lessonType || ':' || LT.lesson_level || ':' || LT.lesson_price, ',') 
                    WITHIN GROUP (ORDER BY LT.id_lessonType) AS lessonType_list,
                LISTAGG(I.id_instructor || ':' || P.firstName || ':' || P.lastName || ':' || P.Birthdate || ':' || I.instructor_hireDate, ',') 
                    WITHIN GROUP (ORDER BY I.id_instructor) AS instructor_list
	            FROM  Accreditation A
	            INNER JOIN LessonType LT ON LT.id_accreditation = A.id_accreditation
	            INNER JOIN InstructorAccreditation IA ON A.id_accreditation = IA.id_accreditation
	            INNER JOIN Instructor I ON IA.id_instructor = I.id_instructor
	            INNER JOIN Person P ON I.id_Person = P.id_Person
	            WHERE A.id_accreditation = ?
	            GROUP BY A.id_accreditation, A.accreditation_name
        		""";

        try (PreparedStatement pstmt = this.connect.prepareStatement(
                sql, 
                ResultSet.TYPE_SCROLL_INSENSITIVE, 
                ResultSet.CONCUR_READ_ONLY)) {
            
            pstmt.setInt(1, id);  
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                	accreditation = setAccreditationDAO(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return accreditation;
    }
    
    public List<Accreditation> findByNameDAO(String name) throws SQLException {
        List<Accreditation> accreditations = new ArrayList<>();
        String sql = """
        	    SELECT A.id_accreditation,  A.accreditation_name,
                LISTAGG(LT.id_lessonType || ':' || LT.lesson_level || ':' || LT.lesson_price, ',') 
                    WITHIN GROUP (ORDER BY LT.id_lessonType) AS lessonType_list,
                LISTAGG(I.id_instructor || ':' || P.firstName || ':' || P.lastName || ':' || P.Birthdate || ':' || I.instructor_hireDate, ',') 
                    WITHIN GROUP (ORDER BY I.id_instructor) AS instructor_list
	            FROM  Accreditation A
	            LEFT JOIN LessonType LT ON LT.id_accreditation = A.id_accreditation
	            LEFT JOIN InstructorAccreditation IA ON A.id_accreditation = IA.id_accreditation
	            LEFT JOIN Instructor I ON IA.id_instructor = I.id_instructor
	            LEFT JOIN Person P ON I.id_Person = P.id_Person
	            WHERE A.accreditation_name LIKE ?
	            GROUP BY A.id_accreditation, A.accreditation_name
    	    """;

        try (PreparedStatement pstmt = this.connect.prepareStatement(sql)) {
            pstmt.setString(1, "%" + name + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Accreditation accreditation = setAccreditationDAO(rs);
                accreditations.add(accreditation);
            }
        }
        return accreditations;
    }
    
    public List<Accreditation> findByInstructorDAO(Instructor instructor) {
        List<Accreditation> accreditations = new ArrayList<>();
        String sql = """
        	    SELECT A.id_accreditation, A.accreditation_name,
    	        LISTAGG(LT.id_lessonType || ':' || LT.lesson_level || ':' || LT.lesson_price, ',') 
    	            WITHIN GROUP (ORDER BY LT.id_lessonType) AS lessonType_list,
	            LISTAGG(I.id_instructor || ':' || P.firstName || ':' || P.lastName || ':' || P.Birthdate || ':' || I.instructor_hireDate, ',') 
    		       WITHIN GROUP (ORDER BY I.id_instructor) AS instructor_list
        	    FROM Accreditation A
        	    LEFT JOIN LessonType LT ON LT.id_accreditation = A.id_accreditation
        	    LEFT JOIN InstructorAccreditation IA ON IA.id_accreditation = A.id_accreditation
        	    LEFT JOIN Instructor I ON IA.id_instructor = I.id_instructor
        		LEFT JOIN Person P ON I.id_Person = P.id_Person
        	    WHERE IA.id_instructor = ?
        	    GROUP BY A.id_accreditation, A.accreditation_name
        	""";

        try (PreparedStatement stmt = this.connect.prepareStatement(sql)) {
            
            stmt.setInt(1, instructor.getId()); 
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                	 Accreditation accreditation = setAccreditationDAO(rs);
					accreditations.add(accreditation);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return accreditations;
    }

    @Override
    public List<Accreditation> findAllDAO() {
        List<Accreditation> accreditations = new ArrayList<>();
        String sql = """
        	    SELECT A.id_accreditation,  A.accreditation_name,
                LISTAGG(LT.id_lessonType || ':' || LT.lesson_level || ':' || LT.lesson_price, ',') 
                    WITHIN GROUP (ORDER BY LT.id_lessonType) AS lessonType_list,
                LISTAGG(I.id_instructor || ':' || P.firstName || ':' || P.lastName || ':' || P.Birthdate || ':' || I.instructor_hireDate, ',') 
                    WITHIN GROUP (ORDER BY I.id_instructor) AS instructor_list
	            FROM  Accreditation A
	            LEFT JOIN LessonType LT ON LT.id_accreditation = A.id_accreditation
	            LEFT JOIN InstructorAccreditation IA ON A.id_accreditation = IA.id_accreditation
	            LEFT JOIN Instructor I ON IA.id_instructor = I.id_instructor
	            LEFT JOIN Person P ON I.id_Person = P.id_Person
	            GROUP BY A.id_accreditation, A.accreditation_name
        """;


        try (Statement stmt = this.connect.createStatement(
                ResultSet.TYPE_SCROLL_INSENSITIVE, 
                ResultSet.CONCUR_READ_ONLY);
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Accreditation accreditation = setAccreditationDAO(rs);
                accreditations.add(accreditation);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return accreditations;
    }
    
    private Accreditation setAccreditationDAO(ResultSet rs) throws SQLException {
        Accreditation accreditation = new Accreditation(
            rs.getInt("id_accreditation"),
            rs.getString("accreditation_name")
        );
        
        String lessonTypeList = rs.getString("lessonType_list");
        if (lessonTypeList != null && !lessonTypeList.isEmpty()) {
            String[] lessonTypeEntries = lessonTypeList.split(",");
            for (String entry : lessonTypeEntries) {
                String[] parts = entry.split(":");
                if (parts.length == 3) {
                    int lessonTypeId = Integer.parseInt(parts[0].trim());
                    String lessonLevel = parts[1].trim();
                    double lessonPrice = Double.parseDouble(parts[2].trim());

                    LessonType lessonType = new LessonType(
                        lessonTypeId,
                        lessonLevel,
                        lessonPrice,
                        accreditation 
                    );

                    accreditation.addLessonType(lessonType);
                }
            }
        }

        String instructorList = rs.getString("instructor_list");
        if (instructorList != null && !instructorList.isEmpty()) {
            String[] instructorEntries = instructorList.split(",");
            DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .appendPattern("dd/MM/yy")
                .parseDefaulting(ChronoField.ERA, IsoEra.CE.getValue())
                .toFormatter();

            for (String entry : instructorEntries) {
                String[] parts = entry.split(":");
                if (parts.length == 5) {
                    int instructorId = Integer.parseInt(parts[0]);
                    String firstName = parts[1];
                    String lastName = parts[2];

                    LocalDate birthdate = LocalDate.parse(parts[3], formatter);
                    LocalDate hireDate = LocalDate.parse(parts[4], formatter);

                    if (birthdate.getYear() > LocalDate.now().getYear()) {
                        birthdate = birthdate.minusYears(100);
                    }
                    if (hireDate.getYear() > LocalDate.now().getYear()) {
                        hireDate = hireDate.minusYears(100);
                    }

                    LocalDate today = LocalDate.now();
                    if (birthdate.isAfter(today)) {
                        throw new IllegalArgumentException("Birthdate cannot be in the future: " + birthdate);
                    }
                    if (hireDate.isAfter(today)) {
                        throw new IllegalArgumentException("Hire date cannot be in the future: " + hireDate);
                    }

                    Instructor instructor = new Instructor(
                        instructorId,
                        firstName,
                        lastName,
                        birthdate,
                        hireDate,
                        accreditation
                    );

                    accreditation.addInstructor(instructor);
                }
            }
        }

        return accreditation;
    }
}
