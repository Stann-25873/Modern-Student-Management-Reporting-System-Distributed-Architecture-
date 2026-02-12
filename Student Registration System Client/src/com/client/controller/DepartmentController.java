package com.client.controller;

import com.client.remote.RMIClient;
import com.client.view.DepartmentView;
import com.server.model.Department;
import com.server.remote.DepartmentServiceRemote;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Controller for managing Departments.
 * Synchronizes the modern DepartmentView with the remote RMI service.
 */
public class DepartmentController implements ActionListener {

    private final DepartmentView departmentView;
    private final DepartmentServiceRemote departmentService;

    public DepartmentController(DepartmentView departmentView) {
        this.departmentView = departmentView;
        
        // Initialize RMI service via Singleton
        RMIClient rmiClient = RMIClient.getInstance();
        
        try {
            this.departmentService = rmiClient.getDepartmentService();
            
            attachListeners();
            loadDepartmentsToTableAsync();
        } catch (RemoteException e) {
            JOptionPane.showMessageDialog(departmentView, 
                "Failed to connect to RMI Department Service: " + e.getMessage(), 
                "Connection Error", JOptionPane.ERROR_MESSAGE);
            throw new RuntimeException("RMI connection failed for DepartmentController", e);
        }
    }

    private void attachListeners() {
        this.departmentView.getAddBtn().addActionListener(this);
        this.departmentView.getUpdateBtn().addActionListener(this);
        this.departmentView.getDeleteBtn().addActionListener(this);
        this.departmentView.getRefreshBtn().addActionListener(this);
        this.departmentView.getBackBtn().addActionListener(this);
        this.departmentView.getClearBtn().addActionListener(this);

        // Table Selection Listener for auto-populating form
        this.departmentView.getDepartmentTable().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                handleTableSelection();
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == departmentView.getAddBtn()) {
            handleAddDepartment();
        } else if (source == departmentView.getUpdateBtn()) {
            handleUpdateDepartment();
        } else if (source == departmentView.getDeleteBtn()) {
            handleDeleteDepartment();
        } else if (source == departmentView.getRefreshBtn()) {
            loadDepartmentsToTableAsync();
        } else if (source == departmentView.getClearBtn()) {
            departmentView.clearFields();
        } else if (source == departmentView.getBackBtn()) {
            departmentView.dispose();
        }
    }

    private void handleTableSelection() {
        long deptId = departmentView.getSelectedDepartmentId();
        if (deptId == -1) return;

        // Fetch the object to ensure we have the latest state from server
        new SwingWorker<Department, Void>() {
            @Override
            protected Department doInBackground() throws Exception {
                // Assuming service has a getById or we build the object from the table
                return departmentService.getDepartmentById(deptId);
            }

            @Override
            protected void done() {
                try {
                    Department selected = get();
                    if (selected != null) {
                        departmentView.populateFields(selected);
                    }
                } catch (Exception e) {
                    showError("Selection Error", e);
                }
            }
        }.execute();
    }

    // --- Data Loading (Asynchronous) ---

    private void loadDepartmentsToTableAsync() {
        new SwingWorker<List<Department>, Void>() {
            @Override
            protected List<Department> doInBackground() throws Exception {
                return departmentService.getAllDepartments();
            }

            @Override
            protected void done() {
                try {
                    departmentView.refreshDepartmentTable(get());
                } catch (InterruptedException | ExecutionException e) {
                    showError("Error loading departments", e);
                }
            }
        }.execute();
    }

    // --- Action Handlers (Asynchronous CRUD) ---

    private void handleAddDepartment() {
        Department newDept = departmentView.getDepartmentDataFromInputs();
        
        if (newDept == null) {
            JOptionPane.showMessageDialog(departmentView, 
                "Please fill in both Name and Code.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                newDept.setId(null); // Ensure ID is null for a fresh save
                departmentService.addDepartment(newDept); 
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    JOptionPane.showMessageDialog(departmentView, "Department added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadDepartmentsToTableAsync();
                    departmentView.clearFields();
                } catch (Exception e) {
                    showError("Addition failed", e);
                }
            }
        }.execute();
    }

    private void handleUpdateDepartment() {
        Department updatedDept = departmentView.getDepartmentDataFromInputs();

        if (updatedDept == null || updatedDept.getId() == null) {
            JOptionPane.showMessageDialog(departmentView, 
                "Select a department from the table to update.", "Attention", JOptionPane.WARNING_MESSAGE);
            return;
        }

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                departmentService.updateDepartment(updatedDept);
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    JOptionPane.showMessageDialog(departmentView, "Department updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadDepartmentsToTableAsync();
                } catch (Exception e) {
                    showError("Update failed", e);
                }
            }
        }.execute();
    }

    private void handleDeleteDepartment() {
        long deptId = departmentView.getSelectedDepartmentId();
        
        if (deptId == -1) {
            JOptionPane.showMessageDialog(departmentView, "Please select a department to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(departmentView, 
            "Are you sure you want to delete Department ID: " + deptId + "?", 
            "Confirm Deletion", JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    departmentService.deleteDepartment(deptId);
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        get();
                        JOptionPane.showMessageDialog(departmentView, "Department deleted successfully!");
                        loadDepartmentsToTableAsync();
                        departmentView.clearFields();
                    } catch (Exception e) {
                        showError("Deletion failed", e);
                    }
                }
            }.execute();
        }
    }

    private void showError(String title, Exception e) {
        Throwable cause = (e.getCause() != null) ? e.getCause() : e;
        JOptionPane.showMessageDialog(departmentView, title + ": " + cause.getMessage(), "System Error", JOptionPane.ERROR_MESSAGE);
    }
}