package com.client.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class UserRegistrationView extends JFrame {

    private static final long serialVersionUID = 1L;

    // --- Custom Theme Colors ---
    private static final Color TEAL_PRIMARY = new Color(45, 126, 114);
    private static final Color BG_LIGHT = new Color(240, 245, 245);
    private static final Font MAIN_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font BOLD_FONT = new Font("Segoe UI", Font.BOLD, 14);

    private JTextField usernameField, firstNameField, lastNameField, emailField;
    private JPasswordField passwordField, confirmPasswordField;
    private JComboBox<String> roleComboBox;
    private JButton registerButton, cancelButton;

    public UserRegistrationView() {
        setTitle("System Registration - Create New Account");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(500, 600);
        getContentPane().setBackground(BG_LIGHT);
        setLayout(new BorderLayout());
        setResizable(false);
        setLocationRelativeTo(null); 

        initComponents();
        layoutComponents();
    }

    private void initComponents() {
        // Styled Input Fields
        usernameField = createStyledTextField();
        firstNameField = createStyledTextField();
        lastNameField = createStyledTextField();
        emailField = createStyledTextField();
        
        passwordField = createStyledPasswordField();
        confirmPasswordField = createStyledPasswordField();
        
        String[] roles = {"STUDENT", "ADMIN", "STAFF"}; 
        roleComboBox = new JComboBox<>(roles);
        roleComboBox.setFont(MAIN_FONT);
        roleComboBox.setBackground(Color.WHITE);
        
        // Styled Buttons
        registerButton = createStyledButton("Register Account", TEAL_PRIMARY);
        cancelButton = createStyledButton("Cancel", new Color(120, 130, 140));
    }

    private void layoutComponents() {
        // --- Header Section ---
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(TEAL_PRIMARY);
        headerPanel.setPreferredSize(new Dimension(500, 80));
        JLabel titleLabel = new JLabel("Create New Account");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(new EmptyBorder(20, 0, 0, 0));
        headerPanel.add(titleLabel);
        add(headerPanel, BorderLayout.NORTH);

        // --- Form Section (Card Layout) ---
        JPanel containerPanel = new JPanel(new GridBagLayout());
        containerPanel.setOpaque(false);
        
        JPanel formCard = new JPanel(new GridBagLayout());
        formCard.setBackground(Color.WHITE);
        formCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(25, 30, 25, 30)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 5, 10, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Form Fields
        addFormField(formCard, "Username:", usernameField, gbc, 0);
        addFormField(formCard, "Password:", passwordField, gbc, 1);
        addFormField(formCard, "Confirm Password:", confirmPasswordField, gbc, 2);
        addFormField(formCard, "First Name:", firstNameField, gbc, 3);
        addFormField(formCard, "Last Name:", lastNameField, gbc, 4);
        addFormField(formCard, "Email Address:", emailField, gbc, 5);
        addFormField(formCard, "Account Type:", roleComboBox, gbc, 6);

        // Buttons Row
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setOpaque(false);
        buttonPanel.add(registerButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0; gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 0, 0, 0);
        formCard.add(buttonPanel, gbc);

        containerPanel.add(formCard);
        add(containerPanel, BorderLayout.CENTER);
    }

    // --- Styling Helpers ---
    private void addFormField(JPanel panel, String labelText, JComponent field, GridBagConstraints gbc, int row) {
        gbc.gridwidth = 1;
        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.weightx = 0;
        JLabel label = new JLabel(labelText);
        label.setFont(BOLD_FONT);
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panel.add(field, gbc);
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField(15);
        styleJComponent(field);
        return field;
    }

    private JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField(15);
        styleJComponent(field);
        return field;
    }

    private void styleJComponent(JComponent comp) {
        comp.setFont(MAIN_FONT);
        comp.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(7, 10, 7, 10)
        ));
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(BOLD_FONT);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(10, 25, 10, 25));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // --- Accessors for the Controller ---
    public JTextField getUsernameField() { return usernameField; }
    public JPasswordField getPasswordField() { return passwordField; }
    public JPasswordField getConfirmPasswordField() { return confirmPasswordField; }
    public JTextField getFirstNameField() { return firstNameField; }
    public JTextField getLastNameField() { return lastNameField; }
    public JTextField getEmailField() { return emailField; }
    public JComboBox<String> getRoleComboBox() { return roleComboBox; }
    public JButton getRegisterButton() { return registerButton; }
    public JButton getCancelButton() { return cancelButton; }
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

    /*
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
            java.util.logging.Logger.getLogger(UserRegistrationView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(UserRegistrationView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(UserRegistrationView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(UserRegistrationView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form *
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new UserRegistrationView().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}*/
