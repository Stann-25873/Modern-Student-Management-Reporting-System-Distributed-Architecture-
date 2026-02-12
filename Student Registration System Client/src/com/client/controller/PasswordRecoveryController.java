package com.client.controller;

import com.client.view.PasswordRecoveryView;
import com.client.view.MainDashboardView; // Assuming you have a dashboard
import com.server.remote.UserServiceRemote;
import com.server.model.User;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PasswordRecoveryController {

    private PasswordRecoveryView view;
    private UserServiceRemote userService;
    private String userEmail; // Passed from the Forgot Password request

    public PasswordRecoveryController(PasswordRecoveryView view, UserServiceRemote userService, String userEmail) {
        this.view = view;
        this.userService = userService;
        this.userEmail = userEmail;

        initController();
    }

    private void initController() {
        // Option A: Reset Password
        view.getResetBtn().addActionListener(e -> handlePasswordReset());

        // Option B: Quick Login with OTP
        view.getQuickLoginBtn().addActionListener(e -> handleQuickLogin());
    }

    private void handlePasswordReset() {
        String otp = view.getOtp();
        String newPass = view.getNewPassword();

        if (otp.isEmpty() || newPass.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Please enter both the OTP and a new password.");
            return;
        }

        try {
            boolean success = userService.resetPassword(userEmail, otp, newPass);
            if (success) {
                JOptionPane.showMessageDialog(view, "Password updated successfully! Please login with your new password.");
                view.dispose(); // Close recovery window
            } else {
                JOptionPane.showMessageDialog(view, "Invalid OTP. Please check ActiveMQ.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Error: " + ex.getMessage());
        }
    }

    private void handleQuickLogin() {
        String otp = view.getOtp();

        if (otp.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Please enter the OTP from ActiveMQ.");
            return;
        }

        try {
            // Option B logic: Get User object directly using OTP
            User user = userService.loginWithOTP(userEmail, otp);
            
            if (user != null) {
                JOptionPane.showMessageDialog(view, "OTP Verified! Access Granted.");
                view.dispose();
                
                // Open the Dashboard directly
                openDashboard(user);
            } else {
                JOptionPane.showMessageDialog(view, "Invalid OTP code.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Login failed: " + ex.getMessage());
        }
    }

    private void openDashboard(User user) {
        SwingUtilities.invokeLater(() -> {
            // Replace with your actual Dashboard class
            // new MainDashboardController(new MainDashboardView(), userService, user);
            System.out.println("Welcome " + user.getUsername() + "! Redirecting to Dashboard...");
        });
    }
}