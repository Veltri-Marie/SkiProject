package be.veltri.ski;

import java.awt.*;
import java.sql.Connection;

import javax.swing.*;

import be.veltri.connection.SkiConnection;

public class MainPage extends JFrame {
    private static final long serialVersionUID = 1L;
    

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                MainPage frame = new MainPage();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public MainPage() {
        Connection conn = SkiConnection.getInstance();
        setTitle("Domaine Châtelet Ski School");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 950, 500);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        getContentPane().add(mainPanel);
        // Titre
        JLabel titleLabel = new JLabel("Domain Châtelet Ski School", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Castellar", Font.BOLD, 24));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Onglets
        JTabbedPane tabbedPane = new JTabbedPane();
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        tabbedPane.addTab("Skier", new SkierPanel());
        tabbedPane.addTab("Instructor", new InstructorPanel());    
        tabbedPane.addTab("Lesson", new LessonPanel());

    }
}
