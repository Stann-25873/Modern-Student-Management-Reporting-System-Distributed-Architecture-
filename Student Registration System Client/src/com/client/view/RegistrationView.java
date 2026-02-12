package com.client.view;

import com.server.model.User;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class RegistrationView extends JFrame {

    private static final long serialVersionUID = 1L;

    // --- Custom Theme Colors ---
    private static final Color TEAL_PRIMARY = new Color(45, 126, 114);
    private static final Color BG_LIGHT = new Color(240, 245, 245);
    private static final Font MAIN_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font BOLD_FONT = new Font("Segoe UI", Font.BOLD, 14);

    private JTextField usernameField, firstNameField, lastNameField, emailField, otpField;
    private JPasswordField passwordField, confirmPasswordField;
    private JButton registerButton, backToLoginButton, sendOtpButton;

    public RegistrationView() {
        setTitle("Student Registration System - Sign Up");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(500, 780); // Increased height for OTP field
        setResizable(false);
        setLocationRelativeTo(null);

        initComponents();
        layoutComponents();
    }

    private void initComponents() {
        // Styled Fields
        usernameField = createStyledTextField();
        passwordField = createStyledPasswordField();
        confirmPasswordField = createStyledPasswordField();
        firstNameField = createStyledTextField();
        lastNameField = createStyledTextField();
        emailField = createStyledTextField();
        
        // OTP Components
        otpField = createStyledTextField();
        sendOtpButton = createStyledButton("SEND CODE", new Color(100, 100, 100));
        sendOtpButton.setPreferredSize(new Dimension(120, 35));

        // Styled Buttons
        registerButton = createStyledButton("CREATE ACCOUNT", TEAL_PRIMARY);
        
        backToLoginButton = new JButton("Already have an account? Log In");
        backToLoginButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        backToLoginButton.setForeground(TEAL_PRIMARY);
        backToLoginButton.setContentAreaFilled(false);
        backToLoginButton.setBorderPainted(false);
        backToLoginButton.setFocusPainted(false);
        backToLoginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void layoutComponents() {
        JPanel contentPane = new JPanel(new GridBagLayout());
        contentPane.setBackground(BG_LIGHT);
        
        JPanel cardPanel = new JPanel(new GridBagLayout());
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(25, 40, 25, 40)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        
        // Title
        JLabel titleLabel = new JLabel("Join Us", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        gbc.gridy = 0; gbc.insets = new Insets(0, 0, 20, 0); cardPanel.add(titleLabel, gbc);

        // Standard Fields
        String[] labels = {"Username", "First Name", "Last Name", "Email"};
        JComponent[] fields = {usernameField, firstNameField, lastNameField, emailField};

        int y = 1;
        for (int i = 0; i < labels.length; i++) {
            cardPanel.add(createLabel(labels[i]), getGbc(y++));
            cardPanel.add(fields[i], getGbc(y++));
        }

        // OTP Field (Horizontal Layout with Button)
        cardPanel.add(createLabel("Verification Code"), getGbc(y++));
        JPanel otpPanel = new JPanel(new BorderLayout(10, 0));
        otpPanel.setBackground(Color.WHITE);
        otpPanel.add(otpField, BorderLayout.CENTER);
        otpPanel.add(sendOtpButton, BorderLayout.EAST);
        gbc.gridy = y++; cardPanel.add(otpPanel, gbc);

        // Password Fields
        cardPanel.add(createLabel("Password"), getGbc(y++));
        cardPanel.add(passwordField, getGbc(y++));
        cardPanel.add(createLabel("Confirm Password"), getGbc(y++));
        cardPanel.add(confirmPasswordField, getGbc(y++));

        // Footer Buttons
        gbc.gridy = y++; gbc.insets = new Insets(25, 0, 10, 0); cardPanel.add(registerButton, gbc);
        gbc.gridy = y; gbc.insets = new Insets(5, 0, 0, 0); cardPanel.add(backToLoginButton, gbc);

        contentPane.add(cardPanel);
        add(contentPane);
    }

    private JLabel createLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(BOLD_FONT);
        l.setForeground(new Color(70, 70, 70));
        return l;
    }

    private GridBagConstraints getGbc(int y) {
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.gridx = 0;
        g.gridy = y;
        g.insets = new Insets(5, 0, 2, 0);
        return g;
    }

    // --- Styling Helpers ---
    private JTextField createStyledTextField() {
        JTextField f = new JTextField(20);
        f.setFont(MAIN_FONT);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(210, 210, 210)),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        return f;
    }

    private JPasswordField createStyledPasswordField() {
        JPasswordField f = new JPasswordField(20);
        f.setFont(MAIN_FONT);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(210, 210, 210)),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        return f;
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton b = new JButton(text);
        b.setFont(BOLD_FONT);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(10, 20, 10, 20));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    // --- Controller Accessors ---
    public JButton getRegisterButton() { return registerButton; }
    public JButton getBackToLoginButton() { return backToLoginButton; }
    public JButton getSendOtpButton() { return sendOtpButton; }
    public String getEmailInput() { return emailField.getText().trim(); }
    public String getOtpInput() { return otpField.getText().trim(); }
    public String getPasswordInput() { return new String(passwordField.getPassword()); }

    public User getUserDataFromInputs() {
        String username = usernameField.getText().trim();
        String password = getPasswordInput();
        String confirmPassword = new String(confirmPasswordField.getPassword());
        String email = getEmailInput();

        if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
            return null; 
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match!", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        User user = new User();
        user.setUsername(username);
        // user.setEmail(email); // Assuming your User model has this field
        return user;
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
            java.util.logging.Logger.getLogger(RegistrationView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(RegistrationView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(RegistrationView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(RegistrationView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form *
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new RegistrationView().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
*/