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
	public Instructor findDAO(int id)
    {
		return null;
    }
    
    @Override
    public List<Instructor> findAllDAO()
    {
    	return null;
    }
}
