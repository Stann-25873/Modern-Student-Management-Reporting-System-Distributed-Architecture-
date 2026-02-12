// File: StudentRegistrationSystemServer/src/com/server/remote/StudentServiceRemote.java
package com.server.remote;

import com.server.model.Student;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface StudentServiceRemote extends Remote {

    void addStudent(Student student) throws RemoteException;
    Student getStudentById(Long studentId) throws RemoteException;
    List<Student> getAllStudents() throws RemoteException;
    void updateStudent(Student student) throws RemoteException;
    void deleteStudent(Long studentId) throws RemoteException;void initiateRegistration(String email) throws RemoteException;
    boolean verifyOTP(String email, String enteredOtp) throws RemoteException;
}