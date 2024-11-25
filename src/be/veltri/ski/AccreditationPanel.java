package be.veltri.ski;

import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import be.veltri.connection.SkiConnection;
import be.veltri.pojo.Accreditation;
import be.veltri.pojo.Instructor;

public class AccreditationPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private DefaultListModel<Accreditation> accreditationListModel;
    private JTable tableAccreditation;
    private Instructor currentInstructor;
    private Accreditation selectedAccreditation;

    private Connection conn = SkiConnection.getInstance();

    private JList<Accreditation> listAccreditations;

    public AccreditationPanel(int instructorId) {
    	currentInstructor = Instructor.find(instructorId, conn);
        setLayout(null);
        initializeComponents();
        loadAccreditationsFromDB();
        addAccreditationsToTable();  
    }

    private void initializeComponents() {
        setBorder(new TitledBorder(new MatteBorder(1, 1, 1, 1, Color.BLACK),
            "Instructor Accreditation", TitledBorder.CENTER, TitledBorder.ABOVE_TOP));
        setBounds(35, 37, 333, 454); 

        accreditationListModel = new DefaultListModel<>();

        JPanel panelRegistration = new JPanel();
        panelRegistration.setLayout(null);
        panelRegistration.setBorder(new TitledBorder(new MatteBorder(1, 1, 1, 1, Color.BLACK),
            "Add an accreditation", TitledBorder.CENTER, TitledBorder.ABOVE_TOP));
        panelRegistration.setBounds(21, 37, 299, 146);
        add(panelRegistration);

        JLabel lblAccreditations = new JLabel("Accreditation: ");
        lblAccreditations.setBounds(35, 31, 106, 33);
        panelRegistration.add(lblAccreditations);

        JScrollPane scrollAccreditations = new JScrollPane();
        scrollAccreditations.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollAccreditations.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollAccreditations.setBounds(151, 32, 126, 50);
        panelRegistration.add(scrollAccreditations);

        listAccreditations = new JList<>(accreditationListModel);
        scrollAccreditations.setViewportView(listAccreditations);

        JButton btnAdd = new JButton("ADD");
        btnAdd.setBounds(30, 101, 126, 30);
        panelRegistration.add(btnAdd);
        btnAdd.addActionListener(e -> addAccreditation());

        JButton btnRemove = new JButton("REMOVE");
        btnRemove.setBounds(166, 101, 129, 30);
        panelRegistration.add(btnRemove);
        btnRemove.addActionListener(e -> removeAccreditation());
        
        createSearchPanel();
    }

    private void createSearchPanel() {
        JPanel panelSearch = new JPanel();
        panelSearch.setBorder(new TitledBorder(new MatteBorder(1, 1, 1, 1, Color.BLACK), "All Accreditations", TitledBorder.CENTER, TitledBorder.ABOVE_TOP));
        panelSearch.setBounds(21, 199, 299, 248);
        add(panelSearch);

        DefaultTableModel model = new DefaultTableModel(
            new Object[][] {},
            new String[] { "Id", "Name" }
        );
        panelSearch.setLayout(null);

        tableAccreditation = new JTable(model) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableAccreditation.setFont(new Font("Tahoma", Font.PLAIN, 12));
        tableAccreditation.setBackground(Color.WHITE);
        tableAccreditation.setForeground(Color.BLACK);

        JScrollPane scrollPane = new JScrollPane(tableAccreditation);
        scrollPane.setBounds(10, 26, 275, 212);
        panelSearch.add(scrollPane);

        tableAccreditation.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int selectedRow = tableAccreditation.getSelectedRow();
                if (selectedRow != -1) {
                    int accreditationId = (int) tableAccreditation.getValueAt(selectedRow, 0);
                    selectedAccreditation = Accreditation.find(accreditationId, conn);

                    if (selectedAccreditation != null) {
                        listAccreditations.setSelectedValue(selectedAccreditation, true);
                    }
                }
            }
        });
    }

    private void addAccreditation() {
        Accreditation selectedAccreditation = listAccreditations.getSelectedValue();
        
        if (selectedAccreditation != null) {
            currentInstructor.addAccreditation(conn, selectedAccreditation);


                addAccreditationsToTable();
            
        } else {
            JOptionPane.showMessageDialog(this, "Select an accreditation to add.", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }


    private void removeAccreditation() {
        int selectedRow = tableAccreditation.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an accreditation to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Integer accreditationId = (Integer) tableAccreditation.getValueAt(selectedRow, 0);  

        if (accreditationId != null) {
            Accreditation selectedAccreditation = Accreditation.find(accreditationId, conn);  


                if (currentInstructor != null) {
                    int response = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this accreditation for the instructor?", 
                            "Confirm Deletion", JOptionPane.YES_NO_OPTION);
                    
                    if (response == JOptionPane.YES_OPTION) {
                        boolean deleted = currentInstructor.removeAccreditation(conn, selectedAccreditation);

                        if (deleted) {
                            ((DefaultTableModel) tableAccreditation.getModel()).removeRow(selectedRow);
                            JOptionPane.showMessageDialog(this, "Accreditation deleted successfully!");
                        } else {
                            JOptionPane.showMessageDialog(this, "Failed to delete accreditation.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    
                    }
                } 
             else {
                JOptionPane.showMessageDialog(this, "Accreditation not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Invalid accreditation ID.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadAccreditationsFromDB() {
        accreditationListModel.clear();
        List<Accreditation> accreditations = Accreditation.findAll(conn);

        for (Accreditation accreditation : accreditations) {
            accreditationListModel.addElement(accreditation);
        }
    }

    private void addAccreditationsToTable() {
        List<Accreditation> accreditations = Accreditation.findByInstructor(conn, currentInstructor);
        DefaultTableModel model = (DefaultTableModel) tableAccreditation.getModel();

        if (model != null) {
            model.setRowCount(0); 

            for (Accreditation accreditation : accreditations) {
                model.addRow(new Object[] {
                    accreditation.getId(), 
                    accreditation.getName()  
                });
            }
        }
    }

}
