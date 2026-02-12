package com.server.remote;

import com.server.model.User;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface UserServiceRemote extends Remote {
    
    // Constant for easier lookups in RMIClient
    String RMI_SERVICE_NAME = "UserService";

    // --- 1. AUTHENTICATION & REGISTRATION ---
    
    User authenticate(String username, String password) throws RemoteException;
    
    User registerUser(User newUser, String firstName, String lastName, String email) throws RemoteException;

    // --- 2. OTP & MESSAGING (ActiveMQ Integration) ---

    void initiateRegistration(String email) throws RemoteException;

    boolean verifyOTP(String email, String enteredOtp) throws RemoteException;

    /**
     * OPTION B: Allows a user to access the system immediately if the OTP is valid.
     * Returns the full User object to the client for the Dashboard.
     */
    User loginWithOTP(String email, String enteredOtp) throws RemoteException;

    /**
     * OPTION A: Permanently updates the password in the database after OTP is verified.
     */
    boolean resetPassword(String email, String otp, String newPassword) throws RemoteException;

    // --- 3. CRUD OPERATIONS (Hibernate Integration) ---
    
    void addUser(User user) throws RemoteException;
    
    void updateUser(User user) throws RemoteException;
    
    void deleteUser(long userId) throws RemoteException;
    
    User getUserById(long userId) throws RemoteException;
     
    void requestPasswordReset(String username) throws RemoteException;   
    
    List<User> getAllUsers() throws RemoteException;
}