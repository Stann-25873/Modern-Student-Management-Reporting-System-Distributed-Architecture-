package com.client.controller;

import com.client.view.RegistrationView;
import com.client.view.LoginView;
import com.client.remote.RMIClient;
import com.server.model.User;
import com.server.remote.UserServiceRemote;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;

public class RegistrationController implements ActionListener {

    private final RegistrationView registrationView;
    private final UserServiceRemote userService;
    private final RMIClient rmiClient;

    public RegistrationController(RegistrationView registrationView, RMIClient rmiClient) throws RemoteException {
        this.registrationView = registrationView;
        this.rmiClient = rmiClient;
        this.userService = rmiClient.getUserService();
        
        this.registrationView.getSendOtpButton().addActionListener(this);
        this.registrationView.getRegisterButton().addActionListener(this);
        this.registrationView.getBackToLoginButton().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == registrationView.getSendOtpButton()) {
            handleRequestOtp();
        } else if (source == registrationView.getRegisterButton()) {
            handleSignUp();
        } else if (source == registrationView.getBackToLoginButton()) {
            navigateToLogin();
        }
    }

    private void handleRequestOtp() {
        String email = registrationView.getEmailInput();
        if (email == null || email.trim().isEmpty() || email.equals("Email Address")) {
            JOptionPane.showMessageDialog(registrationView, "Please enter a valid email to receive a verification code.");
            return;
        }

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                userService.initiateRegistration(email);
                return null;
            }
            @Override
            protected void done() {
                try {
                    get();
                    JOptionPane.showMessageDialog(registrationView, "OTP sent! Check your inbox.");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(registrationView, "Messaging Error: Check if ActiveMQ and Server are running.");
                }
            }
        }.execute();
    }

    private void handleSignUp() {
        // 1. Gather inputs
        User u = registrationView.getUserDataFromInputs();
        String pass = registrationView.getPasswordInput();
        String otp = registrationView.getOtpInput();
        String email = registrationView.getEmailInput();
        
        // 2. Comprehensive Validation to prevent Database Constraint Errors
        if (u == null || email.isEmpty() || email.equals("Email Address") || pass.isEmpty() || otp.isEmpty()) {
            JOptionPane.showMessageDialog(registrationView, "Registration Failed: All fields are required.");
            return;
        }

        // 3. Sync User Object with Mandatory DB Columns
        u.setEmail(email);
        u.setUserRole("STUDENT"); // Hardcoded role to satisfy NOT NULL 'user_role'
        
        // If your RegistrationView doesn't have names, set defaults to avoid nulls
        if (u.getFirstName() == null) u.setFirstName("Student"); 
        if (u.getLastName() == null) u.setLastName("User");

        new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                // Verify OTP first
                if (userService.verifyOTP(email, otp)) {
                    u.setPasswordHash(pass); // Password will be hashed by addUser on the server
                    userService.addUser(u); // Final database insert
                    return true;
                }
                return false;
            }
            @Override
            protected void done() {
                try {
                    if (get()) {
                        JOptionPane.showMessageDialog(registrationView, "Success! Account created for " + u.getUsername());
                        navigateToLogin();
                    } else {
                        JOptionPane.showMessageDialog(registrationView, "Invalid OTP code. Please try again.");
                    }
                } catch (Exception ex) {
                    // This will now show the exact column causing the crash
                    String detail = (ex.getCause() != null) ? ex.getCause().getMessage() : ex.getMessage();
                    JOptionPane.showMessageDialog(registrationView, "Registration Crashed: " + detail);
                }
            }
        }.execute();
    }

    private void navigateToLogin() {
        registrationView.dispose();
        LoginView lv = new LoginView();
        new LoginController(lv, rmiClient);
        lv.setVisible(true);
    }
}