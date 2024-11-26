package be.veltri.dao;

import be.veltri.pojo.Booking;
import be.veltri.pojo.Instructor;
import be.veltri.pojo.Lesson;
import be.veltri.pojo.Period;
import be.veltri.pojo.Skier;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
    
    @Override 
    public boolean deleteDAO(Skier skier) {
        String deleteBookingsSql = "DELETE FROM Booking WHERE id_skier = ?";
        String deleteSkierSql = "DELETE FROM Skier WHERE id_skier = ?";
        String deletePersonSql = "DELETE FROM Person WHERE id_Person = ?";

        try (
            PreparedStatement pstmtBooking = this.connect.prepareStatement(deleteBookingsSql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            PreparedStatement pstmtSkier = this.connect.prepareStatement(deleteSkierSql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            PreparedStatement pstmtPerson = this.connect.prepareStatement(deletePersonSql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)
        ) {
            int personId = findPersonIdBySkierId(skier.getId());
            if (personId == -1) {
                System.err.println("No associated person found for skier ID: " + skier.getId());
                return false;
            }

            pstmtBooking.setInt(1, skier.getId());
            pstmtBooking.executeUpdate();

            pstmtSkier.setInt(1, skier.getId());
            pstmtSkier.executeUpdate();

            pstmtPerson.setInt(1, personId);
            return pstmtPerson.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting skier: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean updateDAO(Skier skier) {
        String updatePersonSql = "UPDATE Person SET firstName = ?, lastName = ?, Birthdate = ? WHERE id_Person = ?";
        String updateSkierSql = "UPDATE Skier SET skier_phoneNumber = ?, skier_email = ? WHERE id_skier = ?";
        
        try (PreparedStatement pstmtPerson = this.connect.prepareStatement(updatePersonSql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
             PreparedStatement pstmtSkier = this.connect.prepareStatement(updateSkierSql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
            
            int personId = findPersonIdBySkierId(skier.getId());
            if (personId == -1) {
                System.err.println("Failed to retrieve associated person ID.");
                return false;
            }
            
            pstmtPerson.setString(1, skier.getFirstName());
            pstmtPerson.setString(2, skier.getLastName());
            pstmtPerson.setDate(3, Date.valueOf(skier.getBirthdate()));
            pstmtPerson.setInt(4, personId);
            
            int rowsAffectedPerson = pstmtPerson.executeUpdate();
            
            pstmtSkier.setString(1, skier.getPhoneNumber());
            pstmtSkier.setString(2, skier.getEmail());
            pstmtSkier.setInt(3, skier.getId()); 
            
            int rowsAffectedSkier = pstmtSkier.executeUpdate();
            
            return rowsAffectedPerson > 0 && rowsAffectedSkier > 0;
        } catch (SQLException e) {
            System.err.println("Error updating skier: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private int findPersonIdBySkierId(int skierId) {
        String sql = "SELECT id_Person FROM Skier WHERE id_skier = ?";
        try (PreparedStatement pstmt = this.connect.prepareStatement(sql)) {
            pstmt.setInt(1, skierId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_Person");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching person ID by skier ID: " + e.getMessage());
        }
        return -1; 
    }
    
    @Override
    public Skier findDAO(int id) {
        Skier skier = null;
        String sql = """
        		SELECT S.id_skier, S.skier_phoneNumber, S.skier_email, P.id_Person, P.firstName,  P.lastName,  P.Birthdate,
        	        LISTAGG(B.id_booking || ':' || B.reservation_date || ':' || B.insurance_opt, ',') 
        	            WITHIN GROUP (ORDER BY B.id_booking) AS bookings_list
        	    FROM Skier S
        	    INNER JOIN Person P ON S.id_Person = P.id_Person
        	    LEFT JOIN Booking B ON S.id_skier = B.id_skier
        	    WHERE S.id_skier LIKE ?
        	    GROUP BY S.id_skier, S.skier_phoneNumber, S.skier_email, P.id_Person, P.firstName,  P.lastName,  P.Birthdate
            """;
        
        try (PreparedStatement pstmt = this.connect.prepareStatement(sql)) {
            pstmt.setInt(1, id);  
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    skier = setSkierDAO(rs); 
                    skier.setId(rs.getInt("id_skier"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return skier;
    }
    
    public List<Skier> findByLastnameDAO(String lastname) throws SQLException {
        List<Skier> skiers = new ArrayList<>();
        String sql = """
        	    SELECT S.id_skier, S.skier_phoneNumber, S.skier_email, P.id_Person, P.firstName,  P.lastName,  P.Birthdate,
        	        LISTAGG(B.id_booking || ':' || B.reservation_date || ':' || B.insurance_opt, ',') 
        	            WITHIN GROUP (ORDER BY B.id_booking) AS bookings_list
        	    FROM Skier S
        	    INNER JOIN Person P ON S.id_Person = P.id_Person
        	    LEFT JOIN Booking B ON S.id_skier = B.id_skier
        	    WHERE P.lastName LIKE ?
        	    GROUP BY S.id_skier, S.skier_phoneNumber, S.skier_email, P.id_Person, P.firstName,  P.lastName,  P.Birthdate
        	    """;

        
        try (PreparedStatement pstmt = this.connect.prepareStatement(sql)) {
            pstmt.setString(1, "%" + lastname + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Skier skier = setSkierDAO(rs); 
                skiers.add(skier);
            }
        }
        return skiers;
    }
    
    @Override
    public List<Skier> findAllDAO() {
        List<Skier> skiers = new ArrayList<>();
        String sql = """
        	    SELECT S.id_skier, S.skier_phoneNumber, S.skier_email, P.id_Person, P.firstName,  P.lastName,  P.Birthdate,
        	        LISTAGG(B.id_booking || ':' || B.reservation_date || ':' || B.insurance_opt, ',') 
        	            WITHIN GROUP (ORDER BY B.id_booking) AS bookings_list
        	    FROM Skier S
        	    INNER JOIN Person P ON S.id_Person = P.id_Person
        	    LEFT JOIN Booking B ON S.id_skier = B.id_skier
        	    GROUP BY S.id_skier, S.skier_phoneNumber, S.skier_email, P.id_Person, P.firstName,  P.lastName,  P.Birthdate
        	    """;

        try (Statement stmt = this.connect.createStatement(
                ResultSet.TYPE_SCROLL_INSENSITIVE, 
                ResultSet.CONCUR_READ_ONLY);
             ResultSet rs = stmt.executeQuery(sql)) {
             
            while (rs.next()) {
                Skier skier = setSkierDAO(rs); 
                skiers.add(skier);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all skiers: " + e.getMessage());
        }
        return skiers;
    }

    
    private Skier setSkierDAO(ResultSet rs) throws SQLException {

        Skier skier = new Skier(
    		rs.getInt("id_skier"),
    		rs.getString("firstName"),
    		rs.getString("lastName"),
    		rs.getDate("Birthdate").toLocalDate(),
    		rs.getString("skier_phoneNumber"),
    		rs.getString("skier_email")        		
    		);
        
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
	                    new Instructor(),
	                    new Period(),
	                    skier,
	                    insuranceOpt
	                );
	                skier.addBooking(booking);
                }
            }
        }
        return skier;
    }
}
