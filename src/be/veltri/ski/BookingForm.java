package be.veltri.ski;

import java.sql.Connection;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import be.veltri.connection.SkiConnection;
import be.veltri.dao.BookingDAO;
import be.veltri.dao.LessonDAO;
import be.veltri.dao.PeriodDAO;
import be.veltri.dao.SkierDAO;
import be.veltri.pojo.Accreditation;
import be.veltri.pojo.Booking;
import be.veltri.pojo.Instructor;
import be.veltri.pojo.Lesson;
import be.veltri.pojo.Period;
import be.veltri.pojo.LessonSession;
import be.veltri.pojo.Skier;

public class BookingForm extends JDialog {

    private static final long serialVersionUID = 1L;
    private DefaultListModel<Skier> skierListModel = new DefaultListModel<>();
    private JList<Skier> listSkiers;
    private DefaultListModel<Lesson> lessonListModel = new DefaultListModel<>();
    private JList<Lesson> listLessons;
    private JCheckBox chckbxNewCheckBox;
    private JLabel lblTotal;
    private JTable tableSessions;
    private DefaultTableModel sessionTableModel;    
    

    private Connection conn = SkiConnection.getInstance();
    
    private BookingDAO bookingDAO = new BookingDAO(conn);
    private PeriodDAO periodDAO = new PeriodDAO(conn);
    private SkierDAO skierDAO = new SkierDAO(conn);
    private LessonDAO lessonDAO = new LessonDAO(conn);

    public BookingForm() {
        setTitle("New Booking");
        setSize(671, 493);
        getContentPane().setLayout(null);
        setLocationRelativeTo(null);

        initializeComponents();
        loadSkiersFromDB();
        loadLessonsFromDB();
        loadSessionTable();
        setVisible(true);
    }

    private void initializeComponents() {
        JLabel lblSkier = new JLabel("Skier :");
        lblSkier.setBounds(30, 20, 100, 25);
        getContentPane().add(lblSkier);

        listSkiers = new JList<>(skierListModel);
        JScrollPane scrollSkiers = new JScrollPane(listSkiers);
        scrollSkiers.setBounds(140, 20, 500, 50);
        getContentPane().add(scrollSkiers);

        JLabel lblLesson = new JLabel("Lesson :");
        lblLesson.setBounds(30, 90, 100, 25);
        getContentPane().add(lblLesson);

        listLessons = new JList<>(lessonListModel);
        listLessons.addListSelectionListener(e -> loadSessionTable());
        JScrollPane scrollLessons = new JScrollPane(listLessons);
        scrollLessons.setBounds(140, 90, 500, 100);
        getContentPane().add(scrollLessons);

        JLabel lblSessions = new JLabel("Select Sessions:");
        lblSessions.setBounds(30, 210, 150, 25);
        getContentPane().add(lblSessions);

		sessionTableModel = new DefaultTableModel(new Object[]{"Day", "Morning", "Afternoon"}, 0) {
			private static final long serialVersionUID = 1L;

			@Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 0 ? String.class : Boolean.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column > 0;
            }
        };
	    
        tableSessions = new JTable(sessionTableModel);
        JScrollPane scrollSessions = new JScrollPane(tableSessions);
        scrollSessions.setBounds(140, 210, 500, 120);
        getContentPane().add(scrollSessions);

        sessionTableModel.addTableModelListener(e -> updateTotalPrice());

        JLabel lblInsurance = new JLabel("Insurance :");
        lblInsurance.setBounds(30, 350, 100, 25);
        getContentPane().add(lblInsurance);

        chckbxNewCheckBox = new JCheckBox("");
        chckbxNewCheckBox.setBounds(140, 350, 100, 25);
        chckbxNewCheckBox.addActionListener(e -> updateTotalPrice());
        getContentPane().add(chckbxNewCheckBox);

        JButton btnSave = new JButton("Save");
        btnSave.setBounds(433, 411, 80, 25);
        btnSave.addActionListener(e -> saveBooking());
        getContentPane().add(btnSave);

        JButton btnCancel = new JButton("Cancel");
        btnCancel.setBounds(533, 411, 80, 25);
        btnCancel.addActionListener(e -> dispose());
        getContentPane().add(btnCancel);

        JLabel lblTot = new JLabel("Total :");
        lblTot.setBounds(36, 411, 100, 25);
        getContentPane().add(lblTot);

        lblTotal = new JLabel("0.0 €");
        lblTotal.setBounds(146, 411, 200, 25);
        getContentPane().add(lblTotal);

        JSeparator separator = new JSeparator();
        separator.setBounds(30, 392, 605, 9);
        getContentPane().add(separator);
    }


    private void saveBooking() {
    	
    	Skier skier = listSkiers.getSelectedValue();
        if (skier == null) {
            JOptionPane.showMessageDialog(this, "Please select a skier.");
            return;
        }
        Lesson lesson = listLessons.getSelectedValue();
        if (lesson == null) {
            JOptionPane.showMessageDialog(this, "Please select a lesson.");
            return;
        }

        Accreditation accreditation = lesson.getLessonType().getAccreditation();

        int skierAge = skier.calculateAge();

        boolean isSnowboard = accreditation.getName().toLowerCase().contains("snowboard");
        boolean isEnfant = accreditation.getName().toLowerCase().contains("enfant");

        if (skierAge < 13 && !isEnfant) {
            JOptionPane.showMessageDialog(this, "Skier under 13 must choose an accreditation with 'Enfant'.");
            return;
        }

        if (isEnfant) {
            if ((isSnowboard && skierAge < 6) || (!isSnowboard && (skierAge < 4 || skierAge > 12))) {
                JOptionPane.showMessageDialog(this, "Skier does not meet the age requirements for this accreditation.");
                return;
            }
        }

        Instructor instructor = lesson.getInstructor();
        Period period = Period.findByDate(lesson.getLessonDate(), periodDAO);
        if (period == null) {
            JOptionPane.showMessageDialog(this, "No period found for the lesson date.");
            return;
        }
        
        LocalDate currentDate = LocalDate.now();
        LocalDate lessonDate = lesson.getLessonDate();
        if (period.getIsVacation()) {
            if (lessonDate.isBefore(currentDate.plusDays(7))) {
                JOptionPane.showMessageDialog(this, "For vacation periods, lessons can only be booked up to 1 week in advance.");
                return;
            }
        } else {
            if (lessonDate.isBefore(currentDate.plusMonths(1))) {
                JOptionPane.showMessageDialog(this, "For non-vacation periods, lessons can only be booked up to 1 month in advance.");
                return;
            }
        }

        if (lesson.getCurrentBookingsCount() >= lesson.getMaxBookings()) {
            JOptionPane.showMessageDialog(this, "This lesson has reached the maximum number of participants.");
            return;
        }

        int selectedSessions = 0;

        for (int i = 0; i < sessionTableModel.getRowCount(); i++) {
            boolean morning = (boolean) sessionTableModel.getValueAt(i, 1);
            boolean afternoon = (boolean) sessionTableModel.getValueAt(i, 2);

            if (morning) selectedSessions++;
            if (afternoon) selectedSessions++;
        }

        if (selectedSessions != 6 && lesson.getIsCollective()) {
            JOptionPane.showMessageDialog(this, "Please select exactly 6 sessions.");
            return;
        }

        int nextBookingId = Booking.getNextId(bookingDAO);
        if (nextBookingId != -1) {            
            Booking booking = new Booking(nextBookingId, LocalDate.now(ZoneId.systemDefault()), lesson, instructor, period, skier, chckbxNewCheckBox.isSelected());
            booking.create(bookingDAO);
            

            for (int i = 0; i < sessionTableModel.getRowCount(); i++) {
                boolean morning = (boolean) sessionTableModel.getValueAt(i, 1);
                boolean afternoon = (boolean) sessionTableModel.getValueAt(i, 2);

                if (morning) {
                    saveSession(booking, "Morning");
                }

                if (afternoon) {
                    saveSession(booking, "Afternoon");
                }
            }

            JOptionPane.showMessageDialog(this, "Booking added successfully!");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add booking.");
        }
    }

    private void saveSession(Booking booking, String sessionType) {
        int newId = LessonSession.getNextId(conn); 
        if (newId != -1) {
            LessonSession lessonSession = new LessonSession(
                newId,
                sessionType,
                booking
            );

            if (!lessonSession.create(conn)) {
                JOptionPane.showMessageDialog(this, "Failed to add session for booking.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }





    private void updateTotalPrice() {
        Lesson selectedLesson = listLessons.getSelectedValue();
        boolean insuranceOpt = chckbxNewCheckBox.isSelected();

        if (selectedLesson != null) {
            Booking tempBooking = new Booking();
            tempBooking.setLesson(selectedLesson);
            tempBooking.setInsuranceOpt(insuranceOpt);

            int countMorning = 0;
            int countAfternoon = 0;

            for (int i = 0; i < sessionTableModel.getRowCount(); i++) {
                boolean morning = (boolean) sessionTableModel.getValueAt(i, 1);
                boolean afternoon = (boolean) sessionTableModel.getValueAt(i, 2);

                if (morning) countMorning++;
                if (afternoon) countAfternoon++;
            }

            double totalPrice = tempBooking.calculatePrice();
            if (countMorning > 0 && countAfternoon > 0) {
                totalPrice *= 0.85;
            }

            lblTotal.setText(String.format("%.2f €", totalPrice));
        } else {
            lblTotal.setText("0.0 €");
        }
    }


    private void loadSkiersFromDB() {
        skierListModel.clear();
        List<Skier> skiers = Skier.findAll(skierDAO);
        for (Skier skier : skiers) {
            skierListModel.addElement(skier);
        }
    }

    private void loadLessonsFromDB() {
        lessonListModel.clear();
        List<Lesson> lessons = Lesson.findAll(lessonDAO);
        for (Lesson lesson : lessons) {
            lessonListModel.addElement(lesson);
        }
    }
    
    private void loadSessionTable() {
        Lesson selectedLesson = listLessons.getSelectedValue();

        if (selectedLesson == null || !selectedLesson.getIsCollective()) {
            tableSessions.setVisible(false);
            return;
        }

        tableSessions.setVisible(true);
        sessionTableModel.setRowCount(0); 

        LocalDate lessonDate = selectedLesson.getLessonDate();
        for (int i = 0; i < 6; i++) { 
            String day = lessonDate.plusDays(i).getDayOfWeek().toString();
            sessionTableModel.addRow(new Object[]{day, false, false});
        }
    }



    public static void main(String[] args) {
        BookingForm dialog = new BookingForm();
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setVisible(true);
    }
}
