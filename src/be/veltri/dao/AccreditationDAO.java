package be.veltri.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.chrono.IsoEra;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;

import be.veltri.pojo.Accreditation;
import be.veltri.pojo.Instructor;
import be.veltri.pojo.LessonType;

public class AccreditationDAO extends DAO<Accreditation>{

    public AccreditationDAO(Connection conn) {
    	super(conn);
    }
    
    public boolean createDAO(Accreditation obj)
	{
		return false;
	}
    
    public boolean deleteDAO(Accreditation obj)
	{
		return false;
	}
    
    public boolean updateDAO(Accreditation obj)
    {
    	return false;
    }

	public Accreditation findDAO(int id)
    {
		return null;
    }
    
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
                if (parts.length == 3) { // Ensure we have all required fields
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
