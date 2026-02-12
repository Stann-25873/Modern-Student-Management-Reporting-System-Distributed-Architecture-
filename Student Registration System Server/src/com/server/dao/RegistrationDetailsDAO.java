// File: StudentRegistrationSystemServer/src/com/server/dao/RegistrationDetailsDAO.java
package com.server.dao;

import com.server.model.RegistrationDetails;
import com.server.util.HibernateUtil;
import java.io.Serializable;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class RegistrationDetailsDAO extends BaseHibernateDAO<RegistrationDetails, Long> implements Serializable {

    private static final long serialVersionUID = 1L;

    public RegistrationDetailsDAO() {
        super(RegistrationDetails.class);
    }
    
    public void saveRegistration(RegistrationDetails reg) {
    Session session = HibernateUtil.getSessionFactory().openSession();
    Transaction tx = null;
    try {
        tx = session.beginTransaction();
        session.save(reg); 
        tx.commit(); // THIS makes it appear in the database permanently
        System.out.println("Success: Registration committed.");
    } catch (Exception e) {
        if (tx != null) tx.rollback();
        e.printStackTrace();
    } finally {
        session.close();
    }
}
    
    @Override
    public List<RegistrationDetails> findAll() {
    Session session = HibernateUtil.getSessionFactory().openSession();
    try {
        // Use JOIN FETCH to get the Student object alongside the Registration details
        return session.createQuery(
            "SELECT r FROM RegistrationDetails r " +
            "LEFT JOIN FETCH r.student")
            .list();
    } finally {
        session.close();
    }
}
}