package com.client.view;

import com.server.model.Student;
import com.server.model.Program;
import com.server.model.Department;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.List;
import java.text.SimpleDateFormat;
import java.text.ParseException;

/**
 * Modernized Student Management View.
 * Optimized for complex data entry and table scannability.
 */
public class StudentView extends JFrame {

    private static final long serialVersionUID = 1L;
    private final JFrame parentFrame;

    // --- Modern Teal Palette ---
    private static final Color TEAL_PRIMARY     = new Color(38, 166, 154);
    private static final Color TEAL_DARK        = new Color(0, 105, 92);
    private static final Color BG_LIGHT         = new Color(245, 247, 248);
    private static final Color DELETE_RED       = new Color(211, 47, 47);
    private static final Color TEXT_DARK        = new Color(33, 33, 33);
    
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 22);
    private static final Font LABEL_FONT = new Font("Segoe UI Semibold", Font.PLAIN, 12);
    private static final Font MAIN_FONT  = new Font("Segoe UI", Font.PLAIN, 13);

    // --- Components ---
    private JTable studentTable;
    private DefaultTableModel tableModel;
    private JTextField idField, uniqueIdField, firstNameField, lastNameField, emailField, dateOfBirthField, enrollmentDateField;
    private JComboBox<String> studyModeComboBox; 
    private JComboBox<Department> departmentComboBox;
    private JComboBox<Program> programComboBox;

    private JButton registerButton, updateButton, deleteButton, clearButton, backButton;

    private final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private final String[] COLUMN_NAMES = {"ID", "Unique ID", "First Name", "Last Name", "Email", "Mode", "DoB", "Enrollment", "Program", "Dept"};

    public StudentView(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        setupFrame();
        initComponents();
        layoutComponents();
        setupTableSelectionListener();
    }

    private void setupFrame() {
        setTitle("Student Information System | Management");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1280, 800);
        getContentPane().setBackground(BG_LIGHT);
        setLayout(new BorderLayout());
        setLocationRelativeTo(parentFrame);
    }

    private void initComponents() {
        // ID: Read Only
        idField = createStyledTextField(false);
        
        // Input Fields with Placeholder hints
        uniqueIdField = createStyledTextField(true);
        setPlaceholder(uniqueIdField, "e.g., STD-2025-001");
        
        firstNameField = createStyledTextField(true);
        lastNameField = createStyledTextField(true);
        
        emailField = createStyledTextField(true);
        setPlaceholder(emailField, "example@university.ac");

        dateOfBirthField = createStyledTextField(true);
        setPlaceholder(dateOfBirthField, "yyyy-MM-dd");
        
        enrollmentDateField = createStyledTextField(true);
        setPlaceholder(enrollmentDateField, "yyyy-MM-dd");
        
        studyModeComboBox = new JComboBox<>(new String[]{"Full-time", "Part-time"});
        departmentComboBox = new JComboBox<>();
        programComboBox = new JComboBox<>();
        styleComboBox(studyModeComboBox);
        styleComboBox(departmentComboBox);
        styleComboBox(programComboBox);

        // Buttons
        registerButton = createStyledButton("Register Student", TEAL_PRIMARY);
        updateButton   = createStyledButton("Update Record", new Color(0, 150, 136));
        deleteButton   = createStyledButton("Delete Record", DELETE_RED);
        clearButton    = createStyledButton("Clear", new Color(158, 158, 158));
        backButton     = createStyledButton("← Dashboard", new Color(69, 90, 100));

        // Table Setup
        tableModel = new DefaultTableModel(COLUMN_NAMES, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        studentTable = new JTable(tableModel);
        styleTable(studentTable);
    }

    private void layoutComponents() {
        // 1. TOP HEADER
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setPreferredSize(new Dimension(1280, 70));
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 3, 0, TEAL_PRIMARY));

        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        navPanel.setOpaque(false);
        navPanel.add(backButton);
        
        JLabel titleLabel = new JLabel("Student Records Management", SwingConstants.CENTER);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(TEXT_DARK);

        headerPanel.add(navPanel, BorderLayout.WEST);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(Box.createRigidArea(new Dimension(200, 0)), BorderLayout.EAST);

        // 2. CENTER TABLE
        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setOpaque(false);
        tableContainer.setBorder(new EmptyBorder(20, 25, 10, 25));

        JScrollPane scrollPane = new JScrollPane(studentTable);
        scrollPane.getViewport().setBackground(Color.WHITE);
        tableContainer.add(scrollPane, BorderLayout.CENTER);

        // 3. BOTTOM FORM CARD
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setOpaque(false);
        footerPanel.setBorder(new EmptyBorder(10, 25, 25, 25));

        JPanel formCard = new JPanel(new GridBagLayout());
        formCard.setBackground(Color.WHITE);
        formCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            new EmptyBorder(15, 20, 15, 20)
        ));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 10, 6, 10);
        g.fill = GridBagConstraints.HORIZONTAL;

        // Grid Layout: 3 Rows of 2 pairs (label + field)
        addFormField(formCard, "System ID:", idField, g, 0, 0, 0.1);
        addFormField(formCard, "Unique Reg ID:", uniqueIdField, g, 0, 2, 0.2);
        addFormField(formCard, "First Name:", firstNameField, g, 0, 4, 0.2);

        addFormField(formCard, "Last Name:", lastNameField, g, 1, 0, 0.1);
        addFormField(formCard, "Email Address:", emailField, g, 1, 2, 0.2);
        addFormField(formCard, "Study Mode:", studyModeComboBox, g, 1, 4, 0.2);

        addFormField(formCard, "Date of Birth:", dateOfBirthField, g, 2, 0, 0.1);
        addFormField(formCard, "Enrollment Date:", enrollmentDateField, g, 2, 2, 0.2);
        addFormField(formCard, "Study Program:", programComboBox, g, 2, 4, 0.2);

        g.gridy = 3; g.gridx = 0; g.weightx = 0;
        formCard.add(new JLabel("Department:"), g);
        g.gridx = 1; g.gridwidth = 1; g.weightx = 0.1;
        formCard.add(departmentComboBox, g);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(registerButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);

        g.gridy = 4; g.gridx = 0; g.gridwidth = 6;
        g.insets = new Insets(20, 0, 5, 0);
        formCard.add(buttonPanel, g);

        footerPanel.add(formCard, BorderLayout.CENTER);

        add(headerPanel, BorderLayout.NORTH);
        add(tableContainer, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);
    }

    // --- Helpers ---

    private void addFormField(JPanel panel, String label, JComponent comp, GridBagConstraints g, int y, int x, double weight) {
        g.gridy = y; g.gridx = x; g.gridwidth = 1; g.weightx = 0;
        JLabel lbl = new JLabel(label);
        lbl.setFont(LABEL_FONT);
        panel.add(lbl, g);
        g.gridx = x + 1; g.weightx = weight;
        panel.add(comp, g);
    }

    private void styleTable(JTable table) {
        table.setRowHeight(35);
        table.setFont(MAIN_FONT);
        table.setSelectionBackground(new Color(224, 242, 241));
        table.setShowVerticalLines(false);
        
        JTableHeader h = table.getTableHeader();
        h.setBackground(TEAL_DARK);
        h.setForeground(Color.WHITE);
        h.setFont(new Font("Segoe UI", Font.BOLD, 13));
        h.setPreferredSize(new Dimension(0, 40));
    }

    private JTextField createStyledTextField(boolean editable) {
        JTextField f = new JTextField();
        f.setEditable(editable);
        f.setFont(MAIN_FONT);
        f.setBackground(editable ? Color.WHITE : new Color(248, 248, 248));
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(210, 210, 210)),
            new EmptyBorder(6, 10, 6, 10)
        ));
        return f;
    }

    private void styleComboBox(JComboBox<?> cb) {
        cb.setFont(MAIN_FONT);
        cb.setBackground(Color.WHITE);
        cb.setPreferredSize(new Dimension(0, 35));
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setBorder(new EmptyBorder(10, 18, 10, 18));
        return b;
    }

    private void setPlaceholder(JTextField field, String hint) {
        field.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                if (field.getText().equals(hint)) {
                    field.setText("");
                    field.setForeground(TEXT_DARK);
                }
            }
            @Override public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setForeground(Color.GRAY);
                    field.setText(hint);
                }
            }
        });
        field.setText(hint);
        field.setForeground(Color.GRAY);
    }

    // --- Logic & Events ---

    private void setupTableSelectionListener() {
        studentTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && studentTable.getSelectedRow() != -1) {
                displaySelectedStudent();
            }
        });
    }

    private void displaySelectedStudent() {
        int row = studentTable.getSelectedRow();
        if (row >= 0) {
            idField.setText(tableModel.getValueAt(row, 0).toString());
            uniqueIdField.setText(tableModel.getValueAt(row, 1).toString());
            uniqueIdField.setForeground(TEXT_DARK);
            firstNameField.setText(tableModel.getValueAt(row, 2).toString());
            lastNameField.setText(tableModel.getValueAt(row, 3).toString());
            emailField.setText(tableModel.getValueAt(row, 4).toString());
            emailField.setForeground(TEXT_DARK);
            studyModeComboBox.setSelectedItem(tableModel.getValueAt(row, 5).toString());
            dateOfBirthField.setText(tableModel.getValueAt(row, 6).toString());
            dateOfBirthField.setForeground(TEXT_DARK);
            enrollmentDateField.setText(tableModel.getValueAt(row, 7).toString());
            enrollmentDateField.setForeground(TEXT_DARK);
            
            selectComboBoxItemByName(programComboBox, tableModel.getValueAt(row, 8).toString());
            selectComboBoxItemByName(departmentComboBox, tableModel.getValueAt(row, 9).toString());
        }
    }

    private <T> void selectComboBoxItemByName(JComboBox<T> comboBox, String name) {
        for (int i = 0; i < comboBox.getItemCount(); i++) {
            T item = comboBox.getItemAt(i);
            if (item != null && item.toString().equals(name)) {
                comboBox.setSelectedItem(item);
                return;
            }
        }
    }

    public Student getStudentDataFromInputs() {
        Student student = new Student();
        try {
            if (!idField.getText().isEmpty()) student.setId(Long.parseLong(idField.getText()));
            
            student.setStudentUniqueId(uniqueIdField.getText().trim());
            student.setFirstName(firstNameField.getText().trim());
            student.setLastName(lastNameField.getText().trim());
            student.setEmail(emailField.getText().trim());
            student.setStudyMode((String) studyModeComboBox.getSelectedItem());

            String dobTxt = dateOfBirthField.getText().trim();
            if (!dobTxt.isEmpty() && !dobTxt.equals("yyyy-MM-dd")) student.setDateOfBirth(DATE_FORMAT.parse(dobTxt));
            
            String enTxt = enrollmentDateField.getText().trim();
            if (!enTxt.isEmpty() && !enTxt.equals("yyyy-MM-dd")) student.setEnrollmentDate(DATE_FORMAT.parse(enTxt));

            student.setDepartment((Department) departmentComboBox.getSelectedItem());
            student.setProgram((Program) programComboBox.getSelectedItem());
            
            return student;
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "Date format must be yyyy-MM-dd", "Date Error", JOptionPane.ERROR_MESSAGE);
            return null;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Check inputs: " + e.getMessage(), "Input Error", JOptionPane.WARNING_MESSAGE);
            return null;
        }
    }

    public void refreshStudentTable(List<Student> students) {
        tableModel.setRowCount(0);
        if (students != null) {
            for (Student s : students) {
                String dob = s.getDateOfBirth() != null ? DATE_FORMAT.format(s.getDateOfBirth()) : "N/A";
                String enrollment = s.getEnrollmentDate() != null ? DATE_FORMAT.format(s.getEnrollmentDate()) : "N/A";
                tableModel.addRow(new Object[]{
                    s.getId(), s.getStudentUniqueId(), s.getFirstName(), s.getLastName(), s.getEmail(),
                    s.getStudyMode(), dob, enrollment,
                    s.getProgram() != null ? s.getProgram().getName() : "N/A",
                    s.getDepartment() != null ? s.getDepartment().getName() : "N/A"
                });
            }
        }
    }

    public void clearFields() {
        idField.setText("");
        setPlaceholder(uniqueIdField, "e.g., STD-2025-001");
        firstNameField.setText("");
        lastNameField.setText("");
        setPlaceholder(emailField, "example@university.ac");
        setPlaceholder(dateOfBirthField, "yyyy-MM-dd");
        setPlaceholder(enrollmentDateField, "yyyy-MM-dd");
        studyModeComboBox.setSelectedIndex(0);
        departmentComboBox.setSelectedIndex(-1);
        programComboBox.setSelectedIndex(-1);
        studentTable.clearSelection();
    }

    public void populateDepartmentComboBox(List<Department> d) {
        departmentComboBox.removeAllItems();
        if (d != null) for (Department dept : d) departmentComboBox.addItem(dept);
    }
    
    public void populateProgramComboBox(List<Program> p) {
        programComboBox.removeAllItems();
        if (p != null) for (Program prog : p) programComboBox.addItem(prog);
    }

    // Accessors
    public JButton getRegisterButton() { return registerButton; }
    public JButton getUpdateButton() { return updateButton; }
    public JButton getDeleteButton() { return deleteButton; }
    public JButton getClearButton() { return clearButton; }
    public JButton getBackButton() { return backButton; }
    
    public long getSelectedStudentIdFromTable() {
        int row = studentTable.getSelectedRow();
        if (row != -1) return (long) studentTable.getValueAt(row, 0);
        return -1;
    }
}


    /*
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     *
    public static void main(String args[]) {
        /* Set the Nimbus look and feel *
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         *
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(StudentView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(StudentView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(StudentView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(StudentView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form *
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new StudentView().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}*/
