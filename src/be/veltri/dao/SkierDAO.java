package be.veltri.dao;

import be.veltri.pojo.Skier;
import java.sql.*;
import java.util.List;

public class SkierDAO extends DAO<Skier> {

    public SkierDAO(Connection conn) {
        super(conn);
    }
    
    public int getNextIdDAO() {
        String idSql = "SELECT SKIER_SEQ.NEXTVAL FROM DUAL";
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
    public boolean createDAO(Skier skier) {
        String personSql = "INSERT INTO Person (id_Person, firstName, lastName, Birthdate) VALUES (PERSON_SEQ.NEXTVAL, ?, ?, ?)";
        String personIdSql = "SELECT PERSON_SEQ.CURRVAL FROM DUAL";
        String skierSql = "INSERT INTO Skier (id_skier, skier_phoneNumber, skier_email, id_Person) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement pstmtPerson = this.connect.prepareStatement(personSql);
             PreparedStatement pstmtPersonId = this.connect.prepareStatement(personIdSql);
             PreparedStatement pstmtSkier = this.connect.prepareStatement(skierSql)) {

            pstmtPerson.setString(1, skier.getFirstName());
            pstmtPerson.setString(2, skier.getLastName());
            pstmtPerson.setDate(3, Date.valueOf(skier.getBirthdate())); 
            pstmtPerson.executeUpdate();
            
            ResultSet rsPersonId = pstmtPersonId.executeQuery();
            int personId = -1;
            if (rsPersonId.next()) {
                personId = rsPersonId.getInt(1);
            }

            if (personId == -1) {
                throw new SQLException("Failed to retrieve generated Person ID.");
            }

			if (skier.getId() == 0) {
				skier.setId(getNextIdDAO());
			}
            pstmtSkier.setInt(1, skier.getId());
            pstmtSkier.setString(2, skier.getPhoneNumber());
            pstmtSkier.setString(3, skier.getEmail());
            pstmtSkier.setInt(4, personId);

            int rowsAffected = pstmtSkier.executeUpdate();
            return rowsAffected > 0; 
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false; 
    }
    
    public boolean deleteDAO(Skier obj)
	{
		return false;
	}
    
    public boolean updateDAO(Skier obj)
    {
    	return false;
    }

	public Skier findDAO(int id)
    {
		return null;
    }
    
    public List<Skier> findAllDAO()
    {
    	return null;
    }
}
