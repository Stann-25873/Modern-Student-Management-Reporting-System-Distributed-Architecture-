package com.client.controller;

import com.client.remote.RMIClient;
import com.client.view.CourseView;
import com.server.model.Course;
import com.server.model.Department;
import com.server.model.Program;
import com.server.remote.CourseServiceRemote;
import com.server.remote.DepartmentServiceRemote;
import com.server.remote.ProgramServiceRemote;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Controller for Course management.
 * Connects the modern Teal View with the RMI Server using SwingWorkers.
 */
public class CourseController implements ActionListener {

    private final CourseView view;
    private final CourseServiceRemote courseService;
    private final DepartmentServiceRemote departmentService;
    private final ProgramServiceRemote programService;

    public CourseController(CourseView view, RMIClient rmiClient) throws RemoteException {
        this.view = view;

        try {
            // Initialize RMI services
            this.courseService = (CourseServiceRemote) rmiClient.getCourseService();
            this.departmentService = (DepartmentServiceRemote) rmiClient.getDepartmentService();
            this.programService = (ProgramServiceRemote) rmiClient.getProgramService();

            attachListeners();
            loadInitialData();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Connection Error: " + e.getMessage(), 
                "Critical RMI Failure", JOptionPane.ERROR_MESSAGE);
            throw new RemoteException("Failed to initialize RMI services for CourseController", e);
        }
    }

    private void attachListeners() {
        view.getAddCourseButton().addActionListener(this);
        view.getUpdateCourseButton().addActionListener(this);
        view.getDeleteCourseButton().addActionListener(this);
        view.getRefreshDataButton().addActionListener(this);
        view.getClearFieldsButton().addActionListener(this);
        view.getBackButton().addActionListener(this);
        
        // Listener for table row selection
        view.getCourseTable().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                handleTableSelection();
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == view.getAddCourseButton()) {
            handleAddCourse();
        } else if (source == view.getUpdateCourseButton()) {
            handleUpdateCourse();
        } else if (source == view.getDeleteCourseButton()) {
            handleDeleteCourse();
        } else if (source == view.getRefreshDataButton()) {
            loadInitialData();
        } else if (source == view.getClearFieldsButton()) {
            view.clearFields();
        } else if (source == view.getBackButton()) {
            view.dispose();
        }
    }

    private void handleTableSelection() {
        long courseId = view.getSelectedCourseIdFromTable();
        if (courseId == -1) return;

        // Fetch full course details (including relationships) from server
        new SwingWorker<Course, Void>() {
            @Override
            protected Course doInBackground() throws Exception {
                return courseService.getCourseById(courseId);
            }

            @Override
            protected void done() {
                try {
                    Course selected = get();
                    if (selected != null) {
                        view.populateFields(selected);
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    showError("Detail Retrieval Failed", ex);
                }
            }
        }.execute();
    }

    public void loadInitialData() {
        loadCoursesTableAsync();
        loadMetadataComboBoxesAsync();
    }

    private void loadCoursesTableAsync() {
        new SwingWorker<List<Course>, Void>() {
            @Override
            protected List<Course> doInBackground() throws Exception {
                return courseService.getAllCourses();
            }

            @Override
            protected void done() {
                try {
                    view.refreshCourseTable(get());
                } catch (Exception e) {
                    showError("Failed to Load Courses", e);
                }
            }
        }.execute();
    }

    private void loadMetadataComboBoxesAsync() {
        new SwingWorker<Void, Void>() {
            private List<Department> departments;
            private List<Program> programs;

            @Override
            protected Void doInBackground() throws Exception {
                // Fetching lists for dropdowns
                departments = departmentService.getAllDepartments();
                programs = programService.getAllPrograms();
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    view.populateProgramComboBox(programs);
                    // If you added a department box to the UI:
                    // view.populateDepartmentComboBox(departments);
                } catch (Exception e) {
                    showError("Metadata Load Failed", e);
                }
            }
        }.execute();
    }

    private void handleAddCourse() {
        Course course = view.getCourseDataFromInputs();
        if (course == null) return;

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                course.setId(null); // Ensure ID is null for new records
                courseService.addCourse(course);
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    JOptionPane.showMessageDialog(view, "Course successfully created.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadCoursesTableAsync();
                    view.clearFields();
                } catch (Exception ex) {
                    showError("Addition Failed", ex);
                }
            }
        }.execute();
    }

    private void handleUpdateCourse() {
        Course course = view.getCourseDataFromInputs();
        if (course == null || course.getId() == null) {
            JOptionPane.showMessageDialog(view, "Select a course from the table to update.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                courseService.updateCourse(course);
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    JOptionPane.showMessageDialog(view, "Course updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadCoursesTableAsync();
                } catch (Exception ex) {
                    showError("Update Failed", ex);
                }
            }
        }.execute();
    }

    private void handleDeleteCourse() {
        long courseId = view.getSelectedCourseIdFromTable();
        if (courseId == -1) {
            JOptionPane.showMessageDialog(view, "Select a course to remove.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(view, 
            "Delete Course ID: " + courseId + "?", "Confirm Action", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    courseService.deleteCourse(courseId);
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        get();
                        JOptionPane.showMessageDialog(view, "Course removed.", "Deleted", JOptionPane.INFORMATION_MESSAGE);
                        loadCoursesTableAsync();
                        view.clearFields();
                    } catch (Exception ex) {
                        showError("Deletion Failed", ex);
                    }
                }
            }.execute();
        }
    }

    private void showError(String title, Exception e) {
        Throwable cause = e.getCause() != null ? e.getCause() : e;
        JOptionPane.showMessageDialog(view, title + ": " + cause.getMessage(), "System Error", JOptionPane.ERROR_MESSAGE);
    }
}