
package com.client.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class PasswordRecoveryView extends JFrame {
    private JTextField otpField;
    private JPasswordField newPasswordField;
    private JButton resetPasswordButton;
    private JButton quickLoginButton; // The "Access with OTP" option

    private static final Color TEAL_PRIMARY = new Color(45, 126, 114);

    public PasswordRecoveryView() {
        setTitle("Account Recovery");
        setSize(400, 450);
        setLocationRelativeTo(null);
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));
        panel.setBackground(Color.WHITE);

        otpField = new JTextField();
        newPasswordField = new JPasswordField();
        resetPasswordButton = new JButton("Update Password");
        quickLoginButton = new JButton("Login with OTP Only");

        // Styling
        styleBtn(resetPasswordButton, TEAL_PRIMARY, Color.WHITE);
        styleBtn(quickLoginButton, Color.LIGHT_GRAY, Color.BLACK);

        panel.add(new JLabel("Please,Enter Code from ActiveMQ:"));
        panel.add(otpField);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(new JLabel("New Password (Optional for quick login):"));
        panel.add(newPasswordField);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(resetPasswordButton);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(quickLoginButton);

        add(panel);
    }

    private void styleBtn(JButton b, Color bg, Color fg) {
        b.setBackground(bg);
        b.setForeground(fg);
        b.setAlignmentX(Component.CENTER_ALIGNMENT);
        b.setMaximumSize(new Dimension(250, 40));
    }

    // Getters for your Controller
    public String getOtp() { return otpField.getText().trim(); }
    public String getNewPassword() { return new String(newPasswordField.getPassword()); }
    public JButton getResetBtn() { return resetPasswordButton; }
    public JButton getQuickLoginBtn() { return quickLoginButton; }
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
        /* Set the Nimbus look and feel */
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
            java.util.logging.Logger.getLogger(PasswordRecoveryView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PasswordRecoveryView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PasswordRecoveryView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PasswordRecoveryView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form *
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new PasswordRecoveryView().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}*/
