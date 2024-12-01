package be.veltri.ski;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import com.toedter.calendar.JDateChooser;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.*;
import java.util.List;
import be.veltri.connection.*;
import be.veltri.dao.SkierDAO;
import be.veltri.pojo.*;

public class SkierPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private JTextField tfFirstname, tfLastname, tfFindLastName, tfPhoneNumber, tfEmail;
    private JLabel lblFirstname, lblLastname, lblPhoneNumber, lblEmail;
    private JDateChooser dateChooser;
    private JTable table;
    private DefaultTableModel tableModel;
    private Skier selectedSkier;

    private Connection conn = SkiConnection.getInstance();
    
    private SkierDAO skieurDAO = new SkierDAO(conn);

    public SkierPanel() {
        setLayout(null);
        initializeComponents();
        loadSkiersFromDB();
    }

    private void initializeComponents() {
        JPanel panelRegistration = createRegistrationPanel();
        add(panelRegistration);
        
        JPanel panelSearch = createSearchPanel();
        add(panelSearch);
        
        tableModel = new DefaultTableModel(
                new Object[][] {},
                new String[] { "Id", "LastName", "FirstName", "Birthdate", "PhoneNumber", "Email" }
            );

            table = new JTable(tableModel) {
    			private static final long serialVersionUID = 1L;

    			@Override
                public boolean isCellEditable(int row, int column) {
                    return false; 
                }
            };
            table.setFont(new Font("Tahoma", Font.PLAIN, 12));
            table.setBackground(new Color(255, 255, 255));
            table.setForeground(new Color(0, 0, 0));

            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setBounds(21, 71, 557, 227);
            panelSearch.add(scrollPane);

            table.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    int selectedRow = table.getSelectedRow();
                    if (selectedRow != -1) {
                        int skierId = (int) tableModel.getValueAt(selectedRow, 0);
                        selectedSkier = Skier.find(skierId, skieurDAO); 
                        if (selectedSkier != null) {
                            tfFirstname.setText(selectedSkier.getFirstName());
                            tfLastname.setText(selectedSkier.getLastName());
                            dateChooser.setDate(Date.valueOf(selectedSkier.getBirthdate()));
                            tfPhoneNumber.setText(selectedSkier.getPhoneNumber());
                            tfEmail.setText(selectedSkier.getEmail());
                            
                        }
                    }
                }
            });
    }

    private JPanel createRegistrationPanel() {
        JPanel panelRegistration = new JPanel();
        panelRegistration.setBounds(35, 37, 285, 300);
        panelRegistration.setBorder(new TitledBorder(new MatteBorder(1, 1, 1, 1, (Color) new Color(0, 0, 0)), "Registration", TitledBorder.CENTER, TitledBorder.TOP, null, new Color(0, 0, 0)));
        panelRegistration.setLayout(null);

        lblFirstname = new JLabel("FirstName : ");
        lblFirstname.setBounds(25, 23, 106, 33);
        panelRegistration.add(lblFirstname);
        tfFirstname = new JTextField();
        tfFirstname.setBounds(141, 30, 96, 19);
        panelRegistration.add(tfFirstname);

        lblLastname = new JLabel("LastName : ");
        lblLastname.setBounds(25, 66, 106, 33);
        panelRegistration.add(lblLastname);
        tfLastname = new JTextField();
        tfLastname.setBounds(141, 73, 96, 19);
        panelRegistration.add(tfLastname);

        JButton btnAdd = new JButton("ADD");
        btnAdd.setBounds(46, 230, 85, 21);
        panelRegistration.add(btnAdd);
        btnAdd.addActionListener(e -> addSkier());

        JButton btnClear = new JButton("CLEAR");
        btnClear.setBounds(46, 261, 85, 21);
        panelRegistration.add(btnClear);
        btnClear.addActionListener(e -> clearFields());

        JLabel lblBirthday = new JLabel("Birthdate : ");
        lblBirthday.setBounds(25, 109, 106, 33);
        panelRegistration.add(lblBirthday);

        JButton btnUpdate = new JButton("UPDATE");
        btnUpdate.setBounds(152, 230, 85, 21);
        panelRegistration.add(btnUpdate);
        btnUpdate.addActionListener(e -> updateSkier());

        JButton btnDelete = new JButton("DELETE");
        btnDelete.setBounds(152, 261, 85, 21);
        panelRegistration.add(btnDelete);
        
        dateChooser = new JDateChooser();
        dateChooser.setBounds(141, 123, 96, 19);
        panelRegistration.add(dateChooser);
        
        lblPhoneNumber= new JLabel("Phone Number : ");
        lblPhoneNumber.setBounds(25, 152, 106, 33);
        panelRegistration.add(lblPhoneNumber);
        
        lblEmail = new JLabel("Email : ");
        lblEmail.setBounds(25, 195, 106, 33);
        panelRegistration.add(lblEmail);
        
        tfPhoneNumber = new JTextField();
        tfPhoneNumber.setText("");
        tfPhoneNumber.setBounds(141, 159, 96, 19);
        panelRegistration.add(tfPhoneNumber);
        
        tfEmail = new JTextField();
        tfEmail.setText("");
        tfEmail.setBounds(141, 202, 96, 19);
        panelRegistration.add(tfEmail);
        btnDelete.addActionListener(e -> deleteSkier());

        return panelRegistration;
    }

    private JPanel createSearchPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, new Color(255, 255, 255), new Color(160, 160, 160)), "Search", TitledBorder.CENTER, TitledBorder.TOP, null, new Color(0, 0, 0)));
        panel.setBounds(330, 37, 577, 300);
        panel.setLayout(null);

        JLabel lblFirstname_1 = new JLabel("LastName : ");
        lblFirstname_1.setBounds(21, 28, 106, 33);
        panel.add(lblFirstname_1);

        tfFindLastName = new JTextField();
        tfFindLastName.setBounds(100, 35, 96, 19);
        panel.add(tfFindLastName);

        JButton btnFind = new JButton("FIND");
        btnFind.setBounds(225, 34, 85, 21);
        panel.add(btnFind);
        btnFind.addActionListener(e -> findSkier());

        return panel;
    }

    private void addSkier() {
        String firstName = tfFirstname.getText();
        
        String lastName = tfLastname.getText();
        String phoneNumber = tfPhoneNumber.getText();
        String email = tfEmail.getText();

        if (dateChooser.getDate() == null) {
            JOptionPane.showMessageDialog(SkierPanel.this, "Please enter a valid date.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

		LocalDate birthdate = dateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();


        if (!validateSkierFields(firstName, lastName, phoneNumber, email, birthdate)) {
            return;
        }

        int newId = Skier.getNextId(skieurDAO);
        
        Skier skier = new Skier(newId, firstName, lastName, birthdate, phoneNumber, email);

		if (!skier.create(skieurDAO)) {
			JOptionPane.showMessageDialog(SkierPanel.this, "Failed to add skier.");
		}
		else
		{
			if (skier.getId() != -1) {
	            JOptionPane.showMessageDialog(SkierPanel.this, "Skier added successfully!");
	            tableModel.addRow(new Object[]{
	                skier.getId(),
	                skier.getLastName(),
	                skier.getFirstName(),
	                skier.getBirthdate(),
	                skier.getPhoneNumber(),
	                skier.getEmail()
	            });
	        } else {
	            JOptionPane.showMessageDialog(SkierPanel.this, "Failed to add skier.");
	        }
		}
    }


    private void updateSkier() {
        try {
            if (selectedSkier != null) {
            	String firstName = tfFirstname.getText();
                String lastName = tfLastname.getText();
                String phoneNumber = tfPhoneNumber.getText();
                String email = tfEmail.getText();
                LocalDate birthdate = dateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(); 
                
                if (!validateSkierFields(firstName, lastName, phoneNumber, email, birthdate)) {
                    return;
                }

                selectedSkier.setFirstName(firstName);
                selectedSkier.setLastName(lastName);
                selectedSkier.setBirthdate(birthdate);
                selectedSkier.setPhoneNumber(phoneNumber);
                selectedSkier.setEmail(email);

                if (selectedSkier.update(skieurDAO)) {
                    JOptionPane.showMessageDialog(SkierPanel.this, "Skier updated successfully!");
                    loadSkiersFromDB();
                } else {
                    JOptionPane.showMessageDialog(SkierPanel.this, "Failed to update skier.");
                }
            } else {
                JOptionPane.showMessageDialog(SkierPanel.this, "No skier selected.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(SkierPanel.this, "Error during update: " + ex.getMessage());
        }
    }

    private void deleteSkier() {
        try {
            if (selectedSkier != null) {
                if (selectedSkier.delete(skieurDAO)) { 
                    JOptionPane.showMessageDialog(SkierPanel.this, "Skier deleted successfully!");
                    loadSkiersFromDB();
                } else {
                    JOptionPane.showMessageDialog(SkierPanel.this, "Failed to delete skier.");
                }
            } else {
                JOptionPane.showMessageDialog(SkierPanel.this, "No skier selected.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(SkierPanel.this, "Error during deletion: " + ex.getMessage());
        }
    }

    private void findSkier() {
        String lastname = tfFindLastName.getText();
        List<Skier> skiers = Skier.findByLastName(lastname, skieurDAO);
        tableModel.setRowCount(0);

        if (skiers.isEmpty()) {
            JOptionPane.showMessageDialog(SkierPanel.this, "No skier found.");
        } else {
            for (Skier skier : skiers) {
                tableModel.addRow(new Object[] {
                        skier.getId(),
                        skier.getLastName(),
                        skier.getFirstName(),
                        skier.getBirthdate(),
                        skier.getPhoneNumber(),
                        skier.getEmail()
                });
            }
        }
    }

    private void clearFields() {
        tfFirstname.setText("");
        tfLastname.setText("");
        dateChooser.setDate(null);
        tfPhoneNumber.setText("");
        tfEmail.setText("");

    }
    
    private boolean validateSkierFields(String firstName, String lastName, String phoneNumber, String email, LocalDate birthdate) {
    	
		if (firstName.isEmpty() || lastName.isEmpty() || phoneNumber.isEmpty() || email.isEmpty()|| birthdate == null) {
			JOptionPane.showMessageDialog(SkierPanel.this, "All fields are required.");
			return false;
		}
		
        String nameRegex = "^[A-Z][a-zA-Z]*$"; 
        String phoneRegex = "^\\+?[0-9]{10,15}$"; 
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"; 
        
        if (!firstName.matches(nameRegex)) {
            JOptionPane.showMessageDialog(SkierPanel.this, "First name should start with an uppercase letter and contain only letters.");
            return false;
        }

        if (!lastName.matches(nameRegex)) {
            JOptionPane.showMessageDialog(SkierPanel.this, "Last name should start with an uppercase letter and contain only letters.");
            return false;
        }

        if (!phoneNumber.matches(phoneRegex)) {
            JOptionPane.showMessageDialog(SkierPanel.this, "Phone number should be between 10 and 15 digits, with an optional '+' at the beginning.");
            return false;
        }

        if (!email.matches(emailRegex)) {
            JOptionPane.showMessageDialog(SkierPanel.this, "Email is not valid.");
            return false;
        }


        int age = java.time.Period.between(birthdate, LocalDate.now()).getYears();
		if (age < 4 || age > 99) {
			JOptionPane.showMessageDialog(SkierPanel.this, "Skier should be between 4 and 99 years old.");
			return false;
		}
      
        return true;  
    }

    private void loadSkiersFromDB() {
        List<Skier> skiers = Skier.findAll(skieurDAO);
        tableModel.setRowCount(0); 
        for (Skier skier : skiers) {
            tableModel.addRow(new Object[]{skier.getId(), skier.getLastName(), skier.getFirstName(), skier.getBirthdate(), skier.getPhoneNumber(), skier.getEmail()});
        }
    }
    

}
