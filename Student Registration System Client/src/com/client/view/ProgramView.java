package com.client.view;

import com.server.model.Program;
import com.server.model.Department;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.List;

public class ProgramView extends JFrame {

    private static final long serialVersionUID = 1L;
    private final JFrame parentFrame;

    // --- Modern Teal Palette ---
    private static final Color TEAL_PRIMARY     = new Color(38, 166, 154);
    private static final Color TEAL_DARK        = new Color(0, 105, 92);
    private static final Color BG_LIGHT         = new Color(245, 247, 248);
    private static final Color DELETE_RED       = new Color(211, 47, 47);
    private static final Color TEXT_DARK        = new Color(33, 33, 33);
    
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font LABEL_FONT = new Font("Segoe UI Semibold", Font.PLAIN, 13);
    private static final Font MAIN_FONT  = new Font("Segoe UI", Font.PLAIN, 14);

    // --- Components ---
    private JTable programTable;
    private DefaultTableModel tableModel;
    private JTextField idField, nameField, durationField;
    private JComboBox<Department> departmentComboBox;
    private JButton addButton, updateButton, deleteButton, refreshButton, backButton, clearFieldsButton;

    public ProgramView(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        setupFrame();
        initializeComponents();
        layoutComponents();
    }

    private void setupFrame() {
        setTitle("Academic Program Management");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 700);
        getContentPane().setBackground(BG_LIGHT);
        setLayout(new BorderLayout());
        setLocationRelativeTo(parentFrame);
    }

    private void initializeComponents() {
        // ID: System Managed
        idField = createStyledTextField(false);
        idField.setToolTipText("System ID - Not editable");

        // Name: String input
        nameField = createStyledTextField(true);
        setPlaceholder(nameField, "e.g., Bachelor of Computer Science");
        nameField.setToolTipText("Enter the full official name of the program");

        // Duration: Numeric input
        durationField = createStyledTextField(true);
        setPlaceholder(durationField, "e.g., 4");
        durationField.setToolTipText("Enter number of years (e.g., 3 or 4)");
        
        departmentComboBox = new JComboBox<>();
        styleComboBox(departmentComboBox);

        // Buttons
        addButton     = createStyledButton("Add Program", TEAL_PRIMARY);
        updateButton  = createStyledButton("Update Details", new Color(0, 150, 136));
        deleteButton  = createStyledButton("Delete", DELETE_RED);
        refreshButton = createStyledButton("Refresh", new Color(120, 144, 156));
        clearFieldsButton = createStyledButton("Clear", new Color(158, 158, 158));
        backButton    = createStyledButton("← Back to Dashboard", new Color(69, 90, 100));

        // Table Setup
        String[] columnNames = {"ID", "Program Name", "Duration (Yrs)", "Department"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
            @Override public Class<?> getColumnClass(int col) {
                if (col == 0) return Long.class;
                if (col == 2) return Integer.class;
                return String.class;
            }
        };
        programTable = new JTable(tableModel);
        styleTable(programTable);
    }

    private void layoutComponents() {
        // 1. TOP HEADER
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setPreferredSize(new Dimension(1000, 75));
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 3, 0, TEAL_PRIMARY));

        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 17));
        navPanel.setOpaque(false);
        navPanel.add(backButton);
        
        JLabel titleLabel = new JLabel("Academic Programs", SwingConstants.CENTER);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(TEXT_DARK);

        headerPanel.add(navPanel, BorderLayout.WEST);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(Box.createRigidArea(new Dimension(200, 0)), BorderLayout.EAST);

        // 2. CENTER TABLE
        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setOpaque(false);
        tableContainer.setBorder(new EmptyBorder(25, 25, 10, 25));

        JScrollPane scrollPane = new JScrollPane(programTable);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        tableContainer.add(scrollPane, BorderLayout.CENTER);

        // 3. BOTTOM FORM CARD
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

        // Row 1
        addFormField(formCard, "Program ID:", idField, g, 0, 0, 0.1);
        addFormField(formCard, "Official Name:", nameField, g, 0, 2, 0.5);

        // Row 2
        addFormField(formCard, "Duration (Years):", durationField, g, 1, 0, 0.1);
        addFormField(formCard, "Faculty Department:", departmentComboBox, g, 1, 2, 0.5);

        // Row 3: Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(clearFieldsButton);

        g.gridy = 2; g.gridx = 0; g.gridwidth = 4;
        g.insets = new Insets(25, 0, 0, 0);
        formCard.add(buttonPanel, g);

        footerPanel.add(formCard, BorderLayout.CENTER);

        add(headerPanel, BorderLayout.NORTH);
        add(tableContainer, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);
    }

    // --- Helper UI Methods ---

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
        table.setSelectionBackground(new Color(224, 242, 241));
        table.setSelectionForeground(TEAL_DARK);
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
        b.setFocusPainted(false);
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

    // --- Logic ---

    public Program getProgramDataFromInputs() {
        try {
            String name = nameField.getText().trim();
            String durationStr = durationField.getText().trim();
            
            if (name.isEmpty() || name.equals("e.g., Bachelor of Computer Science")) throw new Exception("Program name is required.");
            
            Program program = new Program();
            if (!idField.getText().isEmpty()) program.setId(Long.parseLong(idField.getText()));
            program.setName(name);

            int duration = Integer.parseInt(durationStr.equals("e.g., 4") ? "0" : durationStr);
            if (duration <= 0) throw new Exception("Duration must be a positive number.");
            program.setDurationYears(duration);

            Department selectedDept = (Department) departmentComboBox.getSelectedItem();
            if (selectedDept == null) throw new Exception("Please select a Department.");
            program.setDepartment(selectedDept);

            return program;
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Duration must be a whole number (e.g., 3).", "Input Type Error", JOptionPane.ERROR_MESSAGE);
            return null;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Incomplete Form", JOptionPane.WARNING_MESSAGE);
            return null;
        }
    }

    public void populateFields(Program program) {
        if (program != null) {
            idField.setText(String.valueOf(program.getId()));
            nameField.setText(program.getName());
            nameField.setForeground(TEXT_DARK);
            durationField.setText(String.valueOf(program.getDurationYears()));
            durationField.setForeground(TEXT_DARK);
            departmentComboBox.setSelectedItem(program.getDepartment());
        } else {
            clearFields();
        }
    }

    public void clearFields() {
        idField.setText("");
        setPlaceholder(nameField, "e.g., Bachelor of Computer Science");
        setPlaceholder(durationField, "e.g., 4");
        departmentComboBox.setSelectedIndex(-1);
        programTable.clearSelection();
    }

    public void refreshProgramTable(List<Program> list) {
        tableModel.setRowCount(0);
        if (list != null) {
            for (Program p : list) {
                tableModel.addRow(new Object[]{
                    p.getId(), p.getName(), p.getDurationYears(),
                    p.getDepartment() != null ? p.getDepartment().getName() : "Unassigned"
                });
            }
        }
    }

    public void populateDepartmentComboBox(List<Department> list) {
        departmentComboBox.removeAllItems();
        if (list != null) for (Department d : list) departmentComboBox.addItem(d);
    }

    // --- Accessors ---
    public JButton getAddProgramButton() { return addButton; }
    public JButton getUpdateProgramButton() { return updateButton; }
    public JButton getDeleteProgramButton() { return deleteButton; }
    public JButton getRefreshButton() { return refreshButton; }
    public JButton getClearFieldsButton() { return clearFieldsButton; }
    public JButton getBackButton() { return backButton; }
    public JTable getProgramTable() { return programTable; }

    public void addProgramTableSelectionListener(ListSelectionListener l) {
        programTable.getSelectionModel().addListSelectionListener(l);
    }
    /**
 * Retrieves the ID of the program currently selected in the JTable.
 * * @return The Program ID as a long, or -1 if no row is selected.
 */
   
    
    public long getSelectedProgramIdFromTable() {
    int selectedRow = programTable.getSelectedRow();
    
    // Check if a row is actually selected
    if (selectedRow != -1) {
        try {
            // Convert the view index to the model index (important if the table is sortable)
            int modelRow = programTable.convertRowIndexToModel(selectedRow);
            
            // Assuming the ID is in the first column (index 0)
            Object idValue = programTable.getModel().getValueAt(modelRow, 0);
            
            if (idValue instanceof Long) {
                return (Long) idValue;
            } else if (idValue instanceof Integer) {
                return ((Integer) idValue).longValue();
            } else {
                return Long.parseLong(idValue.toString());
            }
        } catch (Exception e) {
            System.err.println("Error retrieving ID from table: " + e.getMessage());
            return -1;
        }
    }
    
    return -1; // No selection
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
            java.util.logging.Logger.getLogger(ProgramView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ProgramView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ProgramView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ProgramView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form *
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ProgramView().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
*/