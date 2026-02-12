package com.client.controller;

import com.client.view.ProgramView;
import com.client.remote.RMIClient;
import com.server.model.Program;
import com.server.model.Department;
import com.server.remote.ProgramServiceRemote;
import com.server.remote.DepartmentServiceRemote;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Controller for Program management.
 * Handles asynchronous RMI calls to manage Program data and Department relations.
 */
public class ProgramController implements ActionListener {

    private final ProgramView view;
    private final ProgramServiceRemote programService;
    private final DepartmentServiceRemote departmentService;

    public ProgramController(ProgramView view, RMIClient rmiClient) {
        this.view = view;
        try {
            // Initialize RMI services
            this.programService = (ProgramServiceRemote) rmiClient.getProgramService();
            this.departmentService = (DepartmentServiceRemote) rmiClient.getDepartmentService();

            attachListeners();
            loadInitialData();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "RMI Connection Error: " + e.getMessage(), 
                "Connection Error", JOptionPane.ERROR_MESSAGE);
            throw new RuntimeException("RMI connection failed.", e);
        }
    }

    private void attachListeners() {
        view.getAddProgramButton().addActionListener(this);
        view.getUpdateProgramButton().addActionListener(this);
        view.getDeleteProgramButton().addActionListener(this);
        view.getRefreshButton().addActionListener(this);
        view.getClearFieldsButton().addActionListener(this);
        view.getBackButton().addActionListener(this);

        // Selection listener for the JTable
        view.addProgramTableSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                handleTableSelection();
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == view.getAddProgramButton()) {
            handleAddProgram();
        } else if (source == view.getUpdateProgramButton()) {
            handleUpdateProgram();
        } else if (source == view.getDeleteProgramButton()) {
            handleDeleteProgram();
        } else if (source == view.getRefreshButton()) {
            loadInitialData();
        } else if (source == view.getClearFieldsButton()) {
            view.clearFields();
        } else if (source == view.getBackButton()) {
            view.dispose();
        }
    }

    private void handleTableSelection() {
        long programId = view.getSelectedProgramIdFromTable();
        if (programId == -1) return;

        new SwingWorker<Program, Void>() {
            @Override
            protected Program doInBackground() throws Exception {
                return programService.getProgramById(programId);
            }

            @Override
            protected void done() {
                try {
                    Program selected = get();
                    if (selected != null) {
                        view.populateFields(selected);
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    showError("Error fetching program details", ex);
                }
            }
        }.execute();
    }

    private void loadInitialData() {
        loadProgramTableAsync();
        loadDepartmentComboBoxAsync();
    }

    private void loadDepartmentComboBoxAsync() {
        new SwingWorker<List<Department>, Void>() {
            @Override
            protected List<Department> doInBackground() throws Exception {
                return departmentService.getAllDepartments();
            }

            @Override
            protected void done() {
                try {
                    view.populateDepartmentComboBox(get());
                } catch (Exception e) {
                    showError("Failed to load Departments", e);
                }
            }
        }.execute();
    }

    private void loadProgramTableAsync() {
        new SwingWorker<List<Program>, Void>() {
            @Override
            protected List<Program> doInBackground() throws Exception {
                return programService.getAllPrograms();
            }

            @Override
            protected void done() {
                try {
                    view.refreshProgramTable(get());
                    view.clearFields();
                } catch (Exception e) {
                    showError("Failed to refresh Program list", e);
                }
            }
        }.execute();
    }

    private void handleAddProgram() {
        Program program = view.getProgramDataFromInputs();
        if (program == null) return;

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                program.setId(null);
                programService.addProgram(program);
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    JOptionPane.showMessageDialog(view, "Program added successfully!");
                    loadInitialData();
                } catch (Exception ex) {
                    showError("Addition failed", ex);
                }
            }
        }.execute();
    }

    private void handleUpdateProgram() {
        Program program = view.getProgramDataFromInputs();
        if (program == null || program.getId() == null) {
            JOptionPane.showMessageDialog(view, "Please select a program to update.");
            return;
        }

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                programService.updateProgram(program);
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    JOptionPane.showMessageDialog(view, "Program updated successfully!");
                    loadInitialData();
                } catch (Exception ex) {
                    showError("Update failed", ex);
                }
            }
        }.execute();
    }

    private void handleDeleteProgram() {
        long programId = view.getSelectedProgramIdFromTable();
        if (programId <= 0) {
            JOptionPane.showMessageDialog(view, "Please select a program to delete.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(view, 
            "Delete Program ID: " + programId + "?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        
        if (confirm != JOptionPane.YES_OPTION) return;

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                programService.deleteProgram(programId);
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    JOptionPane.showMessageDialog(view, "Program deleted successfully!");
                    loadInitialData();
                } catch (Exception ex) {
                    showError("Deletion failed (Check for linked courses)", ex);
                }
            }
        }.execute();
    }

    private void showError(String title, Exception e) {
        String message = (e.getCause() != null) ? e.getCause().getMessage() : e.getMessage();
        JOptionPane.showMessageDialog(view, title + ": " + message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}