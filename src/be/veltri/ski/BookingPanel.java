package be.veltri.ski;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import be.veltri.connection.SkiConnection;
import be.veltri.pojo.Booking;

import java.awt.*;
import java.sql.Connection;
import java.util.List;


public class BookingPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private JTable tableBooking;
    private DefaultTableModel model;


    private Connection conn = SkiConnection.getInstance();

    public BookingPanel() {
        setLayout(new BorderLayout());
        initializeComponents();
        loadBookingsFromDB();
    }

    private void initializeComponents() {
    	model = new DefaultTableModel(
                new Object[][] {},
                new String[]{"Booking ID", "Skier", "Start Date", "Lesson", "Instructor", "Price", "Insurance"}
            );

        tableBooking = new JTable(model) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JScrollPane scrollPaneBooking = new JScrollPane(tableBooking);
        add(scrollPaneBooking, BorderLayout.CENTER);
            
        JPanel panelBookingButtons = new JPanel();
        JButton btnAddBooking = new JButton("Add Booking");
        JButton btnDeleteBooking = new JButton("Delete Booking");

        btnAddBooking.addActionListener(e -> {
            System.out.println("Add Booking button clicked");
            new BookingForm();
        });

        btnDeleteBooking.addActionListener(e -> deleteBooking());

        panelBookingButtons.add(btnAddBooking);
        panelBookingButtons.add(btnDeleteBooking);

        add(panelBookingButtons, BorderLayout.SOUTH);
        

    }

    private void deleteBooking() {
        
    }

    private void loadBookingsFromDB() {
        List<Booking> bookings = Booking.findAll(conn);
        DefaultTableModel model = (DefaultTableModel) tableBooking.getModel();
        model.setRowCount(0);

        for (Booking booking : bookings) {
            model.addRow(new Object[]{
                booking.getId(),
                booking.getSkier().getFirstName() + " " + booking.getSkier().getLastName(),
                booking.getLesson().getLessonDate(),
                booking.getLesson().getLessonType().getAccreditation().getName() + " " + booking.getLesson().getLessonType().getLevel(),
                booking.getInstructor().getFirstName() + " " + booking.getInstructor().getLastName(),
                booking.calculatePrice(),
                booking.getInsuranceOpt() ? "Yes" : "No"
            });
        }
    }
}
