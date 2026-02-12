package com.server.main;

import com.server.remote.*;
import com.server.service.*; 
import com.server.util.HibernateUtil;
import com.server.messaging.NotificationConsumer;
import java.net.MalformedURLException;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class ServerMain {

    private static final int RMI_PORT = 4000;

    public static void main(String[] args) {
        System.out.println("=====================================================");
        System.out.println("   STARTING STUDENT REGISTRATION SYSTEM SERVER...    ");
        System.out.println("=====================================================");

        // 1. Initializing Hibernate
        try {
            System.out.println("[1/4] Initializing Hibernate SessionFactory...");
            HibernateUtil.getSessionFactory();
            System.out.println("      DONE: Database connection established.");
        } catch (Exception e) {
            System.err.println("FATAL ERROR: Failed to initialize Hibernate. Check hibernate.cfg.xml.");
            e.printStackTrace();
            return;
        }

        // 2. Starting ActiveMQ Notification Listener
        try {
            System.out.println("[2/4] Initializing ActiveMQ Notification Service...");
            NotificationConsumer consumer = new NotificationConsumer();
            Thread messagingThread = new Thread(consumer);
            messagingThread.setDaemon(true); 
            messagingThread.start();
            System.out.println("      DONE: ActiveMQ Listener running in background.");
        } catch (Exception e) {
            System.err.println("WARNING: ActiveMQ could not start. OTP emails will not be sent.");
            System.err.println("Cause: " + e.getMessage());
        }

        // 3. Robust RMI Registry Initialization
        try {
            System.out.println("[3/4] Initializing RMI Registry on port " + RMI_PORT + "...");
            try {
                LocateRegistry.createRegistry(RMI_PORT);
                System.out.println("      DONE: New RMI Registry started.");
            } catch (RemoteException e) {
                // If the registry is already running, this catch prevents the crash
                System.out.println("      INFO: RMI Registry already active on port " + RMI_PORT + ". Reusing existing.");
            }

            // 4. Instantiating and Binding Services
            System.out.println("[4/4] Instantiating and Binding Services...");
            
            UserServiceRemote userService = new UserServiceRemoteImpl();
            StudentServiceRemote studentService = new StudentServiceRemoteImpl();
            EnrollmentServiceRemote enrollmentService = new EnrollmentServiceRemoteImpl();
            DepartmentServiceRemote departmentService = new DepartmentServiceRemoteImpl();
            ProgramServiceRemote programService = new ProgramServiceRemoteImpl();
            CourseServiceRemote courseService = new CourseServiceRemoteImpl();
            RegistrationDetailsServiceRemote registrationService = new RegistrationDetailsServiceRemoteImpl(); 

            String baseUrl = "rmi://localhost:" + RMI_PORT + "/";

            // Rebind replaces the old service if it was already bound
            Naming.rebind(baseUrl + "UserService", userService);
            Naming.rebind(baseUrl + "StudentService", studentService);
            Naming.rebind(baseUrl + "EnrollmentService", enrollmentService);
            Naming.rebind(baseUrl + "DepartmentService", departmentService);
            Naming.rebind(baseUrl + "ProgramService", programService);
            Naming.rebind(baseUrl + "CourseService", courseService);
            Naming.rebind(baseUrl + "RegistrationDetailsService", registrationService); 

            System.out.println("      DONE: All RMI Services successfully published.");
            System.out.println("\n<<< SERVER STATUS: READY >>>");
            System.out.println("Awaiting client connections on port " + RMI_PORT + "...");

        } catch (MalformedURLException | RemoteException e) {
            System.err.println("FATAL ERROR: RMI Server startup failed.");
            e.printStackTrace();
            HibernateUtil.shutdown(); 
        }
    }
}