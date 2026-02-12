package com.client.controller;

import com.client.remote.RMIClient;
import com.client.view.UserManagementView;
import com.server.model.User;
import com.server.remote.UserServiceRemote;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class UserController implements ActionListener {

    private final UserManagementView userView;
    private final UserServiceRemote userService;

    public UserController(UserManagementView userView) {
        this.userView = userView;
        try {
            this.userService = RMIClient.getInstance().getUserService();
            attachListeners();
            loadUsersToTableAsync();
        } catch (RemoteException e) {
            showError("RMI Connection Failed", e);
            throw new RuntimeException(e);
        }
    }

    private void attachListeners() {
        userView.getAddBtn().addActionListener(this);
        userView.getUpdateBtn().addActionListener(this);
        userView.getDeleteBtn().addActionListener(this);
        userView.getRefreshBtn().addActionListener(this);
        userView.getClearBtn().addActionListener(this);
        userView.getBackBtn().addActionListener(this);

        // Selection Listener to populate form fields when a row is clicked
        userView.getUserTable().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                handleTableSelection();
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == userView.getAddBtn()) handleAddUser();
        else if (source == userView.getUpdateBtn()) handleUpdateUser();
        else if (source == userView.getDeleteBtn()) handleDeleteUser();
        else if (source == userView.getRefreshBtn()) loadUsersToTableAsync();
        else if (source == userView.getClearBtn()) userView.clearFields();
        else if (source == userView.getBackBtn()) userView.dispose();
    }

    private void handleTableSelection() {
        int row = userView.getUserTable().getSelectedRow();
        if (row != -1) {
            long id = (long) userView.getUserTable().getValueAt(row, 0);
            new SwingWorker<User, Void>() {
                @Override protected User doInBackground() throws Exception {
                    return userService.getUserById(id);
                }
                @Override protected void done() {
                    try { userView.populateFields(get()); } catch (Exception ex) { /* Silent */ }
                }
            }.execute();
        }
    }

    private void loadUsersToTableAsync() {
        new SwingWorker<List<User>, Void>() {
            @Override protected List<User> doInBackground() throws Exception {
                return userService.getAllUsers();
            }
            @Override protected void done() {
                try {
                    userView.refreshUserTable(get());
                } catch (InterruptedException | ExecutionException e) {
                    showError("Error loading users", e);
                }
            }
        }.execute();
    }

    private void handleAddUser() {
        User user = userView.getUserDataFromInputs();
        if (user == null) return;

        new SwingWorker<Void, Void>() {
            @Override protected Void doInBackground() throws Exception {
                userService.addUser(user);
                return null;
            }
            @Override protected void done() {
                try {
                    get();
                    JOptionPane.showMessageDialog(userView, "User created successfully!");
                    loadUsersToTableAsync();
                } catch (Exception e) { showError("Creation failed", e); }
            }
        }.execute();
    }
    
    private void handleUpdateUser() {
    // 1. Get the ID directly from the selected table row for absolute certainty
    int selectedRow = userView.getUserTable().getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(userView, "Please select a user from the table to update.", 
                                      "Selection Required", JOptionPane.WARNING_MESSAGE);
        return;
    }

    // 2. Extract ID from the first column of the table
    long userId = (long) userView.getUserTable().getValueAt(selectedRow, 0);

    // 3. Get the modified data from the input fields
    User updatedUser = userView.getUserDataFromInputs();
    if (updatedUser == null) return; // Validation inside getUserDataFromInputs failed

    // 4. Force the ID into the user object (overriding any manual input)
    updatedUser.setId(userId);

    // 5. Execute RMI call
    new SwingWorker<Void, Void>() {
        @Override 
        protected Void doInBackground() throws Exception {
            userService.updateUser(updatedUser);
            return null;
        }

        @Override 
        protected void done() {
            try {
                get(); // Check for RemoteExceptions
                JOptionPane.showMessageDialog(userView, "User updated successfully!");
                loadUsersToTableAsync(); // Refresh the list
                userView.clearFields();  // Clean up the form
            } catch (Exception e) { 
                showError("Update failed", e); 
            }
        }
    }.execute();
}

  
    private void handleDeleteUser() {
        int row = userView.getUserTable().getSelectedRow();
        if (row == -1) return;
        long id = (long) userView.getUserTable().getValueAt(row, 0);

        int confirm = JOptionPane.showConfirmDialog(userView, "Delete User ID " + id + "?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            new SwingWorker<Void, Void>() {
                @Override protected Void doInBackground() throws Exception {
                    userService.deleteUser(id);
                    return null;
                }
                @Override protected void done() {
                    try {
                        get();
                        loadUsersToTableAsync();
                    } catch (Exception e) { showError("Deletion failed", e); }
                }
            }.execute();
        }
    }

    private void showError(String title, Exception e) {
        Throwable cause = (e.getCause() != null) ? e.getCause() : e;
        JOptionPane.showMessageDialog(userView, title + ": " + cause.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}