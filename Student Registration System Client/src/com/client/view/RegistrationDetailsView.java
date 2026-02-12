package com.client.view;

import com.server.model.RegistrationDetails;
import com.server.model.Student;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Enhanced Registration Details View.
 * Featuring a modern Teal UI, Date Validation, and Centered Table Alignment.
 */
public class RegistrationDetailsView extends JFrame {

    private static final long serialVersionUID = 1L;
    private static final String DATE_PATTERN = "yyyy-MM-dd";
    private final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(DATE_PATTERN);

    // --- Modern Teal Palette ---
    private static final Color TEAL_PRIMARY     = new Color(38, 166, 154);
    private static final Color TEAL_DARK        = new Color(0, 105, 92);
    private static final Color BG_LIGHT         = new Color(245, 247, 248);
    private static final Color DELETE_RED       = new Color(211, 47, 47);
    private static final Color TEXT_DARK        = new Color(33, 33, 33);
    
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 22);
    private static final Font LABEL_FONT = new Font("Segoe UI Semibold", Font.PLAIN, 13);
    private static final Font MAIN_FONT  = new Font("Segoe UI", Font.PLAIN, 14);

    private JTable detailsTable;
    private DefaultTableModel tableModel;
    private JTextField idField, academicYearField, currentSemesterField, registrationDateField, deadlineField;
    private JComboBox<Student> studentComboBox;
    private JComboBox<String> statusComboBox;
    private JButton addButton, updateButton, deleteButton, refreshButton, clearButton, backButton;

    private final Map<Long, Student> studentMap = new HashMap<>();

    public RegistrationDetailsView() {
        setupFrame();
        initializeComponents();
        layoutComponents();
        applyTableAlignment();
        
        // Initial setup
        String[] statuses = {"REGISTERED", "PROVISIONAL", "PENDING", "COMPLETED"};
        for (String status : statuses) statusComboBox.addItem(status);
    }

    private void setupFrame() {
        setTitle("University Management | Registration Portal");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1150, 750);
        getContentPane().setBackground(BG_LIGHT);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
    }

    private void initializeComponents() {
        idField = createStyledTextField(false);
        academicYearField = createStyledTextField(true);
        currentSemesterField = createStyledTextField(true);
        registrationDateField = createStyledTextField(true);
        deadlineField = createStyledTextField(true);

        setPlaceholder(academicYearField, "e.g., 2023-2024");
        setPlaceholder(currentSemesterField, "e.g., SEMESTER 1");
        setPlaceholder(registrationDateField, DATE_PATTERN);
        setPlaceholder(deadlineField, DATE_PATTERN);

        studentComboBox = new JComboBox<>();
        statusComboBox = new JComboBox<>();
        styleComboBox(studentComboBox);
        styleComboBox(statusComboBox);

        addButton    = createStyledButton("Register Student", TEAL_PRIMARY);
        updateButton = createStyledButton("Update Record", new Color(0, 150, 136));
        deleteButton = createStyledButton("Delete Record", DELETE_RED);
        refreshButton = createStyledButton("Refresh", new Color(120, 144, 156));
        clearButton  = createStyledButton("Clear Form", new Color(158, 158, 158));
        backButton   = createStyledButton("Dashboard", new Color(69, 90, 100));

        String[] columnNames = {"ID", "Student ID", "Full Name", "Year", "Semester", "Reg. Date", "Deadline", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
            @Override public Class<?> getColumnClass(int col) {
                return (col == 0 || col == 1) ? Long.class : String.class;
            }
        };
        detailsTable = new JTable(tableModel);
        styleTable(detailsTable);
    }

    private void applyTableAlignment() {
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Center the ID columns (Index 0 and 1)
        detailsTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        detailsTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
    }

    private void layoutComponents() {
        // TOP HEADER
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setPreferredSize(new Dimension(1150, 70));
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 3, 0, TEAL_PRIMARY));

        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        navPanel.setOpaque(false);
        navPanel.add(backButton);
        
        JLabel titleLabel = new JLabel("Enrollment & Registration Records", SwingConstants.CENTER);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(TEXT_DARK);

        headerPanel.add(navPanel, BorderLayout.WEST);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(Box.createRigidArea(new Dimension(200, 0)), BorderLayout.EAST);

        // CENTER TABLE
        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setOpaque(false);
        tableContainer.setBorder(new EmptyBorder(25, 25, 10, 25));
        tableContainer.add(new JScrollPane(detailsTable), BorderLayout.CENTER);

        // BOTTOM FORM
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setOpaque(false);
        footerPanel.setBorder(new EmptyBorder(10, 25, 25, 25));

        JPanel formCard = new JPanel(new GridBagLayout());
        formCard.setBackground(Color.WHITE);
        formCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            new EmptyBorder(20, 25, 20, 25)
        ));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(8, 10, 8, 10);
        g.fill = GridBagConstraints.HORIZONTAL;

        addFormField(formCard, "Registration ID:", idField, g, 0, 0, 0.2);
        addFormField(formCard, "Student:", studentComboBox, g, 0, 2, 0.5);
        addFormField(formCard, "Academic Year:", academicYearField, g, 1, 0, 0.2);
        addFormField(formCard, "Semester:", currentSemesterField, g, 1, 2, 0.5);
        addFormField(formCard, "Reg. Date:", registrationDateField, g, 2, 0, 0.2);
        addFormField(formCard, "Deadline:", deadlineField, g, 2, 2, 0.5);
        
        g.gridy = 3; g.gridx = 0; g.weightx = 0;
        formCard.add(new JLabel("Status:"), g);
        g.gridx = 1; g.weightx = 0.5;
        formCard.add(statusComboBox, g);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(addButton); buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton); buttonPanel.add(refreshButton); buttonPanel.add(clearButton);

        g.gridy = 4; g.gridx = 0; g.gridwidth = 4;
        g.insets = new Insets(25, 0, 0, 0);
        formCard.add(buttonPanel, g);

        footerPanel.add(formCard, BorderLayout.CENTER);
        add(headerPanel, BorderLayout.NORTH);
        add(tableContainer, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);
    }

    // --- Functional Logic ---

    public void populateFields(RegistrationDetails rd) {
        if (rd == null) return;
        idField.setText(String.valueOf(rd.getId()));
        academicYearField.setText(rd.getAcademicYear());
        academicYearField.setForeground(TEXT_DARK);
        currentSemesterField.setText(rd.getCurrentSemester());
        currentSemesterField.setForeground(TEXT_DARK);
        
        registrationDateField.setText(rd.getRegistrationDate() != null ? DATE_FORMAT.format(rd.getRegistrationDate()) : DATE_PATTERN);
        registrationDateField.setForeground(rd.getRegistrationDate() != null ? TEXT_DARK : Color.GRAY);
        
        deadlineField.setText(rd.getDeadline() != null ? DATE_FORMAT.format(rd.getDeadline()) : DATE_PATTERN);
        deadlineField.setForeground(rd.getDeadline() != null ? TEXT_DARK : Color.GRAY);
        
        statusComboBox.setSelectedItem(rd.getRegistrationStatus());
        if (rd.getStudent() != null) {
            studentComboBox.setSelectedItem(studentMap.get(rd.getStudent().getId()));
        }
    }

    public RegistrationDetails getDetailsDataFromInputs() {
        try {
            RegistrationDetails rd = new RegistrationDetails();
            String idTxt = idField.getText().trim();
            if (!idTxt.isEmpty()) rd.setId(Long.parseLong(idTxt));

            Student selected = (Student) studentComboBox.getSelectedItem();
            if (selected == null) throw new Exception("Please select a student.");
            rd.setStudent(selected);

            rd.setAcademicYear(validateInput(academicYearField, "e.g., 2023-2024"));
            rd.setCurrentSemester(validateInput(currentSemesterField, "e.g., SEMESTER 1"));
            rd.setRegistrationStatus((String) statusComboBox.getSelectedItem());

            String rDate = registrationDateField.getText().trim();
            if (!rDate.equals(DATE_PATTERN)) rd.setRegistrationDate(DATE_FORMAT.parse(rDate));
            
            String dDate = deadlineField.getText().trim();
            if (!dDate.equals(DATE_PATTERN)) rd.setDeadline(DATE_FORMAT.parse(dDate));

            return rd;
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "Date format must be YYYY-MM-DD", "Format Error", JOptionPane.ERROR_MESSAGE);
            return null;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Input Error", JOptionPane.WARNING_MESSAGE);
            return null;
        }
    }

    private String validateInput(JTextField field, String hint) {
        String val = field.getText().trim();
        return val.equals(hint) ? "" : val;
    }

    public void populateStudentComboBox(List<Student> students) {
        studentComboBox.removeAllItems();
        studentMap.clear();
        if (students != null) {
            for (Student s : students) {
                studentComboBox.addItem(s);
                studentMap.put(s.getId(), s);
            }
        }
    }

    public void refreshDetailsTable(List<RegistrationDetails> list) {
        tableModel.setRowCount(0);
        if (list != null) {
            for (RegistrationDetails rd : list) {
                tableModel.addRow(new Object[]{
                    rd.getId(),
                    rd.getStudent() != null ? rd.getStudent().getId() : null,
                    rd.getStudent() != null ? rd.getStudent().getFirstName() + " " + rd.getStudent().getLastName() : "N/A",
                    rd.getAcademicYear(),
                    rd.getCurrentSemester(),
                    rd.getRegistrationDate() != null ? DATE_FORMAT.format(rd.getRegistrationDate()) : "",
                    rd.getDeadline() != null ? DATE_FORMAT.format(rd.getDeadline()) : "",
                    rd.getRegistrationStatus()
                });
            }
        }
    }

    public void clearFields() {
        idField.setText("");
        resetPlaceholder(academicYearField, "e.g., 2023-2024");
        resetPlaceholder(currentSemesterField, "e.g., SEMESTER 1");
        resetPlaceholder(registrationDateField, DATE_PATTERN);
        resetPlaceholder(deadlineField, DATE_PATTERN);
        statusComboBox.setSelectedIndex(0);
        detailsTable.clearSelection();
    }

    private void resetPlaceholder(JTextField field, String hint) {
        field.setText(hint);
        field.setForeground(Color.GRAY);
    }

    // --- UI Styling Factories ---

    private void addFormField(JPanel panel, String label, JComponent comp, GridBagConstraints g, int y, int x, double weight) {
        g.gridy = y; g.gridx = x; g.weightx = 0;
        JLabel lbl = new JLabel(label);
        lbl.setFont(LABEL_FONT);
        panel.add(lbl, g);
        g.gridx = x + 1; g.weightx = weight;
        panel.add(comp, g);
    }

    private void styleTable(JTable table) {
        table.setRowHeight(38);
        table.setFont(MAIN_FONT);
        table.setShowVerticalLines(false);
        JTableHeader h = table.getTableHeader();
        h.setBackground(TEAL_DARK);
        h.setForeground(Color.WHITE);
        h.setFont(new Font("Segoe UI", Font.BOLD, 14));
        h.setPreferredSize(new Dimension(0, 45));
    }

    private JTextField createStyledTextField(boolean editable) {
        JTextField f = new JTextField();
        f.setEditable(editable);
        f.setFont(MAIN_FONT);
        f.setBackground(editable ? Color.WHITE : new Color(248, 248, 248));
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(8, 12, 8, 12)
        ));
        return f;
    }

    private void styleComboBox(JComboBox<?> cb) {
        cb.setFont(MAIN_FONT);
        cb.setBackground(Color.WHITE);
        cb.setPreferredSize(new Dimension(0, 38));
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setBorder(new EmptyBorder(10, 20, 10, 20));
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

    // Getters
    public JButton getAddBtn() { return addButton; }
    public JButton getUpdateBtn() { return updateButton; }
    public JButton getDeleteBtn() { return deleteButton; }
    public JButton getRefreshBtn() { return refreshButton; }
    public JButton getClearBtn() { return clearButton; }
    public JButton getBackBtn() { return backButton; }
    public JTable getDetailsTable() { return detailsTable; }
}








/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *
package com.client.view;

/**
 *
 * @author USER
 *
public class RegistrationDetailsView extends javax.swing.JFrame {

    /**
     * Creates new form RegistrationDetailsView
     *
    public RegistrationDetailsView() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     *
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
            java.util.logging.Logger.getLogger(RegistrationDetailsView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(RegistrationDetailsView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(RegistrationDetailsView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(RegistrationDetailsView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form *
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new RegistrationDetailsView().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
*/