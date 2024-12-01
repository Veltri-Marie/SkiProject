package be.veltri.ski;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

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
        setTitle("Domaine Châtelet Ski School");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 1020, 650);

        JPanel mainPanel = new JPanel() {

			private static final long serialVersionUID = 1L;

			@Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                Color color1 = new Color(70, 130, 180);
                Color color2 = new Color(0, 0, 0); 
                GradientPaint gradient = new GradientPaint(0, 0, color1, 0, getHeight(), color2);
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new BorderLayout());
        getContentPane().add(mainPanel);

        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Domaine Châtelet Ski School", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Castellar", Font.BOLD, 32)); 
        titleLabel.setForeground(new Color(255, 255, 255)); 
        headerPanel.add(titleLabel, BorderLayout.CENTER);


        JPanel logoPanel = new JPanel();
        logoPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        ImageIcon logoIcon = new ImageIcon("medias/logo_ski_school.jpg");
        Image logoImage = logoIcon.getImage().getScaledInstance(170, 170, Image.SCALE_SMOOTH);
        logoIcon = new ImageIcon(logoImage);
        JLabel logoLabel = new JLabel(logoIcon);
        logoPanel.add(logoLabel);
        
        headerPanel.add(logoPanel, BorderLayout.EAST);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(new Color(70, 130, 180)); 
        tabbedPane.setForeground(Color.WHITE);
        tabbedPane.setFont(new Font("Arial", Font.PLAIN, 16)); 
        tabbedPane.setUI(new BasicTabbedPaneUI() {
            @Override
            protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected) {
                if (isSelected) {
                    g.setColor(new Color(193, 158, 58)); 
                } else {
                    g.setColor(new Color(173, 216, 230)); 
                }
                g.fillRect(x, y, w, h);
            }
        });

        tabbedPane.addTab("Bookings", new BookingPanel());
        tabbedPane.addTab("Skiers", new SkierPanel());
        tabbedPane.addTab("Instructors", new InstructorPanel());
        tabbedPane.addTab("Lessons", new LessonPanel());

        customizePanel(tabbedPane);

        mainPanel.add(tabbedPane, BorderLayout.CENTER);
    }

    private void customizePanel(JTabbedPane tabbedPane) {
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            JPanel panel = (JPanel) tabbedPane.getComponentAt(i);
            panel.setBackground(new Color(240, 248, 255)); 
            panel.setBorder(BorderFactory.createLineBorder(new Color(173, 216, 230), 2));

        }
    }
}
