package com.client.view;

import com.server.model.Department;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.List;
import javax.swing.event.ListSelectionListener;

/**
 * Enhanced Department Management View
 * Features a modern Teal theme and fixed table alignment for numeric IDs.
 */
public class DepartmentView extends JFrame {

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

    private JTable departmentTable;
    private JTextField idField, nameField, codeField;
    private JButton addBtn, updateBtn, deleteBtn, refreshBtn, backBtn, clearBtn;

    private DefaultTableModel tableModel;
    private final String[] COLUMN_NAMES = {"ID", "Code", "Department Name"};

    public DepartmentView() {
        setupFrame();
        initComponents();
        layoutComponents();
        applyTableAlignment(); // FIX: Centers numeric IDs
    }

    private void setupFrame() {
        setTitle("University Management | Departments");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1100, 700);
        getContentPane().setBackground(BG_LIGHT);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
    }

    private void initComponents() {
        idField = createStyledTextField(false);
        nameField = createStyledTextField(true);
        codeField = createStyledTextField(true);

        setPlaceholder(nameField, "e.g., Information Technology");
        setPlaceholder(codeField, "e.g., IT");

        addBtn     = createStyledButton("Add New", TEAL_PRIMARY);
        updateBtn  = createStyledButton("Update Record", new Color(0, 150, 136));
        deleteBtn  = createStyledButton("Remove", DELETE_RED);
        refreshBtn = createStyledButton("Refresh", new Color(120, 144, 156));
        clearBtn   = createStyledButton("Clear Form", new Color(158, 158, 158));
        backBtn    = createStyledButton("← Back", new Color(69, 90, 100));

        tableModel = new DefaultTableModel(COLUMN_NAMES, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
            @Override public Class<?> getColumnClass(int c) { return c == 0 ? Long.class : String.class; }
        };
        departmentTable = new JTable(tableModel);
        styleTable(departmentTable);
    }

    private void applyTableAlignment() {
        // Center-align the ID column
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        // Add padding to text columns
        DefaultTableCellRenderer paddedRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0)); 
                return this;
            }
        };

        departmentTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        departmentTable.getColumnModel().getColumn(1).setCellRenderer(paddedRenderer);
        departmentTable.getColumnModel().getColumn(2).setCellRenderer(paddedRenderer);
    }

    private void layoutComponents() {
        // Top Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setPreferredSize(new Dimension(1100, 70));
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 3, 0, TEAL_PRIMARY));

        JPanel navBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        navBox.setOpaque(false);
        navBox.add(backBtn);
        
        JLabel titleLabel = new JLabel("Department Administration", SwingConstants.CENTER);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(TEXT_DARK);

        headerPanel.add(navBox, BorderLayout.WEST);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(Box.createRigidArea(new Dimension(120, 0)), BorderLayout.EAST);

        // Center Table
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.setBorder(new EmptyBorder(25, 25, 10, 25));
        centerPanel.add(new JScrollPane(departmentTable), BorderLayout.CENTER);

        // Bottom Form
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.setOpaque(false);
        southPanel.setBorder(new EmptyBorder(10, 25, 25, 25));

        JPanel formCard = new JPanel(new GridBagLayout());
        formCard.setBackground(Color.WHITE);
        formCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(210, 210, 210)),
            new EmptyBorder(20, 30, 20, 30)
        ));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(10, 10, 10, 10);
        g.fill = GridBagConstraints.HORIZONTAL;

        g.gridx = 0; g.gridy = 0; formCard.add(new JLabel("Dept ID:"), g);
        g.gridx = 1; g.weightx = 0.1; formCard.add(idField, g);
        g.gridx = 2; g.weightx = 0; formCard.add(new JLabel("Code:"), g);
        g.gridx = 3; g.weightx = 0.2; formCard.add(codeField, g);
        g.gridx = 4; g.weightx = 0; formCard.add(new JLabel("Name:"), g);
        g.gridx = 5; g.weightx = 0.5; formCard.add(nameField, g);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        actions.setOpaque(false);
        actions.add(addBtn); actions.add(updateBtn); actions.add(deleteBtn);
        actions.add(refreshBtn); actions.add(clearBtn);

        g.gridx = 0; g.gridy = 1; g.gridwidth = 6;
        g.insets = new Insets(25, 0, 0, 0);
        formCard.add(actions, g);

        southPanel.add(formCard, BorderLayout.CENTER);
        add(headerPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);
    }

    // --- Logic & Helpers ---

    public void populateFields(Department d) {
        if (d == null) return;
        idField.setText(String.valueOf(d.getId()));
        codeField.setText(d.getCode());
        codeField.setForeground(TEXT_DARK);
        nameField.setText(d.getName());
        nameField.setForeground(TEXT_DARK);
    }

    public void clearFields() {
        idField.setText("");
        resetPlaceholder(nameField, "e.g., Information Technology");
        resetPlaceholder(codeField, "e.g., IT");
        departmentTable.clearSelection();
    }

    private void resetPlaceholder(JTextField field, String hint) {
        field.setText(hint);
        field.setForeground(Color.GRAY);
    }

    public Department getDepartmentDataFromInputs() {
        try {
            String name = nameField.getText().trim();
            String code = codeField.getText().trim();
            
            // Validate against placeholder text
            if (name.equals("e.g., Information Technology") || name.isEmpty()) return null;
            if (code.equals("e.g., IT") || code.isEmpty()) return null;

            Department d = new Department();
            if (!idField.getText().isEmpty()) d.setId(Long.parseLong(idField.getText()));
            d.setName(name);
            d.setCode(code);
            return d;
        } catch (Exception e) { return null; }
    }

    public void refreshDepartmentTable(List<Department> list) {
        tableModel.setRowCount(0);
        if (list != null) {
            for (Department d : list) {
                tableModel.addRow(new Object[]{d.getId(), d.getCode(), d.getName()});
            }
        }
    }

    // --- Styling Factories ---
    private void styleTable(JTable table) {
        table.setRowHeight(38);
        table.setFont(MAIN_FONT);
        table.setShowVerticalLines(false);
        table.setSelectionBackground(new Color(224, 242, 241));
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
        f.setBackground(editable ? Color.WHITE : new Color(245, 245, 245));
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 189, 189)),
            new EmptyBorder(8, 12, 8, 12)
        ));
        return f;
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

    // --- Getters ---
    public JButton getAddBtn() { return addBtn; }
    public JButton getUpdateBtn() { return updateBtn; }
    public JButton getDeleteBtn() { return deleteBtn; }
    public JButton getRefreshBtn() { return refreshBtn; }
    public JButton getBackBtn() { return backBtn; }
    public JButton getClearBtn() { return clearBtn; }
    public JTable getDepartmentTable() { return departmentTable; }
    
    public long getSelectedDepartmentId() {
        int row = departmentTable.getSelectedRow();
        if (row != -1) return (long) departmentTable.getValueAt(row, 0);
        return -1;
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
            java.util.logging.Logger.getLogger(DepartmentView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(DepartmentView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(DepartmentView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DepartmentView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form *
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new DepartmentView().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
*/