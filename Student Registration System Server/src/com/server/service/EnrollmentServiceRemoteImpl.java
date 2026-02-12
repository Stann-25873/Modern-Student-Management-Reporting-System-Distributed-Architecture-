// File: StudentRegistrationSystemServer/src/com/server/service/EnrollmentServiceRemoteImpl.java
package com.server.service;

import com.server.dao.EnrollmentDAO; 
import com.server.model.Enrollment;
import com.server.remote.EnrollmentServiceRemote; 
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class EnrollmentServiceRemoteImpl extends UnicastRemoteObject implements EnrollmentServiceRemote {

    private final EnrollmentDAO enrollmentDAO;

    // Assuming dependency injection is not used based on the original code structure, 
    // but highly recommended to refactor. Using simple instantiation for compatibility.
    public EnrollmentServiceRemoteImpl() throws RemoteException {
        super();
        this.enrollmentDAO = new EnrollmentDAO();
    }

    private void validateEnrollment(Enrollment enrollment) throws RemoteException {
        if (enrollment.getStudent() == null || enrollment.getStudent().getId() == null) {
            throw new RemoteException("Student is mandatory for enrollment.");
        }
        if (enrollment.getCourse() == null || enrollment.getCourse().getId() == null) {
            throw new RemoteException("Course is mandatory for enrollment.");
        }
        if (enrollment.getEnrollmentDate() == null) {
            throw new RemoteException("Enrollment Date is mandatory.");
        }
    }

    @Override
    public void addEnrollment(Enrollment enrollment) throws RemoteException {
        try {
            validateEnrollment(enrollment);
            enrollmentDAO.save(enrollment);
        } catch (Exception e) {
            throw new RemoteException("Server error saving enrollment.", e);
        }
    }

    @Override
    public void updateEnrollment(Enrollment enrollment) throws RemoteException {
        try {
            if (enrollment.getId() == null) {
                 throw new RemoteException("Enrollment ID is required for update.");
            }
            // Student and Course links generally should not change after creation, 
            // but we ensure mandatory fields are still present.
            validateEnrollment(enrollment); 
            enrollmentDAO.update(enrollment);
        } catch (Exception e) {
            throw new RemoteException("Server error updating enrollment.", e);
        }
    }

    @Override
    public void deleteEnrollment(Long enrollmentId) throws RemoteException {
        try {
            Enrollment enrollment = enrollmentDAO.findById(enrollmentId);
            if (enrollment != null) {
                enrollmentDAO.delete(enrollment);
            } else {
                throw new RemoteException("Enrollment ID " + enrollmentId + " not found.");
            }
        } catch (Exception e) {
            throw new RemoteException("Server error deleting enrollment.", e);
        }
    }

    @Override
    public Enrollment getEnrollmentById(Long enrollmentId) throws RemoteException {
        try {
            return enrollmentDAO.findById(enrollmentId);
        } catch (Exception e) {
            throw new RemoteException("Error finding enrollment by ID.", e);
        }
    }

    @Override
    public List<Enrollment> getAllEnrollments() throws RemoteException {
        try {
            return enrollmentDAO.findAll();
        } catch (Exception e) {
            throw new RemoteException("Error fetching all enrollments.", e);
        }
    }
}