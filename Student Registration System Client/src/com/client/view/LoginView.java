package com.client.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

/**
 * Enhanced Login View with Secure Access UI.
 * Integrates with the Teal/Corporate theme.
 */
public class LoginView extends JFrame {

    private static final long serialVersionUID = 1L;

    // --- Components ---
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private JButton forgotPasswordButton;

    // --- Custom Theme Colors ---
    private static final Color TEAL_PRIMARY = new Color(38, 166, 154);
    private static final Color TEAL_DARK    = new Color(0, 105, 92);
    private static final Color BG_TEAL      = new Color(58, 122, 110);
    private static final Color TEXT_GRAY    = new Color(117, 117, 117);
    
    private static final Font TITLE_FONT    = new Font("Segoe UI", Font.BOLD, 28);
    private static final Font MAIN_FONT     = new Font("Segoe UI", Font.PLAIN, 15);
    private static final Font LABEL_FONT    = new Font("Segoe UI Semibold", Font.PLAIN, 13);

    public LoginView() {
        setupFrame();
        initComponents();
        layoutComponents();
    }

    private void setupFrame() {
        setTitle("University System | Secure Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700); 
        setMinimumSize(new Dimension(850, 650));
        setResizable(false);
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        usernameField = new JTextField();
        setupPlaceholder(usernameField, "Enter Username or Email");
        styleField(usernameField);

        passwordField = new JPasswordField();
        setupPlaceholder(passwordField, "Password");
        styleField(passwordField);

        // --- Action Buttons ---
        forgotPasswordButton = new JButton("Forgot Password? Reset Here");
        styleLinkButton(forgotPasswordButton, 12, Font.ITALIC);

        loginButton = new JButton("Login to System");
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        loginButton.setBackground(TEAL_PRIMARY);
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setBorder(new EmptyBorder(12, 0, 12, 0));
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        registerButton = new JButton("New User? Create an Account");
        styleLinkButton(registerButton, 14, Font.BOLD);
        registerButton.setForeground(TEAL_DARK);
    }

    private void layoutComponents() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(BG_TEAL);

        // Center Card
        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setPreferredSize(new Dimension(420, 580));
        cardPanel.setMaximumSize(new Dimension(420, 580));
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 0, 0, 30), 1),
            BorderFactory.createEmptyBorder(50, 45, 50, 45)
        ));

        // Header Section
        JLabel titleLabel = new JLabel("SECURE LOGIN");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(TEAL_DARK);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Student Management Portal");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(TEXT_GRAY);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        cardPanel.add(titleLabel);
        cardPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        cardPanel.add(subtitleLabel);
        cardPanel.add(Box.createRigidArea(new Dimension(0, 40)));
        
        // Input Section
        addLabeledComponent(cardPanel, "Username / Institutional Email", usernameField);
        cardPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        addLabeledComponent(cardPanel, "Password", passwordField);
        
        // --- Centralized Forgot Password ---
        cardPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        forgotPasswordButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(forgotPasswordButton);
        
        cardPanel.add(Box.createRigidArea(new Dimension(0, 35)));

        // Login Action
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setMaximumSize(new Dimension(330, 50));
        cardPanel.add(loginButton);

        // Registration Footer
        cardPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(registerButton);

        mainPanel.add(cardPanel);
        add(mainPanel);
    }

    private void addLabeledComponent(JPanel panel, String labelText, JComponent comp) {
        JLabel label = new JLabel(labelText);
        label.setFont(LABEL_FONT);
        label.setForeground(new Color(70, 70, 70));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Changed FlowLayout to CENTER to keep label consistent with centered theme
        JPanel labelAligner = new JPanel(new FlowLayout(FlowLayout.CENTER));
        labelAligner.setOpaque(false);
        labelAligner.setMaximumSize(new Dimension(330, 25));
        labelAligner.add(label);
        
        panel.add(labelAligner);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        comp.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(comp);
    }

    private void styleField(JTextField field) {
        field.setFont(MAIN_FONT);
        field.setPreferredSize(new Dimension(330, 45));
        field.setMaximumSize(new Dimension(330, 45));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
    }

    private void styleLinkButton(JButton btn, int size, int style) {
        btn.setFont(new Font("Segoe UI", style, size));
        btn.setForeground(TEXT_GRAY);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void setupPlaceholder(JTextField field, String hint) {
        field.setText(hint);
        field.setForeground(Color.LIGHT_GRAY);
        if (field instanceof JPasswordField) {
            ((JPasswordField) field).setEchoChar((char) 0);
        }

        field.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(hint)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                    if (field instanceof JPasswordField) {
                        ((JPasswordField) field).setEchoChar('●');
                    }
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(hint);
                    field.setForeground(Color.LIGHT_GRAY);
                    if (field instanceof JPasswordField) {
                        ((JPasswordField) field).setEchoChar((char) 0);
                    }
                }
            }
        });
    }

    public boolean validateInputs() {
        if (getUsername().isEmpty() || getPassword().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter username and password.", "Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    public String getUsername() {
        String user = usernameField.getText().trim();
        return (user.equals("Enter Username or Email")) ? "" : user;
    }

    public String getPassword() {
        return new String(passwordField.getPassword());
    }

    public JButton getLoginButton() { return loginButton; }
    public JButton getRegisterButton() { return registerButton; }
    public JButton getForgotPasswordButton() { return forgotPasswordButton; }
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
            java.util.logging.Logger.getLogger(LoginView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(LoginView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(LoginView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(LoginView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form *
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new LoginView().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}*/
