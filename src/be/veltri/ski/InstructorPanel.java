package be.veltri.ski;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import com.toedter.calendar.JDateChooser;

import be.veltri.connection.SkiConnection;
import be.veltri.pojo.Instructor;
import be.veltri.pojo.Accreditation;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

public class InstructorPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private JTextField tfInstructorFirstName, tfInstructorLastName, tfSearchInstructorLastName;
    private JLabel lblFirstName, lblLastName, lblBirthdate, lblHireDate, lblAccreditations;
    private JTable tableInstructor;
    private JScrollPane scrollAccreditations;
    private DefaultListModel<Accreditation> accreditationListModel;
    private JList<Accreditation> listAccreditations;
    private JDateChooser dateChooserBirthdate, dateChooserHireDate;
    private JPanel panelRegistration;
    private JButton btnManage;

    private Connection conn = SkiConnection.getInstance();

    public InstructorPanel() {
        setLayout(null);
        initializeComponents();
        loadAccreditationsFromDB();
    }

    private void initializeComponents() {
        createRegistrationPanel();
        createSearchPanel();
    }

    private void createRegistrationPanel() {
        panelRegistration = new JPanel();
        panelRegistration.setBorder(new TitledBorder(new MatteBorder(1, 1, 1, 1, Color.BLACK), "Instructor Registration", TitledBorder.CENTER, TitledBorder.ABOVE_TOP));
        panelRegistration.setBounds(35, 37, 285, 319);
        add(panelRegistration);
        panelRegistration.setLayout(null);

        lblFirstName = new JLabel("FirstName: ");
        lblFirstName.setBounds(25, 27, 106, 33);
        panelRegistration.add(lblFirstName);
        tfInstructorFirstName = new JTextField();
        tfInstructorFirstName.setBounds(141, 34, 96, 19);
        panelRegistration.add(tfInstructorFirstName);

        lblLastName = new JLabel("LastName: ");
        lblLastName.setBounds(25, 70, 106, 33);
        panelRegistration.add(lblLastName);
        tfInstructorLastName = new JTextField();
        tfInstructorLastName.setBounds(141, 77, 96, 19);
        panelRegistration.add(tfInstructorLastName);

        lblBirthdate = new JLabel("Birthdate: ");
        lblBirthdate.setBounds(25, 113, 106, 33);
        panelRegistration.add(lblBirthdate);
        
        dateChooserBirthdate = new JDateChooser();
        dateChooserBirthdate.setBounds(141, 123, 96, 19);
        panelRegistration.add(dateChooserBirthdate);
        
        lblHireDate = new JLabel("Hire date: ");
        lblHireDate.setBounds(25, 156, 106, 33);
        panelRegistration.add(lblHireDate);

        dateChooserHireDate = new JDateChooser();
        dateChooserHireDate.setBounds(141, 167, 96, 19);
        panelRegistration.add(dateChooserHireDate);
        
        lblAccreditations = new JLabel("Accreditation: ");
        lblAccreditations.setBounds(25, 194, 106, 33);
        panelRegistration.add(lblAccreditations);

        accreditationListModel = new DefaultListModel<>();
        listAccreditations = new JList<>(accreditationListModel); 

        scrollAccreditations = new JScrollPane(listAccreditations);
        scrollAccreditations.setBounds(141, 199, 96, 50); 
        scrollAccreditations.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollAccreditations.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        panelRegistration.add(scrollAccreditations); 

        addButtonActions(panelRegistration);
    }

    private void addButtonActions(JPanel panelRegistration) {
    	
        JButton btnSave = new JButton("ADD");
        btnSave.setBounds(45, 258, 85, 21);
        btnSave.addActionListener(e -> addInstructor());
        panelRegistration.add(btnSave);

        JButton btnClear = new JButton("CLEAR");
        btnClear.setBounds(151, 258, 85, 21);
        btnClear.addActionListener(e -> clearFields());
        panelRegistration.add(btnClear);

        JButton btnUpdate = new JButton("UPDATE");
        btnUpdate.setBounds(45, 289, 85, 21);
        btnUpdate.addActionListener(e -> updateInstructor());
        panelRegistration.add(btnUpdate);

        JButton btnDelete = new JButton("DELETE");
        btnDelete.setBounds(152, 289, 85, 21);
        btnDelete.addActionListener(e -> deleteInstructor());
        panelRegistration.add(btnDelete);
    }

    private void createSearchPanel() {
        JPanel panelSearch = new JPanel();
        panelSearch.setBorder(new TitledBorder(new MatteBorder(1, 1, 1, 1, Color.BLACK), "Search Instructor", TitledBorder.CENTER, TitledBorder.ABOVE_TOP));
        panelSearch.setBounds(330, 37, 577, 319);
        add(panelSearch);
        panelSearch.setLayout(null);

        JLabel lblSearchLastName = new JLabel("LastName: ");
        lblSearchLastName.setBounds(21, 28, 106, 33);
        panelSearch.add(lblSearchLastName);

        tfSearchInstructorLastName = new JTextField();
        tfSearchInstructorLastName.setBounds(100, 35, 96, 19);
        panelSearch.add(tfSearchInstructorLastName);

        JButton btnFind = new JButton("FIND");
        btnFind.addActionListener(e -> findInstructor());
        btnFind.setBounds(225, 34, 85, 21);
        panelSearch.add(btnFind);

        DefaultTableModel model = new DefaultTableModel(
            new Object[][] {},
            new String[] { "Id", "Name", "Birthdate", "Hire Date", "Accreditation(s)" }
        );

        tableInstructor = new JTable(model) {
			private static final long serialVersionUID = 1L;

			@Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };
        
        tableInstructor.setFont(new Font("Tahoma", Font.PLAIN, 12));
        tableInstructor.setBackground(Color.WHITE);
        tableInstructor.setForeground(Color.BLACK);

        JScrollPane scrollPane = new JScrollPane(tableInstructor);
        scrollPane.setBounds(10, 63, 557, 246);
        panelSearch.add(scrollPane);
        
    }

    private void clearFields() {
        tfInstructorFirstName.setText("");
        tfInstructorLastName.setText("");
        listAccreditations.clearSelection();
        dateChooserHireDate.setDate(null);
        dateChooserBirthdate.setDate(null);
        if (btnManage != null) {
            panelRegistration.remove(btnManage);
        }

        panelRegistration.revalidate();
        panelRegistration.repaint();
        
        listAccreditations.setVisible(true);
        scrollAccreditations.setVisible(true);
        
        panelRegistration.revalidate();
        panelRegistration.repaint();
    }

    private void addInstructor() {
        Instructor instructor = null;
        String firstName = tfInstructorFirstName.getText();
        String lastName = tfInstructorLastName.getText();
        LocalDate birthdate = dateChooserBirthdate.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate hireDate = dateChooserHireDate.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        List<Accreditation> selectedAccreditations = listAccreditations.getSelectedValuesList();
        

        if (selectedAccreditations == null || selectedAccreditations.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select at least one accreditation.");
            return;
        }
        
        if (!validateInstructorFields(firstName, lastName, birthdate, hireDate)) {
            return;  
        }

        int newId = Instructor.getNextId(conn);
        if (newId != -1) {
            for (Accreditation selectedAccreditation : selectedAccreditations) {
                instructor = new Instructor(newId, firstName, lastName, birthdate, hireDate, selectedAccreditation);
            }
            instructor.create(conn);
            addInstructorToTable(instructor);
            JOptionPane.showMessageDialog(this, "Instructor added successfully!");
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add instructor.");
        }
    }

    private void addInstructorToTable(Instructor instructor) {
        DefaultTableModel model = (DefaultTableModel) tableInstructor.getModel();
        
        StringBuilder formatAccreditations = new StringBuilder();
        
        for (Accreditation accreditation : instructor.getAccreditations()) {
            if (formatAccreditations.length() > 0) {
            	formatAccreditations.append(", ");  
            }
            formatAccreditations.append(accreditation.getName());
        }
        
        model.addRow(new Object[] {
            instructor.getId(),
            instructor.getFirstName() + " " + instructor.getLastName(),
            instructor.getBirthdate(),
            instructor.getHireDate(),
            formatAccreditations.toString()
        });
    }


    private boolean validateInstructorFields(String firstName, String lastName, LocalDate birthdate, LocalDate hireDate) {
        String nameRegex = "^[A-Z][a-zA-Z]*$"; 

        if (!firstName.matches(nameRegex)) {
            JOptionPane.showMessageDialog(this, "First name should start with an uppercase letter and contain only letters.");
            return false;
        }

        if (!lastName.matches(nameRegex)) {
            JOptionPane.showMessageDialog(this, "Last name should start with an uppercase letter and contain only letters.");
            return false;
        }

        int age = java.time.Period.between(birthdate, LocalDate.now()).getYears();
		if (age < 18 || age > 99) {
			JOptionPane.showMessageDialog(this, "Instructor should be between 18 and 99 years old.");
			return false;
		}

        if (hireDate.isBefore(birthdate)) {
            JOptionPane.showMessageDialog(this, "Hire date cannot be before birthdate.");
            return false;
        }

        return true;  
    }
    
    private void updateInstructor() {
    }
    
    private void deleteInstructor() {
    }
    
    private void findInstructor() {
    }
    
    private void loadAccreditationsFromDB() {
        accreditationListModel.clear();
        List<Accreditation> accreditations = Accreditation.findAll(conn);
        
        for (Accreditation accreditation : accreditations) {
            accreditationListModel.addElement(accreditation);
        }    
    }



}
