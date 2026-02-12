package com.client.controller;

import com.client.remote.RMIClient;
import com.client.view.StudentView;
import com.server.model.Student;
import com.server.model.Program;
import com.server.model.Department;
import com.server.remote.StudentServiceRemote;
import com.server.remote.ProgramServiceRemote;
import com.server.remote.DepartmentServiceRemote;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.util.List;

public class StudentController implements ActionListener {

    private final StudentView studentView;
    private final StudentServiceRemote studentService;
    private final ProgramServiceRemote programService;
    private final DepartmentServiceRemote departmentService;

    public StudentController(StudentView studentView) {
        this.studentView = studentView;
        RMIClient rmiClient = RMIClient.getInstance(); 
        
        try {
            this.studentService = rmiClient.getStudentService();
            this.programService = rmiClient.getProgramService(); 
            this.departmentService = rmiClient.getDepartmentService(); 
            
            attachListeners();
            loadInitialData(); 
        } catch (RemoteException e) {
            JOptionPane.showMessageDialog(studentView, "Connection Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            throw new RuntimeException("RMI failed", e);
        }
    }

    private void attachListeners() {
        studentView.getRegisterButton().addActionListener(this); 
        studentView.getUpdateButton().addActionListener(this);  
        studentView.getDeleteButton().addActionListener(this);  
        studentView.getClearButton().addActionListener(this);  
        studentView.getBackButton().addActionListener(this);    
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == studentView.getRegisterButton()) {
            handleAddStudent(); 
        } else if (source == studentView.getUpdateButton()) {
            handleUpdateStudent(); 
        } else if (source == studentView.getDeleteButton()) {
            handleDeleteStudent(); 
        } else if (source == studentView.getClearButton()) {
            studentView.clearFields(); 
        } else if (source == studentView.getBackButton()) {
            studentView.dispose();
        }
    }

    private void loadInitialData() {
        loadStudentsToTableAsync();
        loadComboBoxesAsync();
    }

    private void loadComboBoxesAsync() {
        new SwingWorker<Void, Void>() {
            List<Program> progs;
            List<Department> depts;

            @Override
            protected Void doInBackground() throws Exception {
                progs = programService.getAllPrograms();
                depts = departmentService.getAllDepartments();
                return null;
            }

            @Override
            protected void done() {
                try {
                    get(); 
                    studentView.populateProgramComboBox(progs);
                    studentView.populateDepartmentComboBox(depts);
                } catch (Exception e) {
                    showError("Error loading ComboBox data", e);
                }
            }
        }.execute();
    }

    private void loadStudentsToTableAsync() {
        new SwingWorker<List<Student>, Void>() {
            @Override
            protected List<Student> doInBackground() throws Exception {
                return studentService.getAllStudents();
            }

            @Override
            protected void done() {
                try {
                    studentView.refreshStudentTable(get());
                    studentView.clearFields();
                } catch (Exception e) {
                    showError("Error loading students", e);
                }
            }
        }.execute();
    }

    private void handleAddStudent() {
        Student s = studentView.getStudentDataFromInputs();
        if (s != null && validate(s)) {
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    s.setId(null);
                    studentService.addStudent(s);
                    return null;
                }
                @Override
                protected void done() {
                    try {
                        get();
                        JOptionPane.showMessageDialog(studentView, "Student registered successfully.");
                        loadInitialData();
                    } catch (Exception e) { showError("Registration failed", e); }
                }
            }.execute();
        }
    }

    private void handleUpdateStudent() {
        Student s = studentView.getStudentDataFromInputs();
        if (s != null && s.getId() != null && validate(s)) {
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    studentService.updateStudent(s);
                    return null;
                }
                @Override
                protected void done() {
                    try {
                        get();
                        JOptionPane.showMessageDialog(studentView, "Student updated successfully.");
                        loadInitialData();
                    } catch (Exception e) { showError("Update failed", e); }
                }
            }.execute();
        }
    }

  private void handleDeleteStudent() {
    // 1. Retrieve the selected ID from the view
    long studentId = studentView.getSelectedStudentIdFromTable(); 

    // 2. Validate that an ID was actually selected
    if (studentId > 0) {
        // 3. Request user confirmation before proceeding
        int confirm = JOptionPane.showConfirmDialog(
            studentView, 
            "Are you sure you want to delete student (ID: " + studentId + ")?", 
            "Confirm Deletion", 
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            // 4. Perform the deletion asynchronously to keep the UI responsive
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    studentService.deleteStudent(studentId);
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        get(); // Check if an exception occurred during the background task
                        JOptionPane.showMessageDialog(studentView, "Student deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                        
                        // 5. Refresh the UI
                        loadStudentsToTableAsync();
                        studentView.clearFields(); 
                    } catch (Exception e) {
                        // Use the centralized error handler to show the RMI or SQL cause
                        showError("Deletion failed", e);
                    }
                }
            }.execute();
        }
    } else {
        // Notify the user if they clicked delete without selecting a row
        JOptionPane.showMessageDialog(studentView, "Please select a student from the table to delete.", "Attention", JOptionPane.WARNING_MESSAGE);
    }
}

    private boolean validate(Student s) {
        if (s.getFirstName().isEmpty() || s.getLastName().isEmpty() || s.getEmail().isEmpty()) {
            JOptionPane.showMessageDialog(studentView, "All mandatory fields must be filled.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private void showError(String title, Exception e) {
        String msg = (e.getCause() != null) ? e.getCause().getMessage() : e.getMessage();
        JOptionPane.showMessageDialog(studentView, title + ": " + msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}