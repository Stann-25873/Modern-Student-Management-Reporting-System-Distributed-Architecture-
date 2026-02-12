package com.client.main;

import com.client.remote.RMIClient;
import com.client.controller.LoginController;
import com.client.view.LoginView;
import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;

public class ClientMain {

    public static void main(String[] args) { 
        System.out.println("=====================================================");
        System.out.println("   STARTING STUDENT REGISTRATION SYSTEM CLIENT...    ");
        System.out.println("=====================================================");

        // 1. Initializing RMI Connection via Singleton
        // We do this outside the EDT to ensure the server is reachable before opening the UI
        try {
            System.out.print("[1/2] Connecting to RMI Server on port 4000... ");
            
            // This triggers the connect() method inside RMIClient internally
            RMIClient rmiClient = RMIClient.getInstance(); 
            
            System.out.println("DONE.");
            System.out.println(">>> Connection established successfully.");
            
        } catch (RuntimeException e) {
            System.err.println("\nFATAL ERROR: Could not connect to the server.");
            JOptionPane.showMessageDialog(
                null, 
                "Communication Error: The RMI Server is not responding on port 4000.\n" +
                "Please ensure the Server is running first.", 
                "Connection Failed", 
                JOptionPane.ERROR_MESSAGE
            );
            // e.printStackTrace();
            System.exit(1);
        }

        // 2. Launching Swing UI on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            try {
                System.out.println("[2/2] Launching Login Interface...");
                
                // Initialize the View
                LoginView loginView = new LoginView();
                
                // Initialize the Controller with the shared RMI instance
                // This matches the LoginController(LoginView, RMIClient) constructor
                new LoginController(loginView, RMIClient.getInstance()); 

                // Show the window
                loginView.setVisible(true);
                System.out.println("<<< CLIENT APPLICATION READY >>>");

            } catch (Exception e) {
                System.err.println("Critical GUI Launch Error: " + e.getMessage());
                e.printStackTrace();
                System.exit(1); 
            }
        });
    }
}