package com.client.view;

import com.server.model.Course;
import com.server.model.Department;    
import com.server.model.Program;    
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.List;

public class CourseView extends JFrame {

    private static final long serialVersionUID = 1L;

    // --- Modern Color Palette ---
    private static final Color TEAL_PRIMARY     = new Color(38, 166, 154);
    private static final Color TEAL_DARK        = new Color(0, 105, 92);
    private static final Color BG_LIGHT         = new Color(245, 247, 248);
    private static final Color DELETE_RED       = new Color(211, 47, 47);
    private static final Color TEXT_DARK        = new Color(33, 33, 33);
    
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font LABEL_FONT = new Font("Segoe UI Semibold", Font.PLAIN, 13);
    private static final Font MAIN_FONT  = new Font("Segoe UI", Font.PLAIN, 14);

    // --- UI Components ---
    private JTable courseTable;
    private JTextField idField, codeField, titleField, creditsField;
    private JComboBox<Department> departmentComboBox;    
    private JComboBox<Program> programComboBox;       
    private JButton addBtn, updateBtn, deleteBtn, refreshBtn, clearBtn, backBtn;    

    private final String[] COLUMN_NAMES = {"ID", "Course Code", "Course Title", "Credits", "Target Program"};
    private DefaultTableModel tableModel;

    public CourseView() {
        setupFrame();
        initComponents();
        layoutComponents();
        applyTableAlignment(); // FIX: Apply the alignment fix for numbers
    }

    private void setupFrame() {
        setTitle("University Management | Course Administration");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1150, 750);
        getContentPane().setBackground(BG_LIGHT);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        idField = createStyledTextField(false);
        idField.setToolTipText("System generated ID (Auto-increment)");

        codeField = createStyledTextField(true);
        setPlaceholder(codeField, "e.g., CS101");

        titleField = createStyledTextField(true);
        setPlaceholder(titleField, "e.g., Introduction to Programming");

        creditsField = createStyledTextField(true);
        setPlaceholder(creditsField, "e.g., 3");

        departmentComboBox = new JComboBox<>();
        programComboBox = new JComboBox<>();
        styleComboBox(departmentComboBox);
        styleComboBox(programComboBox);

        addBtn     = createStyledButton("Add Course", TEAL_PRIMARY);
        updateBtn  = createStyledButton("Update Details", new Color(0, 150, 136));
        deleteBtn  = createStyledButton("Delete", DELETE_RED);
        refreshBtn = createStyledButton("Refresh", new Color(120, 144, 156));
        clearBtn   = createStyledButton("Clear Fields", new Color(158, 158, 158));
        backBtn    = createStyledButton("← Back", new Color(69, 90, 100));

        tableModel = new DefaultTableModel(COLUMN_NAMES, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
            @Override public Class<?> getColumnClass(int c) { 
                return (c == 0 || c == 3) ? Long.class : String.class; 
            }
        };
        courseTable = new JTable(tableModel);
        styleTable(courseTable);
    }

    private void applyTableAlignment() {
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        DefaultTableCellRenderer leftPaddingRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0)); 
                return this;
            }
        };

        // Align ID and Credits to center to fix the "squishing" seen in screenshots
        courseTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // ID
        courseTable.getColumnModel().getColumn(1).setCellRenderer(leftPaddingRenderer); // Code
        courseTable.getColumnModel().getColumn(2).setCellRenderer(leftPaddingRenderer); // Title
        courseTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer); // Credits
        courseTable.getColumnModel().getColumn(4).setCellRenderer(leftPaddingRenderer); // Program
    }

    private void layoutComponents() {
        // Top Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setPreferredSize(new Dimension(1100, 75));
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 3, 0, TEAL_PRIMARY));

        JPanel navBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 17));
        navBox.setOpaque(false);
        navBox.add(backBtn);
        
        JLabel titleLabel = new JLabel("Course Management System", SwingConstants.CENTER);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(TEXT_DARK);

        headerPanel.add(navBox, BorderLayout.WEST);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(Box.createRigidArea(new Dimension(130, 0)), BorderLayout.EAST);

        // Center Table
        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setOpaque(false);
        tableContainer.setBorder(new EmptyBorder(20, 25, 10, 25));
        tableContainer.add(new JScrollPane(courseTable), BorderLayout.CENTER);

        // Bottom Form
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setOpaque(false);
        footerPanel.setBorder(new EmptyBorder(10, 25, 25, 25));

        JPanel formCard = new JPanel(new GridBagLayout());
        formCard.setBackground(Color.WHITE);
        formCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            new EmptyBorder(25, 30, 25, 30)
        ));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(10, 12, 10, 12);
        g.fill = GridBagConstraints.HORIZONTAL;

        g.gridx = 0; g.gridy = 0; formCard.add(new JLabel("ID:"), g);
        g.gridx = 1; g.weightx = 0.1; formCard.add(idField, g);
        g.gridx = 2; g.weightx = 0; formCard.add(new JLabel("Code:"), g);
        g.gridx = 3; g.weightx = 0.2; formCard.add(codeField, g);
        g.gridx = 4; g.weightx = 0; formCard.add(new JLabel("Title:"), g);
        g.gridx = 5; g.weightx = 0.5; formCard.add(titleField, g);

        g.gridy = 1; g.gridx = 0; g.weightx = 0; formCard.add(new JLabel("Credits:"), g);
        g.gridx = 1; formCard.add(creditsField, g);
        g.gridx = 2; formCard.add(new JLabel("Program:"), g);
        g.gridx = 3; g.gridwidth = 3; formCard.add(programComboBox, g);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        btnPanel.setOpaque(false);
        btnPanel.add(addBtn); btnPanel.add(updateBtn); btnPanel.add(deleteBtn);
        btnPanel.add(refreshBtn); btnPanel.add(clearBtn);

        g.gridy = 2; g.gridx = 0; g.gridwidth = 6;
        g.insets = new Insets(25, 0, 0, 0);
        formCard.add(btnPanel, g);

        footerPanel.add(formCard, BorderLayout.CENTER);
        add(headerPanel, BorderLayout.NORTH);
        add(tableContainer, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);
    }

    // --- Styling Helpers ---
    private void styleTable(JTable table) {
        table.setRowHeight(38);
        table.setFont(MAIN_FONT);
        table.setSelectionBackground(new Color(224, 242, 241));
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

    private void styleComboBox(JComboBox<?> box) {
        box.setFont(MAIN_FONT);
        box.setBackground(Color.WHITE);
        box.setPreferredSize(new Dimension(0, 38));
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
            @Override
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(hint)) {
                    field.setText("");
                    field.setForeground(TEXT_DARK);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setForeground(Color.GRAY);
                    field.setText(hint);
                }
            }
        });
        field.setText(hint);
        field.setForeground(Color.GRAY);
    }

    // --- Logic Methods ---

    public void populateFields(Course course) {
        if (course == null) return;
        idField.setText(String.valueOf(course.getId()));
        codeField.setText(course.getCode());
        codeField.setForeground(TEXT_DARK);
        titleField.setText(course.getTitle());
        titleField.setForeground(TEXT_DARK);
        creditsField.setText(String.valueOf(course.getCredits()));
        creditsField.setForeground(TEXT_DARK);

        if (course.getPrograms() != null && !course.getPrograms().isEmpty()) {
            programComboBox.setSelectedItem(course.getPrograms().iterator().next());
        } else {
            programComboBox.setSelectedIndex(-1);
        }
    }

    public void clearFields() {
        idField.setText("");
        resetPlaceholder(codeField, "e.g., CS101");
        resetPlaceholder(titleField, "e.g., Introduction to Programming");
        resetPlaceholder(creditsField, "e.g., 3");
        programComboBox.setSelectedIndex(-1);
        courseTable.clearSelection();
    }

    private void resetPlaceholder(JTextField field, String hint) {
        field.setText(hint);
        field.setForeground(Color.GRAY);
    }

    public void refreshCourseTable(List<Course> courses) {
        tableModel.setRowCount(0);
        if (courses != null) {
            for (Course c : courses) {
                String pName = (c.getPrograms() != null && !c.getPrograms().isEmpty()) ? 
                               c.getPrograms().iterator().next().getName() : "General";
                tableModel.addRow(new Object[]{ c.getId(), c.getCode(), c.getTitle(), c.getCredits(), pName });
            }
        }
    }

    public long getSelectedCourseIdFromTable() {
        int row = courseTable.getSelectedRow();
        if (row != -1) {
            return (long) courseTable.getModel().getValueAt(courseTable.convertRowIndexToModel(row), 0);
        }
        return -1;
    }

    public Course getCourseDataFromInputs() {
        try {
            String code = codeField.getText().trim();
            String title = titleField.getText().trim();
            if (code.equals("e.g., CS101") || title.equals("e.g., Introduction to Programming")) throw new Exception("Required fields missing");

            Course course = new Course();
            if (!idField.getText().isEmpty()) course.setId(Long.parseLong(idField.getText()));
            course.setCode(code);
            course.setTitle(title);
            course.setCredits(Integer.parseInt(creditsField.getText()));
            
            Program sel = (Program) programComboBox.getSelectedItem();
            if (sel != null) course.getPrograms().add(sel);
            
            return course;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Please check your inputs.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return null;
        }
    }

    // Getters for Controller
    public JButton getAddCourseButton() { return addBtn; }
    public JButton getUpdateCourseButton() { return updateBtn; }
    public JButton getDeleteCourseButton() { return deleteBtn; }
    public JButton getRefreshDataButton() { return refreshBtn; }
    public JButton getClearFieldsButton() { return clearBtn; }
    public JButton getBackButton() { return backBtn; }
    public JTable getCourseTable() { return courseTable; }
    public void populateProgramComboBox(List<Program> p) {
        programComboBox.removeAllItems();
        if (p != null) for (Program prog : p) programComboBox.addItem(prog);
    }
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
            java.util.logging.Logger.getLogger(CourseView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CourseView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CourseView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CourseView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form *
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new CourseView().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}*/
