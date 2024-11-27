package be.veltri.ski;

import javax.swing.*;
import be.veltri.connection.SkiConnection;
import java.awt.*;

import java.sql.Connection;


public class BookingPanel extends JPanel {

    private static final long serialVersionUID = 1L;


    private Connection conn = SkiConnection.getInstance();

    public BookingPanel() {
        setLayout(new BorderLayout());
        initializeComponents();
        loadBookingsFromDB();
    }

    private void initializeComponents() {
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
        
    }
}
