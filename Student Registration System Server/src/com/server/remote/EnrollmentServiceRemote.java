// File: StudentRegistrationSystemServer/src/com/server/remote/EnrollmentServiceRemote.java
package com.server.remote;

import com.server.model.Enrollment;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface EnrollmentServiceRemote extends Remote {

    void addEnrollment(Enrollment enrollment) throws RemoteException;
    void updateEnrollment(Enrollment enrollment) throws RemoteException;
    void deleteEnrollment(Long enrollmentId) throws RemoteException;
    Enrollment getEnrollmentById(Long enrollmentId) throws RemoteException;
    List<Enrollment> getAllEnrollments() throws RemoteException;
}