// File: StudentRegistrationSystemServer/src/com/server/service/DepartmentServiceRemoteImpl.java
package com.server.service;

import com.server.dao.DepartmentDAO;
import com.server.dao.DepartmentDAO; // Assuming the concrete DAO implementation exists
import com.server.model.Department;
import com.server.remote.DepartmentServiceRemote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class DepartmentServiceRemoteImpl extends UnicastRemoteObject implements DepartmentServiceRemote {

    private final DepartmentDAO departmentDAO;

    /**
     * CONSTRUCTOR 1: Default constructor for use when DAO is instantiated internally 
     * or to simplify RMI binding in ServerMain.
     */
    public DepartmentServiceRemoteImpl() throws RemoteException {
        super();
        // Assuming DepartmentDAOImpl is the concrete class for dependency instantiation
        this.departmentDAO = new DepartmentDAO(); 
    }

    /**
     * CONSTRUCTOR 2: Constructor for Dependency Injection (kept for better design practice).
     * @param departmentDAO The DAO implementation to use.
     */
    public DepartmentServiceRemoteImpl(DepartmentDAO departmentDAO) throws RemoteException {
        super();
        this.departmentDAO = departmentDAO; 
    }
    
    // --- Implémentation des méthodes CRUD de l'interface DepartmentServiceRemote ---

    @Override
    public void addDepartment(Department department) throws RemoteException {
        try {
            departmentDAO.save(department);
        } catch (Exception e) {
            throw new RemoteException("Server error saving department.", e);
        }
    }

    @Override
    public Department getDepartmentById(Long departmentId) throws RemoteException {
        try {
            return departmentDAO.findById(departmentId);
        } catch (Exception e) {
            throw new RemoteException("Error finding department by ID.", e);
        }
    }

    @Override
public List<Department> getAllDepartments() throws RemoteException {
    try {
        List<Department> departments = departmentDAO.findAll(); // Session is open here
        
        // Explicitly initialize the lazy collection (Programs) before the session closes
        for (Department department : departments) {
            // Check if the association exists and initialize it if lazy-loaded
            if (department.getPrograms() != null) {
                // Use Hibernate.initialize to fetch the collection data
                org.hibernate.Hibernate.initialize(department.getPrograms()); // Manual Initialization
            }
        }
        
        return departments;
    } catch (Exception e) {
        // Wrap the underlying exception in a RemoteException for RMI transmission
        throw new RemoteException("Error fetching all departments with their associated programs.", e);
    }
}

    @Override
    public void updateDepartment(Department department) throws RemoteException {
        try {
            departmentDAO.update(department);
        } catch (Exception e) {
            throw new RemoteException("Server error updating department.", e);
        }
    }

    @Override
    public void deleteDepartment(Long departmentId) throws RemoteException {
        try {
            Department department = departmentDAO.findById(departmentId);
            
            if (department != null) {
                departmentDAO.delete(department); 
            } else {
                throw new RemoteException("Department ID " + departmentId + " not found.");
            }
        } catch (RemoteException re) {
            throw re; 
        } catch (Exception e) {
            throw new RemoteException("Server error deleting department. Check for related entities.", e);
        }
    }
}