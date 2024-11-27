package be.veltri.ski;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import be.veltri.connection.SkiConnection;
import be.veltri.pojo.Accreditation;
import be.veltri.pojo.Instructor;
import be.veltri.pojo.Lesson;
import be.veltri.pojo.LessonType;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import com.toedter.calendar.JDateChooser;

public class LessonPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private JTextField tfMinBooking, tfMaxBooking, tfSearchLessonName;
    private JLabel lblStartDate, lblLessonType, lblLessonInstructor, lblMinBooking, lblMaxBooking, lblCollective;
    private DefaultListModel<Instructor> instructorListModel;
    private JList<Instructor> listInstructors;
    private DefaultListModel<LessonType> lessonTypeListModel;
    private JList<LessonType> listLessonTypes;
    private JTable tableLesson;
    private JDateChooser dateChooser;
    private DefaultTableModel model;
    private JCheckBox chckbxNewCheckBox;
    
    private Connection conn = SkiConnection.getInstance();
    private JTextField tfNbHours;

    public LessonPanel() {
        setLayout(null);
        initializeComponents();
        loadLessonsFromDB();
        loadInstructorsFromDB();
        loadLessonTypesFromDB();
    }

    private void initializeComponents() {
        createRegistrationPanel();
        createSearchPanel();
    }

    private void createRegistrationPanel() {
        JPanel panelRegistration = new JPanel();
        panelRegistration.setBounds(35, 37, 293, 349);
        panelRegistration.setBorder(new TitledBorder(new MatteBorder(1, 1, 1, 1, Color.BLACK), "Lesson Registration", TitledBorder.CENTER, TitledBorder.ABOVE_TOP));
        add(panelRegistration);
        panelRegistration.setLayout(null);
        
        lblStartDate = new JLabel("Start Date: ");
        lblStartDate.setBounds(22, 30, 106, 25);
        panelRegistration.add(lblStartDate);
        
        dateChooser = new JDateChooser();
        dateChooser.setBounds(113, 36, 170, 19);
        panelRegistration.add(dateChooser);

        lblMinBooking = new JLabel("Min Booking : ");
        lblMinBooking.setBounds(22, 65, 106, 25);
        panelRegistration.add(lblMinBooking);
        tfMinBooking = new JTextField();
        tfMinBooking.setBounds(114, 68, 169, 19);
        panelRegistration.add(tfMinBooking);
        
        lblMaxBooking = new JLabel("Max Booking : ");
        lblMaxBooking.setBounds(22, 100, 106, 25);
        panelRegistration.add(lblMaxBooking);
        tfMaxBooking = new JTextField();
        tfMaxBooking.setBounds(114, 103, 169, 19);
        panelRegistration.add(tfMaxBooking);

        lblLessonType = new JLabel("Lesson Type : ");
        lblLessonType.setBounds(22, 192, 106, 25);
        panelRegistration.add(lblLessonType);

        lessonTypeListModel = new DefaultListModel<>();

        JScrollPane scrollLessonTypes = new JScrollPane();
        scrollLessonTypes.setBounds(114, 196, 169, 50);
        scrollLessonTypes.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollLessonTypes.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        panelRegistration.add(scrollLessonTypes); 
        listLessonTypes = new JList<>(lessonTypeListModel);
        scrollLessonTypes.setViewportView(listLessonTypes);
        
        lblLessonInstructor = new JLabel("Instructor : ");
        lblLessonInstructor.setBounds(22, 135, 106, 25);
        panelRegistration.add(lblLessonInstructor);
        
        instructorListModel = new DefaultListModel<>();
        
        JScrollPane scrollInstructors = new JScrollPane();
        scrollInstructors.setBounds(114, 136, 169, 50);
        scrollInstructors.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollInstructors.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        panelRegistration.add(scrollInstructors); 
        listInstructors = new JList<>(instructorListModel);
        scrollInstructors.setViewportView(listInstructors);
        
        lblCollective = new JLabel("Collective : ");
        lblCollective.setBounds(22, 252, 79, 25);
        panelRegistration.add(lblCollective);
        
        chckbxNewCheckBox = new JCheckBox("");
        chckbxNewCheckBox.setBounds(110, 252, 26, 21);
        panelRegistration.add(chckbxNewCheckBox);
        
        tfNbHours = new JTextField();
        tfNbHours.setText("Number of hours");
        tfNbHours.setBounds(147, 256, 136, 19);
        tfNbHours.setColumns(10);
        panelRegistration.add(tfNbHours);
        
        chckbxNewCheckBox.addItemListener(e -> {
            boolean isChecked = chckbxNewCheckBox.isSelected();
            tfNbHours.setVisible(!isChecked);
        });

        tfNbHours.setVisible(!chckbxNewCheckBox.isSelected());



        addButtonActions(panelRegistration);            
    }


    private void createSearchPanel() {
        JPanel panelSearch = new JPanel();
        panelSearch.setBounds(330, 37, 577, 349);
        panelSearch.setBorder(new TitledBorder(new MatteBorder(1, 1, 1, 1, Color.BLACK), "Search Lesson", TitledBorder.CENTER, TitledBorder.ABOVE_TOP));
        add(panelSearch);
        panelSearch.setLayout(null);

        JLabel lblSearchLessonId = new JLabel("Lesson id : ");
        lblSearchLessonId.setBounds(21, 28, 106, 25);
        panelSearch.add(lblSearchLessonId);

        tfSearchLessonName = new JTextField();
        tfSearchLessonName.setBounds(130, 28, 96, 19);
        panelSearch.add(tfSearchLessonName);

        JButton btnFind = new JButton("FIND");
        btnFind.setBounds(245, 28, 85, 21);
        panelSearch.add(btnFind);
        
        model = new DefaultTableModel(
                new Object[][] {},
                new String[] { "Lesson id", "Start date", "Min Booking", "Max Booking", "Instructor", "Lesson Type", "IsCollective", "Nb Hours"}
            );

            tableLesson = new JTable(model) {
    			private static final long serialVersionUID = 1L;

    			@Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            tableLesson.setFont(new Font("Tahoma", Font.PLAIN, 12));
            tableLesson.setBackground(Color.WHITE);
            tableLesson.setForeground(Color.BLACK);

            JScrollPane scrollPane = new JScrollPane(tableLesson);
            scrollPane.setBounds(10, 63, 557, 276);
            panelSearch.add(scrollPane);

    	
    		tableLesson.addMouseListener(new MouseAdapter() {
    			public void mouseClicked(MouseEvent e) {
    				int selectedRow = tableLesson.getSelectedRow();
    				if (selectedRow != -1) {
    					int lessonId = (int) tableLesson.getValueAt(selectedRow, 0);
    					Lesson selectedLesson = Lesson.find(lessonId, conn);

    					if (selectedLesson != null) {
    						dateChooser.setDate(Date.valueOf(selectedLesson.getLessonDate()));
    						tfMinBooking.setText(String.valueOf(selectedLesson.getMinBookings()));
    						tfMaxBooking.setText(String.valueOf(selectedLesson.getMaxBookings()));

    						listInstructors.setSelectedValue(selectedLesson.getInstructor(), true);
    						
    						listLessonTypes.setSelectedValue(selectedLesson.getLessonType(), true);			

    						chckbxNewCheckBox.setSelected(selectedLesson.getIsCollective());

    						if (selectedLesson.getIsCollective()) {
    							tfNbHours.setVisible(false);
    						} else {
    							tfNbHours.setVisible(true);
    							tfNbHours.setText(String.valueOf(selectedLesson.getNb_hours()));
    						}
    					}
    				}
    			}
    		});



            btnFind.addActionListener(e -> findLesson());
    }


    private void addButtonActions(JPanel panelRegistration) {
        JButton btnSave = new JButton("ADD");
        btnSave.setBounds(58, 287, 85, 21);
        btnSave.addActionListener(e -> addLesson());
        panelRegistration.add(btnSave);

        JButton btnClear = new JButton("CLEAR");
        btnClear.setBounds(154, 287, 85, 21);
        btnClear.addActionListener(e -> clearFields());
        panelRegistration.add(btnClear);

        JButton btnUpdate = new JButton("UPDATE");
        btnUpdate.setBounds(58, 318, 85, 21);
        btnUpdate.addActionListener(e -> updateLesson());
        panelRegistration.add(btnUpdate);

        JButton btnDelete = new JButton("DELETE");
        btnDelete.setBounds(154, 318, 85, 21);
        btnDelete.addActionListener(e -> deleteLesson());
        panelRegistration.add(btnDelete);
    }

    private void addLesson() {
        LocalDate lessonDate = dateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        int minBookings = Integer.parseInt(tfMinBooking.getText());
        int maxBookings = Integer.parseInt(tfMaxBooking.getText());

        Instructor selectedInstructor = listInstructors.getSelectedValue();
        LessonType selectedLessonType = listLessonTypes.getSelectedValue();

        boolean isCollective = chckbxNewCheckBox.isSelected();
        int nbHours = 168;  

        if (isCollective) {
            nbHours = 168;
        } else {
            try {
                nbHours = Integer.parseInt(tfNbHours.getText());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Please enter a valid number for hours.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        if (!validateLessonFields(lessonDate, minBookings, maxBookings, isCollective, nbHours, selectedInstructor, selectedLessonType)) {
            return; 
        }

        int newId = Lesson.getNextId(conn);
        Lesson lesson = new Lesson(newId, lessonDate, minBookings, maxBookings, nbHours, isCollective, selectedInstructor, selectedLessonType);

        if (lesson.create(conn)) {
            addLessonToTable(lesson);
            JOptionPane.showMessageDialog(this, "Lesson added successfully!");
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add lesson.");
        }
    }

    private void addLessonToTable(Lesson lesson) {
        DefaultTableModel model = (DefaultTableModel) tableLesson.getModel();
        model.addRow(new Object[] {
            lesson.getId(),
            lesson.getLessonDate(),
            lesson.getMinBookings(),
            lesson.getMaxBookings(),
            lesson.getInstructor().toString(),
            lesson.getLessonType().getLevel() + " à " + lesson.getLessonType().getPrice() + "€" ,
            lesson.getIsCollective(),
            lesson.getNb_hours()
        });
    }

    private void updateLesson() {
        int selectedRow = tableLesson.getSelectedRow();
        if (selectedRow != -1) {
            int lessonId = (int) tableLesson.getValueAt(selectedRow, 0);
            Lesson lesson = new Lesson();
            lesson.setId(lessonId);

            lesson.setLessonDate(dateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            lesson.setMinBookings(Integer.parseInt(tfMinBooking.getText()));
            lesson.setMaxBookings(Integer.parseInt(tfMaxBooking.getText()));

            Instructor selectedInstructor = listInstructors.getSelectedValue();
            LessonType selectedLessonType = listLessonTypes.getSelectedValue();

            boolean isCollective = chckbxNewCheckBox.isSelected();
            lesson.setIsCollective(isCollective);

            if (isCollective) {
                lesson.setNb_hours(168);
            } else {
                try {
                    int nbHours = Integer.parseInt(tfNbHours.getText());
                    if (nbHours <= 0) {
                        JOptionPane.showMessageDialog(this, "Please enter a valid number of hours greater than 0.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    lesson.setNb_hours(nbHours);
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Please enter a valid number for hours.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            if (!validateLessonFields(lesson.getLessonDate(), lesson.getMinBookings(), lesson.getMaxBookings(), isCollective, lesson.getNb_hours(), selectedInstructor, selectedLessonType)) {
                return;  
            }

            lesson.setInstructor(selectedInstructor);
            lesson.setLessonType(selectedLessonType);

            boolean updated = lesson.update(conn);
            if (updated) {
                JOptionPane.showMessageDialog(this, "Lesson updated successfully!");
                loadLessonsFromDB();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update lesson.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a lesson to update.");
        }
    }



    private void deleteLesson() {
    }
    
    private void findLesson() {
        String searchText = tfSearchLessonName.getText().trim();
        DefaultTableModel model = (DefaultTableModel) tableLesson.getModel();
        model.setRowCount(0); 

        if (searchText.isEmpty()) loadLessonsFromDB();
        else {
            try {
                int searchId = Integer.parseInt(searchText); 
                Lesson lesson = Lesson.find(searchId, conn); 
                
                if (lesson != null) {
                    model.addRow(new Object[] {
                        lesson.getId(),
                        lesson.getLessonDate(),
                        lesson.getMinBookings(),
                        lesson.getMaxBookings(),
                        lesson.getInstructor().toString(),
                        lesson.getLessonType().getLevel() + " à " + lesson.getLessonType().getPrice() + "€",
                        lesson.getIsCollective(),
                        lesson.getNb_hours()
                    });
                } else {
                    JOptionPane.showMessageDialog(this, "Leçon non trouvée.", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Veuillez entrer un ID valide.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearFields() {
        tfMinBooking.setText("");
        tfMaxBooking.setText("");
        listLessonTypes.clearSelection();
        listInstructors.clearSelection();
        chckbxNewCheckBox.setSelected(false);
        tfNbHours.setText("");
    }
    
    private boolean validateLessonFields(LocalDate lessonDate, int minBookings, int maxBookings, boolean isCollective,
            int nbHours, Instructor selectedInstructor, LessonType selectedLessonType) {

        LocalDate startDate = LocalDate.of(2024, 12, 6);
        LocalDate endDate = LocalDate.of(2025, 5, 3);

        if (lessonDate.isBefore(startDate) || lessonDate.isAfter(endDate)) {
            JOptionPane.showMessageDialog(this, "The domain is only open between Saturday 06/12/2024 and Sunday 03/05/2025.");
            return false;
        }

        if (selectedInstructor == null || selectedLessonType == null) {
            JOptionPane.showMessageDialog(this, "Please select an instructor and lesson type.");
            return false;
        }

        if (!selectedInstructor.isAvailable(lessonDate)) {
            System.out.println("Instructor is not available");
            JOptionPane.showMessageDialog(this, "The selected instructor is already booked on this date.");
            return false;
        }

        if (isCollective) {
            String accreditationName = selectedLessonType.getAccreditation().getName().toLowerCase();
            String lessonType = selectedLessonType.getLevel().toLowerCase();

            if ((accreditationName.contains("enfant") || accreditationName.contains("snowboard")) &&
                    (minBookings != 5 || maxBookings != 8)) {
                JOptionPane.showMessageDialog(this, "For these collective lessons, the number of participants must be between 5 and 8.");
                return false;
            }

            if ((lessonType.contains("compétition") || lessonType.contains("hors-piste")) &&
                    accreditationName.contains("adulte") &&
                    (minBookings != 5 || maxBookings != 8)) {
                JOptionPane.showMessageDialog(this, "For these adult collective lessons (competition or off-piste), the number of participants must be between 5 and 8.");
                return false;
            }

            if (accreditationName.contains("adulte") &&
                    (minBookings != 6 || maxBookings != 10) &&
                    !(lessonType.contains("compétition") || lessonType.contains("hors-piste"))) {
                JOptionPane.showMessageDialog(this, "For adult collective lessons, the number of participants must be between 6 and 10.");
                return false;
            }

        } else {
            if (minBookings != 1 || maxBookings != 4) {
                JOptionPane.showMessageDialog(this, "For private lessons, the number of participants must be between 1 and 4.");
                return false;
            }
            if (nbHours != 1 && nbHours != 2) {
                JOptionPane.showMessageDialog(this, "Private lessons can only last 1 or 2 hours.");
                return false;
            }
        }

        Accreditation requiredAccreditation = selectedLessonType.getAccreditation();
        boolean hasAccreditation = selectedInstructor.getAccreditations().stream()
                .anyMatch(acc -> acc.getId() == requiredAccreditation.getId());

        if (!hasAccreditation) {
            JOptionPane.showMessageDialog(this, "The selected instructor does not have the required accreditation for this lesson type.");
            return false;
        }

        return true;
    }
    
    private void loadLessonsFromDB() {
        List<Lesson> lessons = Lesson.findAll(conn);
        DefaultTableModel model = (DefaultTableModel) tableLesson.getModel();
        model.setRowCount(0); 
        for (Lesson lesson : lessons) {
            model.addRow(new Object[] {
                lesson.getId(),
                lesson.getLessonDate(),
                lesson.getMinBookings(),
                lesson.getMaxBookings(),
                lesson.getInstructor().toString(),
                lesson.getLessonType().getLevel() + " à " + lesson.getLessonType().getPrice() + "€" ,
                lesson.getIsCollective(),
                lesson.getNb_hours()
            });
        }
    }
        
    private void loadInstructorsFromDB() {
        instructorListModel.clear();
        List<Instructor> instructors = Instructor.findAll(conn);
        
        for (Instructor instructor : instructors) {
            instructorListModel.addElement(instructor);
            
        }
    }

    private void loadLessonTypesFromDB() {
        lessonTypeListModel.clear();
        List<LessonType> lessonTypes = LessonType.findAll(conn);
        
        for (LessonType lessonType : lessonTypes) {
            lessonTypeListModel.addElement(lessonType);
        }
    }
}
