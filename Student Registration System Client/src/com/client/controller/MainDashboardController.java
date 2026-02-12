package com.client.controller;

import com.client.view.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainDashboardController {
    private MainDashboardView view;

    public MainDashboardController(MainDashboardView view) {
        this.view = view;
        initController();
    }

    private void initController() {
        // Navigation to Programs
        view.getManageProgramsButton().addActionListener(e -> openProgramManagement());

        // Navigation to Registration Details (Enrollment)
        view.getManageEnrollmentGradesButton().addActionListener(e -> openRegistrationDetails());

        // Logout Logic
        view.getLogoutButton().addActionListener(e -> handleLogout());
        
        // Add other listeners here (Departments, Students, etc.)
    }

    private void openProgramManagement() {
        ProgramView pView = new ProgramView(view);
        // Note: You'll need to pass your RMI services to the controller
        // new ProgramController(pView, programService, departmentService);
        pView.setVisible(true);
    }

    private void openRegistrationDetails() {
        RegistrationDetailsView rdView = new RegistrationDetailsView();
        // new RegistrationDetailsController(rdView, registrationService, studentService);
        rdView.setVisible(true);
    }

    private void handleLogout() {
        view.dispose();
        new LoginView().setVisible(true);
    }
}