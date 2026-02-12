package com.client.controller;

import com.client.remote.RMIClient;
import com.client.view.RegistrationDetailsView;
import com.server.model.RegistrationDetails;
import com.server.model.Student;
import com.server.remote.RegistrationDetailsServiceRemote;
import com.server.remote.StudentServiceRemote;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Controller for Student Registration Details.
 * Orchestrates the flow between the UI and multiple RMI services (Registration & Student).
 */
public class RegistrationDetailsController implements ActionListener {

    private final RegistrationDetailsView detailsView;
    private final RegistrationDetailsServiceRemote detailsService;
    private final StudentServiceRemote studentService;

    public RegistrationDetailsController(RegistrationDetailsView detailsView) {
        this.detailsView = detailsView;
        
        try {
            // Initialize services via Singleton
            RMIClient rmiClient = RMIClient.getInstance();
            this.detailsService = rmiClient.getRegistrationService();
            this.studentService = rmiClient.getStudentService();
            
            attachListeners();
            loadInitialData(); 
        } catch (RemoteException e) {
             JOptionPane.showMessageDialog(detailsView, "RMI Connection Error: " + e.getMessage(), "Connection Error", JOptionPane.ERROR_MESSAGE);
             throw new RuntimeException("RMI connection failed for RegistrationDetailsController", e);
        }
    }

    private void attachListeners() {
        // Using getBtn() naming convention
        detailsView.getAddBtn().addActionListener(this);         
        detailsView.getUpdateBtn().addActionListener(this);     
        detailsView.getDeleteBtn().addActionListener(this);     
        detailsView.getRefreshBtn().addActionListener(this);   
        detailsView.getClearBtn().addActionListener(this);
        detailsView.getBackBtn().addActionListener(this);

        // Table Selection Listener
        detailsView.getDetailsTable().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                handleTableSelection();
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == detailsView.getAddBtn()) { 
            handleAddDetails();
        } else if (source == detailsView.getUpdateBtn()) { 
            handleUpdateDetails();
        } else if (source == detailsView.getDeleteBtn()) { 
            handleDeleteDetails();
        } else if (source == detailsView.getRefreshBtn()) { 
            loadInitialData();
        } else if (source == detailsView.getClearBtn()) { 
             detailsView.clearFields(); 
        } else if (source == detailsView.getBackBtn()) {
             detailsView.dispose();
        }
    }

    private void handleTableSelection() {
        int row = detailsView.getDetailsTable().getSelectedRow();
        if (row != -1) {
            long id = (long) detailsView.getDetailsTable().getValueAt(row, 0);
            
            new SwingWorker<RegistrationDetails, Void>() {
                @Override
                protected RegistrationDetails doInBackground() throws Exception {
                    // Consistent with getDetailsById naming
                    return detailsService.getDetailsById(id);
                }
                @Override
                protected void done() {
                    try {
                        RegistrationDetails rd = get();
                        if (rd != null) detailsView.populateFields(rd);
                    } catch (Exception e) { showError("Selection failed", e); }
                }
            }.execute();
        }
    }

    // --- Data Loading ---

    private void loadInitialData() {
        loadDetailsAsync();
        loadStudentsAsync();
    }

    private void loadDetailsAsync() {
        new SwingWorker<List<RegistrationDetails>, Void>() {
            @Override
            protected List<RegistrationDetails> doInBackground() throws Exception {
                return detailsService.getAllDetails();
            }
            @Override
            protected void done() {
                try {
                    detailsView.refreshDetailsTable(get());
                    detailsView.clearFields();
                } catch (InterruptedException | ExecutionException e) { showError("Loading details failed", e); }
            }
        }.execute();
    }

    private void loadStudentsAsync() {
        new SwingWorker<List<Student>, Void>() {
            @Override
            protected List<Student> doInBackground() throws Exception {
                return studentService.getAllStudents(); 
            }
            @Override
            protected void done() {
                try {
                    detailsView.populateStudentComboBox(get());
                } catch (InterruptedException | ExecutionException e) { showError("Loading students failed", e); }
            }
        }.execute();
    }

    // --- Action Handlers ---

    private void handleAddDetails() {
        RegistrationDetails rd = detailsView.getDetailsDataFromInputs();
        if (rd == null) return; 

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                rd.setId(null); 
                detailsService.addDetails(rd); // Using save naming convention
                return null;
            }
            @Override
            protected void done() {
                try {
                    get();
                    JOptionPane.showMessageDialog(detailsView, "Enrollment completed successfully.");
                    loadInitialData(); 
                } catch (Exception ex) { showError("Registration failed", ex); }
            }
        }.execute();
    }

    private void handleUpdateDetails() {
        RegistrationDetails rd = detailsView.getDetailsDataFromInputs();
        if (rd == null || rd.getId() == null) {
            JOptionPane.showMessageDialog(detailsView, "Please select an entry from the table to update.");
            return;
        }

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                detailsService.updateDetails(rd); // Using update naming convention
                return null;
            }
            @Override
            protected void done() {
                try {
                    get();
                    JOptionPane.showMessageDialog(detailsView, "Record updated successfully.");
                    loadInitialData();
                } catch (Exception ex) { showError("Update failed", ex); }
            }
        }.execute();
    }

    private void handleDeleteDetails() {
        int row = detailsView.getDetailsTable().getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(detailsView, "Please select a record to delete.");
            return;
        }

        long id = (long) detailsView.getDetailsTable().getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(detailsView, 
            "Are you sure you want to delete Registration ID: " + id + "?", 
            "Confirm Deletion", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    detailsService.deleteDetails(id); // Using delete naming convention
                    return null;
                }
                @Override
                protected void done() {
                    try {
                        get();
                        loadInitialData(); 
                    } catch (Exception ex) { showError("Deletion failed", ex); }
                }
            }.execute();
        }
    }

    private void showError(String title, Exception e) {
        Throwable cause = (e.getCause() != null) ? e.getCause() : e;
        JOptionPane.showMessageDialog(detailsView, title + ": " + cause.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}