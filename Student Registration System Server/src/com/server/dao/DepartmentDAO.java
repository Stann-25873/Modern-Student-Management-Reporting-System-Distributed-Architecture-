// File: StudentRegistrationSystemServer/src/com/server/dao/DepartmentDAO.java
package com.server.dao;

import com.server.model.Department;
import java.io.Serializable;

// Supposons que BaseHibernateDAO<T, ID> est disponible
// et implémente save, update, findById, findAll, delete(T entity).
public class DepartmentDAO extends BaseHibernateDAO<Department, Long> implements Serializable {

    private static final long serialVersionUID = 1L;

    public DepartmentDAO() {
        super(Department.class);
    }
    
    // Surcharge de la suppression pour gérer l'objet Department (si nécessaire)
    public void delete(Department department) {
        super.delete(department);
    }
    
    
}