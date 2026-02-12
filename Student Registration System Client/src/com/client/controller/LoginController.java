package com.client.controller;

import com.client.view.LoginView;
import com.client.view.MainDashboardView;
import com.client.view.RegistrationView;
import com.client.view.PasswordRecoveryView;
import com.client.remote.RMIClient;
import com.server.model.User;
import com.server.remote.UserServiceRemote;
import java.awt.HeadlessException;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.util.concurrent.ExecutionException;

public class LoginController implements ActionListener {

    private final LoginView loginView;
    private final RMIClient rmiClient;
    private final UserServiceRemote userService;

    public LoginController(LoginView loginView, RMIClient rmiClient) {
        this.loginView = loginView;
        this.rmiClient = rmiClient;

        try {
            this.userService = rmiClient.getUserService();
        } catch (RemoteException e) {
            JOptionPane.showMessageDialog(loginView, "RMI Connection Error: " + e.getMessage());
            throw new RuntimeException(e);
        }

        // Add Listeners
        loginView.getLoginButton().addActionListener(this);
        loginView.getRegisterButton().addActionListener(this);
        loginView.getForgotPasswordButton().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        
        if (source == loginView.getLoginButton()) {
            handleLogin();
        } else if (source == loginView.getRegisterButton()) {
            handleOpenRegistration();
        } else if (source == loginView.getForgotPasswordButton()) {
            handleForgotPassword();
        }
    }

    private void handleLogin() {
        String username = loginView.getUsername();
        String password = loginView.getPassword();

        if (username.isEmpty() || username.equals("Username")) {
            JOptionPane.showMessageDialog(loginView, "Username is required.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (password.isEmpty()) {
            JOptionPane.showMessageDialog(loginView, "Password is required.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Standard Password Login Flow
        new SwingWorker<User, Void>() {
            @Override
            protected User doInBackground() throws Exception {
                // Now strictly using standard authentication
                return userService.authenticate(username, password);
            }

            @Override
            protected void done() {
                try {
                    User authUser = get();
                    if (authUser != null) {
                        JOptionPane.showMessageDialog(loginView, "Welcome back,  " + authUser.getUsername() + " !");
                        loginView.dispose();
                        
                        MainDashboardView dashboardView = new MainDashboardView();
                        new DashboardController(dashboardView, rmiClient);
                        dashboardView.setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(loginView, "Invalid username or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (HeadlessException | InterruptedException | ExecutionException ex) {
                    JOptionPane.showMessageDialog(loginView, "Authentication Error: " + ex.getMessage());
                }
            }
        }.execute();
    }

    private void handleForgotPassword() {
        String username = loginView.getUsername();
        
        if (username.isEmpty() || username.equals("Username")) {
            JOptionPane.showMessageDialog(loginView, "Please enter your Username first to receive an OTP.", "Forgot Password", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        new SwingWorker<User, Void>() {
            @Override
            protected User doInBackground() throws Exception {
                // 1. Trigger OTP generation on server and ActiveMQ message
                userService.requestPasswordReset(username);
                
                // 2. Fetch user to get the email for the next view
                return userService.getAllUsers().stream()
                        .filter(u -> u.getUsername().equalsIgnoreCase(username))
                        .findFirst()
                        .orElse(null);
            }

            @Override
            protected void done() {
                try {
                    User user = get();
                    if (user != null) {
                        JOptionPane.showMessageDialog(loginView, "A verification code (OTP) has been sent to: " + user.getEmail());
                        
                        // Launch the Recovery View
                        PasswordRecoveryView recoveryView = new PasswordRecoveryView();
                        new PasswordRecoveryController(recoveryView, userService, user.getEmail());
                        recoveryView.setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(loginView, "User not found.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (HeadlessException | InterruptedException | ExecutionException ex) {
                    JOptionPane.showMessageDialog(loginView, "Reset failed: " + ex.getMessage());
                }
            }
        }.execute();
    }

    private void handleOpenRegistration() {
        try {
            RegistrationView regView = new RegistrationView();
            new RegistrationController(regView, rmiClient);
            loginView.dispose();
            regView.setVisible(true);
        } catch (RemoteException ex) {
            JOptionPane.showMessageDialog(loginView, "Error opening registration: " + ex.getMessage());
        }
    }
}