// File: StudentRegistrationSystemServer/src/com/server/dao/EnrollmentDAO.java
package com.server.dao;

import com.server.model.Enrollment;
import com.server.util.HibernateUtil;
import java.io.Serializable;
import java.util.List;
import org.hibernate.Session;

// Using Long as ID type for consistency with JPA model (bigint)
public class EnrollmentDAO extends BaseHibernateDAO<Enrollment, Long> implements Serializable {

    private static final long serialVersionUID = 1L;

    public EnrollmentDAO() {
        super(Enrollment.class);
    }
    @Override
    public List<Enrollment> findAll() {
    Session session = HibernateUtil.getSessionFactory().openSession();
    try {
        // This query fetches the Enrollment PLUS the Student PLUS the Program in one go
        return session.createQuery(
            "SELECT e FROM Enrollment e " +
            "JOIN FETCH e.student s " +
            "JOIN FETCH s.program")
            .list();
    } finally {
        session.close();
    }
}
    
    // Specific implementations if necessary...
}