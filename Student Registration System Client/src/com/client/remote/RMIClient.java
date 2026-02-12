package com.client.remote;

import com.server.remote.*;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;

public class RMIClient {

    private static final String HOST = "localhost";
    private static final int PORT = 4000;
    private static RMIClient instance; 

    private UserServiceRemote userService;
    private StudentServiceRemote studentService;
    private CourseServiceRemote courseService;
    private EnrollmentServiceRemote enrollmentService;
    private DepartmentServiceRemote departmentService;
    private ProgramServiceRemote programService;
    private RegistrationDetailsServiceRemote registrationDetailsService;

    // Private constructor for Singleton
    private RMIClient() throws RemoteException, NotBoundException, MalformedURLException {
        connect();
    }

    public static RMIClient getInstance() {
        if (instance == null) {
            synchronized (RMIClient.class) {
                if (instance == null) {
                    try {
                        instance = new RMIClient();
                    } catch (MalformedURLException | NotBoundException | RemoteException e) {
                        System.err.println("FATAL: Failed to connect to Server on port " + PORT);
                        throw new RuntimeException("RMI Client initialization failed.", e);
                    }
                }
            }
        }
        return instance;
    }

    public void connect() throws RemoteException, NotBoundException, MalformedURLException {
        String url = String.format("rmi://%s:%d/", HOST, PORT);
        System.out.println("Connecting to: " + url);
        
        // Lookup all published services
        userService = (UserServiceRemote) Naming.lookup(url + "UserService");
        studentService = (StudentServiceRemote) Naming.lookup(url + "StudentService");
        courseService = (CourseServiceRemote) Naming.lookup(url + "CourseService");
        enrollmentService = (EnrollmentServiceRemote) Naming.lookup(url + "EnrollmentService");
        departmentService = (DepartmentServiceRemote) Naming.lookup(url + "DepartmentService");
        programService = (ProgramServiceRemote) Naming.lookup(url + "ProgramService");
        registrationDetailsService = (RegistrationDetailsServiceRemote) Naming.lookup(url + "RegistrationDetailsService");

        System.out.println(">>> All RMI Services retrieved successfully.");
    }
    
    // --- Accessors with Automatic Reconnect ---

    public UserServiceRemote getUserService() throws RemoteException {
        try {
            // Test if the service is still alive
            userService.toString(); 
            return userService;
        } catch (Exception e) {
            // If connection lost, try to reconnect once
            System.out.println("UserService connection lost. Retrying...");
            try { connect(); return userService; } catch (MalformedURLException | NotBoundException | RemoteException ex) { throw new RemoteException("Server Offline"); }
        }
    }

    // Apply the same logic to other getters as needed
    public StudentServiceRemote getStudentService() throws RemoteException {
        if (studentService == null) throw new RemoteException("StudentService not initialized.");
        return studentService;
    }

    public CourseServiceRemote getCourseService() throws RemoteException {
        if (courseService == null) throw new RemoteException("CourseService not initialized.");
        return courseService;
    }

    public EnrollmentServiceRemote getEnrollmentService() throws RemoteException {
        if (enrollmentService == null) throw new RemoteException("EnrollmentService not initialized.");
        return enrollmentService;
    }

    public DepartmentServiceRemote getDepartmentService() throws RemoteException {
        if (departmentService == null) throw new RemoteException("DepartmentService not initialized.");
        return departmentService;
    }

    public ProgramServiceRemote getProgramService() throws RemoteException {
        if (programService == null) throw new RemoteException("ProgramService not initialized.");
        return programService;
    }

    public RegistrationDetailsServiceRemote getRegistrationService() throws RemoteException {
        if (registrationDetailsService == null) throw new RemoteException("RegistrationService not initialized.");
        return registrationDetailsService;
    }
}