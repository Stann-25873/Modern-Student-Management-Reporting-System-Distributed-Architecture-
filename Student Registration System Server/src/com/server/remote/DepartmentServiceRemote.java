// File: StudentRegistrationSystemServer/src/com/server/remote/DepartmentServiceRemote.java
package com.server.remote;

import com.server.model.Department;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface DepartmentServiceRemote extends Remote {

    void addDepartment(Department department) throws RemoteException;
    Department getDepartmentById(Long departmentId) throws RemoteException;
    List<Department> getAllDepartments() throws RemoteException;
    void updateDepartment(Department department) throws RemoteException;
    void deleteDepartment(Long departmentId) throws RemoteException;
}