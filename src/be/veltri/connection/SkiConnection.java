package be.veltri.connection;

import java.io.*;
import java.sql.*;
import java.util.*;
import javax.swing.JOptionPane;

// Singleton
public class SkiConnection {
    private static Connection instance = null;

    private SkiConnection() {
        try {
            Properties properties = new Properties();
            properties.load(new FileInputStream("src/config.properties"));

            Class.forName("oracle.jdbc.driver.OracleDriver");

            String url = properties.getProperty("db.url");
            String user = properties.getProperty("db.user");
            String password = properties.getProperty("db.password");
            System.setProperty("oracle.jdbc.Trace", "true");
            instance = DriverManager.getConnection(url, user, password);
            System.out.println("Connexion réussie !");
        } catch (ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(null, "Classe de driver introuvable : " + ex.getMessage());
            System.exit(0);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Erreur JDBC : " + ex.getMessage());
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Erreur lors du chargement du fichier de configuration : " + ex.getMessage());
        }

        if (instance == null) {
            JOptionPane.showMessageDialog(null, "La base de données est inaccessible, fermeture du programme.");
            System.exit(0);
        }
    }

    public static Connection getInstance() {
        if (instance == null) {
            new SkiConnection();
        }
        return instance;
    }

    public static void closeConnection() {
        if (instance != null) {
            try {
                instance.close();
                instance = null; 
                System.out.println("Connexion fermée !");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Erreur lors de la fermeture de la connexion : " + ex.getMessage());
            }
        }
    }
}
