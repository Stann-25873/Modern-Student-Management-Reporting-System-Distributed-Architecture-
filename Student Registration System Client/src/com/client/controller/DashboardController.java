package com.client.controller;

import com.client.view.*;
import com.client.remote.RMIClient;
import com.client.util.ReportExporter;
import com.client.util.StudentReportExporter; // Import the new student exporter
import com.server.model.User;
import com.server.model.Student;

import javax.swing.*;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class DashboardController implements ActionListener {

    private final MainDashboardView dashboardView;
    private final RMIClient rmiClient;

    public DashboardController(MainDashboardView dashboardView, RMIClient rmiClient) {
        this.dashboardView = dashboardView;
        this.rmiClient = rmiClient;
        attachListeners();
        dashboardView.setVisible(true);
    }

    private void attachListeners() {
        dashboardView.getManageDepartmentsButton().addActionListener(this);
        dashboardView.getManageProgramsButton().addActionListener(this);
        dashboardView.getManageCoursesButton().addActionListener(this);
        dashboardView.getManageStudentsButton().addActionListener(this);
        dashboardView.getManageEnrollmentGradesButton().addActionListener(this);
        dashboardView.getLogoutButton().addActionListener(this);
        dashboardView.getUserManagementButton().addActionListener(this);
        
        // Note: You can decide if these buttons export Users or Students.
        // For now, I'll keep them as User exports, but you can add specific "Export Student" buttons.
        dashboardView.getExportPdfButton().addActionListener(this);
        dashboardView.getExportExcelButton().addActionListener(this);
        dashboardView.getExportCsvButton().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        try {
            if (source == dashboardView.getManageDepartmentsButton()) openDepartmentManagement();
            else if (source == dashboardView.getManageProgramsButton()) openProgramManagement();
            else if (source == dashboardView.getManageCoursesButton()) openCourseManagement();
            else if (source == dashboardView.getManageStudentsButton()) openStudentManagement();
            else if (source == dashboardView.getManageEnrollmentGradesButton()) openRegistrationDetailsManagement();
            else if (source == dashboardView.getUserManagementButton()) openUserManagement();
            else if (source == dashboardView.getLogoutButton()) handleLogout();
            
            // Example: Basic buttons export "USER" data
            else if (source == dashboardView.getExportPdfButton()) handleExport("PDF", "USER");
            else if (source == dashboardView.getExportExcelButton()) handleExport("EXCEL", "USER");
            else if (source == dashboardView.getExportCsvButton()) handleExport("CSV", "USER");
            
        } catch (Exception ex) {
            showError("System Error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    // --- Navigation Logic ---
    private void openDepartmentManagement() throws RemoteException {
        DepartmentView deptView = new DepartmentView();
        new DepartmentController(deptView); 
        deptView.setVisible(true);
    }

    private void openProgramManagement() {
        ProgramView programView = new ProgramView(this.dashboardView);
        new ProgramController(programView, this.rmiClient);
        programView.setVisible(true);
    }

    private void openCourseManagement() throws RemoteException {
        CourseView courseView = new CourseView(); 
        new CourseController(courseView, this.rmiClient);
        courseView.setVisible(true);
    }

    private void openStudentManagement() throws RemoteException {
        StudentView studentView = new StudentView(this.dashboardView);
        new StudentController(studentView); 
        studentView.setVisible(true);
    }

    private void openRegistrationDetailsManagement() throws RemoteException {
        RegistrationDetailsView detailsView = new RegistrationDetailsView();
        new RegistrationDetailsController(detailsView);
        detailsView.setVisible(true);
    }

    private void openUserManagement() throws RemoteException {
        UserManagementView userView = new UserManagementView();
        new UserController(userView);
        userView.setVisible(true);
    }

    // --- Unified Action Handler for Exports ---
    private void handleExport(final String format, final String dataType) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save " + dataType + " " + format + " Report");
        
        if (chooser.showSaveDialog(dashboardView) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        final String path = chooser.getSelectedFile().getAbsolutePath();

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                String finalPath;

                if ("STUDENT".equals(dataType)) {
                    List<Student> students = rmiClient.getStudentService().getAllStudents();
                    if (students == null || students.isEmpty()) throw new Exception("No student data found.");
                    
                    switch (format) {
                        case "PDF":
                            finalPath = path.toLowerCase().endsWith(".pdf") ? path : path + ".pdf";
                            StudentReportExporter.exportToPDF(students, finalPath);
                            break;
                        case "EXCEL":
                            finalPath = path.toLowerCase().endsWith(".xlsx") ? path : path + ".xlsx";
                            StudentReportExporter.exportToExcel(students, finalPath);
                            break;
                        default:
                            throw new IllegalArgumentException("CSV not yet implemented for Students");
                    }
                } else {
                    // USER Export Logic
                    List<User> users = rmiClient.getUserService().getAllUsers();
                    if (users == null || users.isEmpty()) throw new Exception("No user data found.");

                    switch (format) {
                        case "PDF":
                            finalPath = path.toLowerCase().endsWith(".pdf") ? path : path + ".pdf";
                            ReportExporter.exportToPDF(users, finalPath);
                            break;
                        case "EXCEL":
                            finalPath = path.toLowerCase().endsWith(".xlsx") ? path : path + ".xlsx";
                            ReportExporter.exportToExcel(users, finalPath);
                            break;
                        case "CSV":
                            finalPath = path.toLowerCase().endsWith(".csv") ? path : path + ".csv";
                            ReportExporter.exportToCSV(users, finalPath);
                            break;
                        default:
                            throw new IllegalArgumentException("Unknown format: " + format);
                    }
                }
                return null;
            }

            @Override
            protected void done() {
                try {
                    get(); 
                    JOptionPane.showMessageDialog(dashboardView, dataType + " " + format + " Export successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (HeadlessException | InterruptedException | ExecutionException ex) {
                    String errorMsg = (ex.getCause() != null) ? ex.getCause().getMessage() : ex.getMessage();
                    showError("Export failed: " + errorMsg);
                }
            }
        }.execute();
    }

    private void handleLogout() {
        dashboardView.dispose();
        LoginView loginView = new LoginView();
        new LoginController(loginView, rmiClient);
        loginView.setVisible(true);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(dashboardView, message, "System Error", JOptionPane.ERROR_MESSAGE);
    }
}