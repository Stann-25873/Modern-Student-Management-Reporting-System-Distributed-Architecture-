package com.client.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * The main application dashboard providing navigation links and Reporting features.
 */
public class MainDashboardView extends JFrame {

    private static final long serialVersionUID = 1L;

    // --- Navigation Buttons ---
    private JButton manageDepartmentsButton;
    private JButton manageProgramsButton;
    private JButton manageCoursesButton;
    private JButton manageStudentsButton;
    private JButton manageEnrollmentGradesButton; 
    private JButton userManagementButton;
    private JButton logoutButton;
    
    // --- Export Buttons (New) ---
    private JButton exportPdfButton;
    private JButton exportExcelButton;
    private JButton exportCsvButton;
    
    // --- Custom Theme Colors ---
    private static final Color TEAL_PRIMARY = new Color(45, 126, 114);
    private static final Color BG_LIGHT = new Color(240, 245, 245);
    private static final Color TEXT_DARK = new Color(50, 50, 50);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 28);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 15);

    public MainDashboardView() {
        setTitle("Student Registration System - Admin Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 800); // Increased size slightly for reports
        setResizable(true);
        setLocationRelativeTo(null); 

        initComponents();
        layoutComponents();
    }

    private void initComponents() {
        // --- Dashboard Module Buttons ---
        manageDepartmentsButton = createModuleButton("Manage Departments", "Organize faculty and staff units");
        manageProgramsButton = createModuleButton("Manage Programs", "Configure degrees and certifications");
        manageCoursesButton = createModuleButton("Manage Courses", "Maintain syllabus and course catalogs");
        manageStudentsButton = createModuleButton("Manage Students", "View and edit student profiles");
        manageEnrollmentGradesButton = createModuleButton("Registration Details", "Handle student enrollments and records");
        userManagementButton = createModuleButton("User Management", "System security and admin access");
        
        // --- Export Buttons Initialization ---
        exportPdfButton = createExportButton("Export PDF", new Color(200, 50, 50)); // Reddish for PDF
        exportExcelButton = createExportButton("Export Excel", new Color(34, 139, 34)); // Green for Excel
        exportCsvButton = createExportButton("Export CSV", new Color(70, 70, 70)); // Grey for CSV

        // --- Logout Button ---
        logoutButton = new JButton("Sign Out");
        logoutButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        logoutButton.setForeground(new Color(180, 50, 50));
        logoutButton.setContentAreaFilled(false);
        logoutButton.setBorder(BorderFactory.createLineBorder(new Color(180, 50, 50), 1));
        logoutButton.setFocusPainted(false);
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
    
    private JButton createModuleButton(String title, String subtitle) {
        String buttonText = "<html><div style='text-align: center;'><b style='font-size: 12px;'>" + title.toUpperCase() + "</b><br>"
                          + "<i style='font-size: 9px; font-weight: normal; color: #555555;'>" + subtitle + "</i></div></html>";
        
        JButton button = new JButton(buttonText);
        button.setFont(BUTTON_FONT);
        button.setBackground(Color.WHITE);
        button.setForeground(TEAL_PRIMARY);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        return button;
    }

    // Helper for specialized Export Buttons
    private JButton createExportButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(color);
        btn.setBackground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createLineBorder(color, 1));
        btn.setPreferredSize(new Dimension(120, 35));
        return btn;
    }

    private void layoutComponents() {
        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setBackground(BG_LIGHT);

        // --- 1. Top Header ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setPreferredSize(new Dimension(0, 80));
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 3, 0, TEAL_PRIMARY));
        
        JLabel titleLabel = new JLabel("Administration Portal", SwingConstants.LEFT);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(TEXT_DARK);
        titleLabel.setBorder(new EmptyBorder(0, 30, 0, 0));
        
        JPanel logoutWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 30, 25));
        logoutWrapper.setOpaque(false);
        logoutWrapper.add(logoutButton);
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(logoutWrapper, BorderLayout.EAST);

        // --- 2. Center Grid (Modules) ---
        JPanel gridPanel = new JPanel(new GridLayout(2, 3, 25, 25));
        gridPanel.setBackground(BG_LIGHT);
        gridPanel.setBorder(new EmptyBorder(40, 40, 40, 40));

        gridPanel.add(manageDepartmentsButton);
        gridPanel.add(manageProgramsButton);
        gridPanel.add(manageCoursesButton);
        gridPanel.add(manageStudentsButton);
        gridPanel.add(manageEnrollmentGradesButton);
        gridPanel.add(userManagementButton);

        // --- 3. Report Section (NEW) ---
        JPanel reportPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        reportPanel.setBackground(Color.WHITE);
        reportPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(TEAL_PRIMARY), "System Reports & Exports", 0, 0, 
                new Font("Segoe UI", Font.BOLD, 12), TEAL_PRIMARY));
        
        reportPanel.add(new JLabel("Export User Data: "));
        reportPanel.add(exportPdfButton);
        reportPanel.add(exportExcelButton);
        reportPanel.add(exportCsvButton);

        // --- 4. Main Body Container ---
        JPanel bodyContainer = new JPanel(new BorderLayout());
        bodyContainer.setOpaque(false);
        bodyContainer.add(gridPanel, BorderLayout.CENTER);
        bodyContainer.add(reportPanel, BorderLayout.SOUTH);

        // --- 5. Welcome Bar ---
        JPanel welcomePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        welcomePanel.setBackground(new Color(230, 235, 235));
        welcomePanel.setBorder(new EmptyBorder(5, 30, 5, 30));
        JLabel welcomeLabel = new JLabel("Logged in as System Administrator | " + java.time.LocalDate.now());
        welcomeLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        welcomePanel.add(welcomeLabel);

        contentPane.add(headerPanel, BorderLayout.NORTH);
        contentPane.add(bodyContainer, BorderLayout.CENTER);
        contentPane.add(welcomePanel, BorderLayout.SOUTH);

        add(contentPane);
    }

    // --- Accessors ---
    public JButton getManageDepartmentsButton() { return manageDepartmentsButton; }
    public JButton getManageProgramsButton() { return manageProgramsButton; }
    public JButton getManageCoursesButton() { return manageCoursesButton; }
    public JButton getManageStudentsButton() { return manageStudentsButton; }
    public JButton getManageEnrollmentGradesButton() { return manageEnrollmentGradesButton; } 
    public JButton getUserManagementButton() { return userManagementButton; }
    public JButton getLogoutButton() { return logoutButton; }
    
    // New Accessors for Report functionality
    public JButton getExportPdfButton() { return exportPdfButton; }
    public JButton getExportExcelButton() { return exportExcelButton; }
    public JButton getExportCsvButton() { return exportCsvButton; }
}






    /*
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     *
    public static void main(String args[]) {
        /* Set the Nimbus look and feel *
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         *
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(DashboardView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(DashboardView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(DashboardView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DashboardView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form *
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new DashboardView().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}*/
