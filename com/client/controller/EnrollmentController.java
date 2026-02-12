package com.client.controller;

import com.client.remote.RMIClient;
import com.client.view.EnrollmentView;
import com.server.model.Enrollment;
import com.server.model.Student;
import com.server.model.Course;
import com.server.remote.EnrollmentServiceRemote;
import com.server.remote.StudentServiceRemote;
import com.server.remote.CourseServiceRemote;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Controller for managing Student Enrollments.
 * Synchronizes the Centralized View with RMI Remote Services.
 */
public class EnrollmentController implements ActionListener {

    private final EnrollmentView enrollmentView;
    private final EnrollmentServiceRemote enrollmentService;
    private final StudentServiceRemote studentService;
    private final CourseServiceRemote courseService;

    public EnrollmentController(EnrollmentView enrollmentView) {
        this.enrollmentView = enrollmentView;

        // Initialize RMI services via Singleton
        RMIClient rmiClient = RMIClient.getInstance();

        try {
            this.enrollmentService = rmiClient.getEnrollmentService();
            this.studentService = rmiClient.getStudentService();
            this.courseService = rmiClient.getCourseService();

            attachListeners();
            loadInitialData();
        } catch (RemoteException e) {
            JOptionPane.showMessageDialog(enrollmentView, "RMI Connection Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            throw new RuntimeException("RMI connection failed for EnrollmentController.", e);
        }
    }

    private void attachListeners() {
        this.enrollmentView.getAddEnrollmentButton().addActionListener(this);
        this.enrollmentView.getUpdateEnrollmentButton().addActionListener(this);
        this.enrollmentView.getDeleteEnrollmentButton().addActionListener(this);
        this.enrollmentView.getClearButton().addActionListener(this);
        this.enrollmentView.getBackButton().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == enrollmentView.getAddEnrollmentButton()) {
            handleAddEnrollment();
        } else if (source == enrollmentView.getUpdateEnrollmentButton()) {
            handleUpdateEnrollment();
        } else if (source == enrollmentView.getDeleteEnrollmentButton()) {
            handleDeleteEnrollment();
        } else if (source == enrollmentView.getClearButton()) {
            enrollmentView.clearFields();
        } else if (source == enrollmentView.getBackButton()) {
            enrollmentView.dispose();
        }
    }

    // --- Data Loading Logic ---

    private void loadInitialData() {
        loadComboBoxesAsync();
        loadEnrollmentsToTableAsync();
    }

    private void loadComboBoxesAsync() {
        new SwingWorker<Void, Void>() {
            List<Student> students;
            List<Course> courses;

            @Override
            protected Void doInBackground() throws Exception {
                // Fetching from RMI Server
                students = studentService.getAllStudents();
                courses = courseService.getAllCourses();
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    enrollmentView.populateStudentComboBox(students);
                    enrollmentView.populateCourseComboBox(courses);
                } catch (InterruptedException | ExecutionException e) {
                    showError("Sync Error: Could not load Student/Course lists", e);
                }
            }
        }.execute();
    }

    private void loadEnrollmentsToTableAsync() {
        new SwingWorker<List<Enrollment>, Void>() {
            @Override
            protected List<Enrollment> doInBackground() throws Exception {
                // This uses the JOIN FETCH logic we added to the Server
                return enrollmentService.getAllEnrollments();
            }

            @Override
            protected void done() {
                try {
                    enrollmentView.refreshEnrollmentTable(get());
                } catch (InterruptedException | ExecutionException e) {
                    showError("Table Sync Error", e);
                }
            }
        }.execute();
    }

    // --- Action Handlers ---

    private void handleAddEnrollment() {
        Enrollment newEnrollment = enrollmentView.getEnrollmentDataFromInputs();

        if (newEnrollment != null && validateFields(newEnrollment)) {
            setButtonsEnabled(false); // Prevent double submission
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    newEnrollment.setId(null); // Ensure DB generates new ID
                    enrollmentService.addEnrollment(newEnrollment);
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        get();
                        JOptionPane.showMessageDialog(enrollmentView, "Student Enrolled Successfully!");
                        loadEnrollmentsToTableAsync();
                        enrollmentView.clearFields();
                    } catch (Exception e) {
                        showError("Enrollment Failed", e);
                    } finally {
                        setButtonsEnabled(true);
                    }
                }
            }.execute();
        }
    }

    private void handleUpdateEnrollment() {
        Enrollment updated = enrollmentView.getEnrollmentDataFromInputs();

        // Verification: Ensure an ID exists (selected from table)
        if (updated != null && updated.getId() != null && updated.getId() > 0) {
            setButtonsEnabled(false);
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    enrollmentService.updateEnrollment(updated);
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        get();
                        JOptionPane.showMessageDialog(enrollmentView, "Update Complete.");
                        loadEnrollmentsToTableAsync();
                        enrollmentView.clearFields();
                    } catch (Exception e) {
                        showError("Update Failed", e);
                    } finally {
                        setButtonsEnabled(true);
                    }
                }
            }.execute();
        } else {
            JOptionPane.showMessageDialog(enrollmentView, "Please select an existing record from the table to update.", "Selection Required", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void handleDeleteEnrollment() {
        long enrollmentId = enrollmentView.getSelectedEnrollmentIdFromTable();

        if (enrollmentId > 0) {
            int confirm = JOptionPane.showConfirmDialog(enrollmentView,
                    "Are you sure you want to delete Enrollment ID: " + enrollmentId + "?",
                    "Confirm Deletion", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        enrollmentService.deleteEnrollment(enrollmentId);
                        return null;
                    }

                    @Override
                    protected void done() {
                        try {
                            get();
                            JOptionPane.showMessageDialog(enrollmentView, "Record Deleted.");
                            loadEnrollmentsToTableAsync();
                            enrollmentView.clearFields();
                        } catch (Exception e) {
                            showError("Deletion Failed", e);
                        }
                    }
                }.execute();
            }
        } else {
            JOptionPane.showMessageDialog(enrollmentView, "Selection Required: Click a row in the table first.", "Attention", JOptionPane.WARNING_MESSAGE);
        }
    }

    // --- Helper Logic ---

    private void setButtonsEnabled(boolean enabled) {
        enrollmentView.getAddEnrollmentButton().setEnabled(enabled);
        enrollmentView.getUpdateEnrollmentButton().setEnabled(enabled);
        enrollmentView.getDeleteEnrollmentButton().setEnabled(enabled);
    }

    private boolean validateFields(Enrollment enrollment) {
        if (enrollment.getStudent() == null || enrollment.getCourse() == null) {
            JOptionPane.showMessageDialog(enrollmentView, "Please select both a Student and a Course.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private void showError(String title, Exception e) {
        // Extracts the real cause (e.g., Database constraint) from the RMI wrapper
        Throwable cause = e.getCause() != null ? e.getCause() : e;
        JOptionPane.showMessageDialog(enrollmentView, title + "\nReason: " + cause.getMessage(), "Server Error", JOptionPane.ERROR_MESSAGE);
    }
}