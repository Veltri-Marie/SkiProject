package be.veltri.dao;

import be.veltri.pojo.Accreditation;
import be.veltri.pojo.Instructor;
import be.veltri.pojo.Lesson;
import be.veltri.pojo.LessonType;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;

public class LessonTypeDAO extends DAO<LessonType>{

    public LessonTypeDAO(Connection conn) {
    	super(conn);
    }
    
    public int getNextIdDAO() {
        String idSql = "SELECT LESSONTYPE_SEQ.NEXTVAL FROM DUAL";
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
    public boolean createDAO(LessonType lessonType) {
    	String sql = "INSERT INTO LessonType (id_lessonType, lesson_level, lesson_price) VALUES (?, ?, ?)";

        try (PreparedStatement pstmt = this.connect.prepareStatement(sql)) { 
        	pstmt.setInt(1, lessonType.getId());
            pstmt.setString(2, lessonType.getLevel());
            pstmt.setDouble(3, lessonType.getPrice());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0; 
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false; 
    }

    @Override
    public boolean deleteDAO(LessonType lessonType) {
    	String deleteLessonSql = "DELETE FROM Lesson WHERE id_lessonType = ?";
        String deleteAccreditationLessonTypeSql = "DELETE FROM AccreditationLessonType WHERE id_lessonType = ?";
        String deleteLessonTypeSql = "DELETE FROM LessonType WHERE id_lessonType = ?";
        
        
        try (
        		PreparedStatement pstmtLesson = this.connect.prepareStatement(deleteLessonSql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                PreparedStatement pstmtAccreditationLessonType = this.connect.prepareStatement(deleteAccreditationLessonTypeSql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                PreparedStatement pstmtLessonType = this.connect.prepareStatement(deleteLessonTypeSql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)

            ) {
	        	pstmtAccreditationLessonType.setInt(1, lessonType.getId());
	            pstmtAccreditationLessonType.executeUpdate();
	
	            pstmtLesson.setInt(1, lessonType.getId());
	            pstmtLesson.executeUpdate();
	
	            pstmtLessonType.setInt(1, lessonType.getId());
	            return pstmtLessonType.executeUpdate() > 0; 
            
            } catch (SQLException e) {
                System.err.println("Error deleting lesson: " + e.getMessage());
                return false;
            }
    }

    @Override
    public boolean updateDAO(LessonType lessonType) {
    	String sql = "UPDATE LessonType SET lesson_level = ?, lesson_price = ? WHERE id_lessonType = ?";
        try (PreparedStatement pstmt = this.connect.prepareStatement(
                sql, 
                ResultSet.TYPE_SCROLL_INSENSITIVE, 
                ResultSet.CONCUR_UPDATABLE)) { 

        	pstmt.setString(1, lessonType.getLevel());
            pstmt.setDouble(2, lessonType.getPrice());
        	pstmt.setInt(3, lessonType.getId());
        	return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public LessonType findDAO(int id) {
    	LessonType lessonType = null;
    	String sql = """
        		 SELECT LT.id_lessonType, LT.lesson_level, LT.lesson_price, 
                   A.id_accreditation, A.accreditation_name,
                   LISTAGG(L.id_lesson || ':' || L.lessonDate || ':' || L.minBookings || ':' || 
                           L.maxBookings || ':' || L.isCollective || ':' || L.nb_hours || ':' || L.id_lessonType, ',') 
                   WITHIN GROUP (ORDER BY L.id_lesson) AS lesson_list
            FROM LessonType LT
            LEFT JOIN Accreditation A ON LT.id_accreditation = A.id_accreditation
            LEFT JOIN Lesson L ON LT.id_lessonType = L.id_lessonType
            		        WHERE id_lessonType = ?

            GROUP BY LT.id_lessonType, LT.lesson_level, LT.lesson_price, 
                     A.id_accreditation, A.accreditation_name
        		""";
        try (PreparedStatement pstmt = this.connect.prepareStatement(
                sql, 
                ResultSet.TYPE_SCROLL_INSENSITIVE, 
                ResultSet.CONCUR_READ_ONLY)) {
            
            pstmt.setInt(1, id);  
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                	lessonType = setLessonTypeDAO(rs); 
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    	return lessonType;
    }

    @Override
    public List<LessonType> findAllDAO() {
        List<LessonType> lessonTypes = new ArrayList<>();
        String sql = """
            SELECT LT.id_lessonType, LT.lesson_level, LT.lesson_price, 
                   A.id_accreditation, A.accreditation_name,
                   LISTAGG(L.id_lesson || ':' || L.lessonDate || ':' || L.minBookings || ':' || 
                           L.maxBookings || ':' || L.isCollective || ':' || L.nb_hours || ':' || L.id_lessonType, ',') 
                   WITHIN GROUP (ORDER BY L.id_lesson) AS lesson_list
            FROM LessonType LT
            LEFT JOIN Accreditation A ON LT.id_accreditation = A.id_accreditation
            LEFT JOIN Lesson L ON LT.id_lessonType = L.id_lessonType
            GROUP BY LT.id_lessonType, LT.lesson_level, LT.lesson_price, 
                     A.id_accreditation, A.accreditation_name
            """;

        try (Statement stmt = this.connect.createStatement(
                ResultSet.TYPE_SCROLL_INSENSITIVE, 
                ResultSet.CONCUR_READ_ONLY);
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                LessonType lessonType = setLessonTypeDAO(rs);
                lessonTypes.add(lessonType);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lessonTypes;
    }


    
    private LessonType setLessonTypeDAO(ResultSet rs) throws SQLException {
        Accreditation accreditation = new Accreditation(
            rs.getInt("id_accreditation"),
            rs.getString("accreditation_name")
        );

        LessonType lessonType = new LessonType(
            rs.getInt("id_lessonType"),
            rs.getString("lesson_level"),
            rs.getDouble("lesson_price"),
            accreditation
        );

        String lessonList = rs.getString("lesson_list");
        if (lessonList != null && !lessonList.isEmpty()) {
            String[] lessonEntries = lessonList.split(",");
            for (String entry : lessonEntries) {
                String[] parts = entry.split(":");
                if (parts.length == 7) {
                    int lessonId = Integer.parseInt(parts[0]);
                    LocalDate lessonDate = LocalDate.parse(parts[1]);
                    int minBookings = Integer.parseInt(parts[2]);
                    int maxBookings = Integer.parseInt(parts[3]);
                    boolean isCollective = Integer.parseInt(parts[4]) == 1;
                    int nbHours = Integer.parseInt(parts[5]);
                    int lessonTypeId = Integer.parseInt(parts[6]);

                    if (lessonTypeId == lessonType.getId()) {
                        Lesson lesson = new Lesson(
                            lessonId,
                            lessonDate,
                            minBookings,
                            maxBookings,
                            nbHours,
                            isCollective,
                            new Instructor(), 
                            lessonType
                        );

                        lessonType.addLesson(lesson);
                    }
                }
            }
        }

        return lessonType;
    }

}
