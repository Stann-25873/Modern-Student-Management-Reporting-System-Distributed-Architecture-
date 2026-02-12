package com.client.view;

import com.server.model.User;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

/**
 * Modern Teal User Management View.
 * Handles Administrative CRUD operations for System Users.
 */
public class UserManagementView extends JFrame {

    private static final long serialVersionUID = 1L;

    // --- Modern Teal Palette ---
    private static final Color TEAL_PRIMARY     = new Color(38, 166, 154);
    private static final Color TEAL_DARK        = new Color(0, 105, 92);
    private static final Color BG_LIGHT         = new Color(245, 247, 248);
    private static final Color DELETE_RED       = new Color(211, 47, 47);
    private static final Color TEXT_DARK        = new Color(33, 33, 33);
    
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 22);
    private static final Font LABEL_FONT = new Font("Segoe UI Semibold", Font.PLAIN, 13);
    private static final Font MAIN_FONT  = new Font("Segoe UI", Font.PLAIN, 14);

    // --- Components ---
    private JTable userTable;
    private DefaultTableModel tableModel;
    private JTextField idField, usernameField;
    private JPasswordField passwordField; 
    private JComboBox<String> roleComboBox;
    private JButton addButton, updateButton, deleteButton, refreshButton, clearButton, backButton;

    private static final String[] ROLES = {"ADMIN", "STUDENT", "LECTURER"};

    public UserManagementView() {
        setupFrame();
        initializeComponents();
        layoutComponents();
        applyTableAlignment();
    }

    private void setupFrame() {
        setTitle("University System | Admin User Management");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1050, 680); 
        getContentPane().setBackground(BG_LIGHT);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
    }

    private void initializeComponents() {
        // Styled Fields
        idField = createStyledTextField(false);
        usernameField = createStyledTextField(true);
        passwordField = createStyledPasswordField();
        
        roleComboBox = new JComboBox<>(ROLES);
        styleComboBox(roleComboBox);

        // Buttons (Using standardized naming)
        addButton     = createStyledButton("Create User", TEAL_PRIMARY);
        updateButton  = createStyledButton("Update Access", new Color(0, 150, 136));
        deleteButton  = createStyledButton("Revoke User", DELETE_RED);
        refreshButton = createStyledButton("Refresh", new Color(120, 144, 156));
        clearButton   = createStyledButton("Clear", new Color(158, 158, 158));
        backButton    = createStyledButton("Dashboard", new Color(69, 90, 100));

        // Table Setup
        String[] columnNames = {"User ID", "Username", "Authorization Role"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
            @Override public Class<?> getColumnClass(int col) { return col == 0 ? Long.class : String.class; }
        };
        userTable = new JTable(tableModel);
        styleTable(userTable);
    }

    private void layoutComponents() {
        // TOP HEADER
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setPreferredSize(new Dimension(1000, 70));
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 3, 0, TEAL_PRIMARY));

        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        navPanel.setOpaque(false);
        navPanel.add(backButton);
        
        JLabel titleLabel = new JLabel("System Access Control", SwingConstants.CENTER);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(TEXT_DARK);

        headerPanel.add(navPanel, BorderLayout.WEST);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(Box.createRigidArea(new Dimension(200, 0)), BorderLayout.EAST);

        // CENTER TABLE
        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setOpaque(false);
        tableContainer.setBorder(BorderFactory.createEmptyBorder(25, 25, 10, 25));
        tableContainer.add(new JScrollPane(userTable), BorderLayout.CENTER);

        // BOTTOM FORM
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setOpaque(false);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 25, 25, 25));

        JPanel formCard = new JPanel(new GridBagLayout());
        formCard.setBackground(Color.WHITE);
        formCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            new EmptyBorder(20, 25, 20, 25)
        ));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(8, 10, 8, 10);
        g.fill = GridBagConstraints.HORIZONTAL;

        addFormField(formCard, "User ID:", idField, g, 0, 0, 0.2);
        addFormField(formCard, "Username:", usernameField, g, 0, 2, 0.5);
        addFormField(formCard, "Password:", passwordField, g, 1, 0, 0.2);
        addFormField(formCard, "Role:", roleComboBox, g, 1, 2, 0.5);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(addButton); buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton); buttonPanel.add(refreshButton); buttonPanel.add(clearButton);

        g.gridy = 2; g.gridx = 0; g.gridwidth = 4;
        g.insets = new Insets(25, 0, 0, 0);
        formCard.add(buttonPanel, g);

        footerPanel.add(formCard, BorderLayout.CENTER);
        add(headerPanel, BorderLayout.NORTH);
        add(tableContainer, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);
    }

    // --- Functional Logic ---

    public void populateFields(User user) {
        if (user == null) return;
        idField.setText(String.valueOf(user.getId()));
        usernameField.setText(user.getUsername());
        passwordField.setText(""); // Security: do not show hashes
        roleComboBox.setSelectedItem(user.getUserRole());
    }

    public User getUserDataFromInputs() {
        String username = usernameField.getText().trim();
        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username cannot be empty", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        User user = new User();
        String idTxt = idField.getText().trim();
        if (!idTxt.isEmpty()) user.setId(Long.parseLong(idTxt));
        
        user.setUsername(username);
        user.setUserRole((String) roleComboBox.getSelectedItem());
        
        String pass = new String(passwordField.getPassword()).trim();
        user.setPasswordHash(pass.isEmpty() ? null : pass);

        return user;
    }

    public void refreshUserTable(List<User> users) {
        tableModel.setRowCount(0);
        if (users != null) {
            for (User u : users) {
                tableModel.addRow(new Object[]{ u.getId(), u.getUsername(), u.getUserRole() });
            }
        }
    }

    public void clearFields() {
        idField.setText("");
        usernameField.setText("");
        passwordField.setText("");
        roleComboBox.setSelectedIndex(0);
        userTable.clearSelection();
    }

    // --- UI Helper Methods ---

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

    private void applyTableAlignment() {
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        userTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
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

    private JPasswordField createStyledPasswordField() {
        JPasswordField f = new JPasswordField();
        f.setFont(MAIN_FONT);
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
        b.setFocusPainted(false);
        return b;
    }

    private void addFormField(JPanel panel, String label, JComponent comp, GridBagConstraints g, int y, int x, double weight) {
        g.gridy = y; g.gridx = x; g.weightx = 0;
        JLabel lbl = new JLabel(label);
        lbl.setFont(LABEL_FONT);
        panel.add(lbl, g);
        g.gridx = x + 1; g.weightx = weight;
        panel.add(comp, g);
    }

    // --- Accessors (Standardized) ---
    public JButton getAddBtn() { return addButton; }
    public JButton getUpdateBtn() { return updateButton; }
    public JButton getDeleteBtn() { return deleteButton; }
    public JButton getRefreshBtn() { return refreshButton; }
    public JButton getClearBtn() { return clearButton; }
    public JButton getBackBtn() { return backButton; }
    public JTable getUserTable() { return userTable; }
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
            java.util.logging.Logger.getLogger(UserManagementView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(UserManagementView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(UserManagementView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(UserManagementView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form *
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new UserManagementView().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}*/
