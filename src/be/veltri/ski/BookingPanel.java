package be.veltri.ski;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import be.veltri.connection.SkiConnection;
import be.veltri.pojo.Booking;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
        
        tableBooking.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = tableBooking.getSelectedRow();
                if (selectedRow != -1) {
                    int bookingId = (int) tableBooking.getValueAt(selectedRow, 0);
                    Booking.find(bookingId, conn);
                }
            }
        });
        

    }

    private void deleteBooking() {
        int selectedRow = tableBooking.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a booking to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int bookingId = (int) tableBooking.getValueAt(selectedRow, 0);
        int response = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this booking?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);

        if (response == JOptionPane.YES_OPTION) {
            Booking booking = Booking.find(bookingId, conn);
            if (booking != null && booking.delete(conn)) {
                ((DefaultTableModel) tableBooking.getModel()).removeRow(selectedRow);
                JOptionPane.showMessageDialog(this, "Booking deleted successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete booking.");
            }
        }
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
