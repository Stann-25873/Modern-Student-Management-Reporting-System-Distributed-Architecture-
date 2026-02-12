

package com.client.view;

import com.server.model.Enrollment;
import com.server.model.Student;
import com.server.model.Course;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.List;
import java.text.SimpleDateFormat;

public class EnrollmentView extends JFrame {

    private static final long serialVersionUID = 1L;
    private JTable enrollmentTable;
    private DefaultTableModel tableModel;

    private JTextField idField, gradeField, semesterField, enrollmentDateField;
    private JComboBox<Student> studentComboBox;
    private JComboBox<Course> courseComboBox;

    private JButton addEnrollmentButton, updateEnrollmentButton, deleteEnrollmentButton, clearButton, backButton;

    // Custom Colors
    private static final Color TEAL_PRIMARY = new Color(45, 126, 114);
    private static final Color TABLE_HEADER_COLOR = new Color(58, 122, 110);
    private static final Color BG_LIGHT = new Color(240, 245, 245);
    private static final Color DELETE_RED = new Color(200, 50, 50);

    private final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public EnrollmentView(JFrame parentFrame) {
        setTitle("Enrollment Management System");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1100, 750);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG_LIGHT);
        setLocationRelativeTo(parentFrame);

        initializeComponents();
        layoutComponents();
        setupTableSelectionListener();
    }

    private void initializeComponents() {
        // Styled Fields with Centralized Text and Prompt/Ghost Text
        idField = createStyledTextField("Auto-ID", false);
        gradeField = createStyledTextField("e.g. A or 85", true);
        semesterField = createStyledTextField("e.g. Semester 1", true);
        enrollmentDateField = createStyledTextField("YYYY-MM-DD", true);

        studentComboBox = new JComboBox<>();
        courseComboBox = new JComboBox<>();
        styleComboBox(studentComboBox);
        styleComboBox(courseComboBox);

        // Styled Buttons
        addEnrollmentButton = createStyledButton("Enroll Student", TEAL_PRIMARY);
        updateEnrollmentButton = createStyledButton("Update Details", TEAL_PRIMARY);
        deleteEnrollmentButton = createStyledButton("Delete Enrollment", DELETE_RED);
        clearButton = createStyledButton("Clear Fields", new Color(80, 100, 120));
        backButton = createStyledButton("Back to Dashboard", new Color(100, 120, 180));

        // Table Setup
        String[] columnNames = {"ID", "Student ID", "Student Name", "Course Code", "Course Title", "Grade", "Semester", "Enrollment Date"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        enrollmentTable = new JTable(tableModel);
        styleTable(enrollmentTable);
    }

    private void layoutComponents() {
        // --- 1. TOP HEADER ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setPreferredSize(new Dimension(1100, 70));

        JPanel backWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        backWrapper.setOpaque(false);
        backWrapper.add(backButton);
        headerPanel.add(backWrapper, BorderLayout.WEST);

        JLabel titleLabel = new JLabel("Enrollment & Registration Records", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(new Color(40, 40, 40));
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 3, 0, TEAL_PRIMARY));

        // --- 2. CENTER TABLE ---
        JScrollPane scrollPane = new JScrollPane(enrollmentTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        scrollPane.getViewport().setBackground(Color.WHITE);

        // --- 3. BOTTOM FORM (CENTRALIZED) ---
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(BG_LIGHT);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        JPanel formCard = new JPanel(new GridBagLayout());
        formCard.setBackground(Color.WHITE);
        formCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 210, 210), 1),
                BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 15, 8, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Form Alignment
        addLabelAndField(formCard, "Enrollment ID:", idField, 0, 0, gbc);
        addLabelAndField(formCard, "Target Student:", studentComboBox, 2, 0, gbc);
        addLabelAndField(formCard, "Target Course:", courseComboBox, 0, 1, gbc);
        addLabelAndField(formCard, "Grade Received:", gradeField, 2, 1, gbc);
        addLabelAndField(formCard, "Academic Semester:", semesterField, 0, 2, gbc);
        addLabelAndField(formCard, "Date (YYYY-MM-DD):", enrollmentDateField, 2, 2, gbc);

        // Action Buttons Row
        JPanel actionButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        actionButtons.setOpaque(false);
        actionButtons.add(addEnrollmentButton);
        actionButtons.add(updateEnrollmentButton);
        actionButtons.add(deleteEnrollmentButton);
        actionButtons.add(clearButton);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 4;
        formCard.add(actionButtons, gbc);

        footerPanel.add(formCard, BorderLayout.CENTER);

        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);
    }

    private void addLabelAndField(JPanel panel, String labelText, JComponent field, int x, int y, GridBagConstraints gbc) {
        gbc.gridx = x; gbc.gridy = y; gbc.gridwidth = 1;
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        panel.add(label, gbc);

        gbc.gridx = x + 1;
        panel.add(field, gbc);
    }

    // --- Styling & Centralization Helpers ---
    private void styleTable(JTable table) {
        table.setRowHeight(35);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setGridColor(new Color(240, 240, 240));

        // Centralize Table Data
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JTableHeader header = table.getTableHeader();
        header.setBackground(TABLE_HEADER_COLOR);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        ((DefaultTableCellRenderer)header.getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);
    }

    private JTextField createStyledTextField(String prompt, boolean editable) {
        JTextField field = new JTextField(prompt);
        field.setHorizontalAlignment(JTextField.CENTER); // Centralize Text
        field.setEditable(editable);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setForeground(Color.GRAY); // Prompt Color
        field.setPreferredSize(new Dimension(200, 35));
        field.setBackground(editable ? Color.WHITE : new Color(248, 248, 248));
        field.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        // Prompt Text Logic (Ghost Text)
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(prompt)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setForeground(Color.GRAY);
                    field.setText(prompt);
                }
            }
        });
        return field;
    }

    private void styleComboBox(JComboBox<?> box) {
        box.setPreferredSize(new Dimension(200, 35));
        box.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        box.setBackground(Color.WHITE);
        // Centralize text in ComboBox
        ((JLabel)box.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(12, 25, 12, 25));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // Logic implementation remains the same for data flow...
    private void setupTableSelectionListener() {
        enrollmentTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && enrollmentTable.getSelectedRow() != -1) {
                displaySelectedEnrollment();
            }
        });
    }

    private void displaySelectedEnrollment() {
        int row = enrollmentTable.getSelectedRow();
        if (row >= 0) {
            idField.setText(tableModel.getValueAt(row, 0).toString());
            idField.setForeground(Color.BLACK);

            gradeField.setText(tableModel.getValueAt(row, 5).toString());
            gradeField.setForeground(Color.BLACK);

            semesterField.setText(tableModel.getValueAt(row, 6).toString());
            semesterField.setForeground(Color.BLACK);

            enrollmentDateField.setText(tableModel.getValueAt(row, 7).toString());
            enrollmentDateField.setForeground(Color.BLACK);

            selectStudentComboBoxItem(tableModel.getValueAt(row, 2).toString());
            selectCourseComboBoxItem(tableModel.getValueAt(row, 3).toString());
        }
    }

    private void selectStudentComboBoxItem(String studentName) {
        for (int i = 0; i < studentComboBox.getItemCount(); i++) {
            Student student = studentComboBox.getItemAt(i);
            if (student != null && (student.getFirstName() + " " + student.getLastName()).equals(studentName)) {
                studentComboBox.setSelectedItem(student);
                return;
            }
        }
    }

    private void selectCourseComboBoxItem(String courseCode) {
        for (int i = 0; i < courseComboBox.getItemCount(); i++) {
            Course course = courseComboBox.getItemAt(i);
            if (course != null && course.getCode().equals(courseCode)) {
                courseComboBox.setSelectedItem(course);
                return;
            }
        }
    }

    public void clearFields() {
        idField.setText("Auto-ID"); idField.setForeground(Color.GRAY);
        gradeField.setText("e.g. A or 85"); gradeField.setForeground(Color.GRAY);
        semesterField.setText("e.g. Semester 1"); semesterField.setForeground(Color.GRAY);
        enrollmentDateField.setText("YYYY-MM-DD"); enrollmentDateField.setForeground(Color.GRAY);
        studentComboBox.setSelectedIndex(-1);
        courseComboBox.setSelectedIndex(-1);
        enrollmentTable.clearSelection();
    }

    public void refreshEnrollmentTable(List<Enrollment> enrollments) {
        tableModel.setRowCount(0);
        for (Enrollment e : enrollments) {
            tableModel.addRow(new Object[]{
                    e.getId(),
                    e.getStudent() != null ? e.getStudent().getId() : "N/A",
                    e.getStudent() != null ? e.getStudent().getFirstName() + " " + e.getStudent().getLastName() : "N/A",
                    e.getCourse() != null ? e.getCourse().getCode() : "N/A",
                    e.getCourse() != null ? e.getCourse().getTitle() : "N/A",
                    e.getGrade(),
                    e.getSemester(),
                    e.getEnrollmentDate() != null ? DATE_FORMAT.format(e.getEnrollmentDate()) : "N/A"
            });
        }
    }

    public void populateStudentComboBox(List<Student> students) {
        studentComboBox.removeAllItems();
        if (students != null) for (Student s : students) studentComboBox.addItem(s);
    }

    public void populateCourseComboBox(List<Course> courses) {
        courseComboBox.removeAllItems();
        if (courses != null) for (Course c : courses) courseComboBox.addItem(c);
    }

    // Getters for buttons...
    public JButton getAddEnrollmentButton() { return addEnrollmentButton; }
    public JButton getUpdateEnrollmentButton() { return updateEnrollmentButton; }
    public JButton getDeleteEnrollmentButton() { return deleteEnrollmentButton; }
    public JButton getClearButton() { return clearButton; }
    public JButton getBackButton() { return backButton; }
}
    /*
    @SuppressWarnings("unchecked")
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
            java.util.logging.Logger.getLogger(EnrollmentView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(EnrollmentView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(EnrollmentView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(EnrollmentView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form *
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new EnrollmentView().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}*/
