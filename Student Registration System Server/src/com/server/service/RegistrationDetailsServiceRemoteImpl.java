// File: StudentRegistrationSystemServer/src/com/server/service/RegistrationDetailsServiceRemoteImpl.java
package com.server.service;

import com.server.dao.RegistrationDetailsDAO;
import com.server.model.RegistrationDetails;
import com.server.remote.RegistrationDetailsServiceRemote;
import com.server.util.HibernateUtil;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class RegistrationDetailsServiceRemoteImpl extends UnicastRemoteObject implements RegistrationDetailsServiceRemote {

    private final RegistrationDetailsDAO detailsDAO;

    public RegistrationDetailsServiceRemoteImpl() throws RemoteException {
        super();
        this.detailsDAO = new RegistrationDetailsDAO();
    }
    
    private void validateRegistrationDetails(RegistrationDetails details) throws RemoteException {
        if (details.getStudent() == null || details.getStudent().getId() == null) {
            throw new RemoteException("Student is mandatory for registration details.");
        }
        if (details.getAcademicYear() == null || details.getAcademicYear().trim().isEmpty()) {
            throw new RemoteException("Academic Year is mandatory.");
        }
        if (details.getCurrentSemester() == null || details.getCurrentSemester().trim().isEmpty()) {
            throw new RemoteException("Current Semester is mandatory.");
        }
        if (details.getRegistrationDate() == null) {
            throw new RemoteException("Registration Date is mandatory.");
        }
        if (details.getDeadline() == null) {
            throw new RemoteException("Deadline Date is mandatory.");
        }
    }
    
    @Override
    public void addDetails(RegistrationDetails details) throws RemoteException {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            validateRegistrationDetails(details);
            tx = session.beginTransaction();
            
            // Set default payment status if null
            if (details.getIsPaid() == null) {
                details.setIsPaid(false);
            }
            
            session.save(details);
            tx.commit(); 
        } catch (RemoteException e) {
            if (tx != null) tx.rollback();
            System.err.println("Error saving registration details: " + e.getMessage());
            throw new RemoteException("Server error saving registration details.", e);
        } finally {
            session.close();
        }
    }

    @Override
    public List<RegistrationDetails> getAllDetails() throws RemoteException {
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            // JOIN FETCH ensures all nested data is loaded before the session closes
            String hql = "SELECT DISTINCT r FROM RegistrationDetails r " +
                         "LEFT JOIN FETCH r.student s " +
                         "LEFT JOIN FETCH s.program p " +
                         "LEFT JOIN FETCH p.department";
            
            // Hibernate 4.3.1 compatible: No .class argument in createQuery
            return session.createQuery(hql).list();
        } catch (Exception e) {
            throw new RemoteException("Error fetching all registration details.", e);
        } finally {
            session.close();
        }
    }

    @Override
    public void updateDetails(RegistrationDetails details) throws RemoteException {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            if (details.getId() == null || details.getId() <= 0) {
                 throw new RemoteException("Details ID is required for update.");
            }
            validateRegistrationDetails(details);
            tx = session.beginTransaction();
            session.update(details);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            System.err.println("Error updating registration details: " + e.getMessage());
            throw new RemoteException("Server error updating registration details.", e);
        } finally {
            session.close();
        }
    }

    @Override
    public void deleteDetails(Long detailsId) throws RemoteException {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            RegistrationDetails detailsToDelete = (RegistrationDetails) session.get(RegistrationDetails.class, detailsId);
            if (detailsToDelete != null) {
                session.delete(detailsToDelete);
                tx.commit();
            } else {
                throw new RemoteException("Registration Details with ID " + detailsId + " not found.");
            }
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            System.err.println("Error deleting registration details: " + e.getMessage());
            throw new RemoteException("Server error deleting registration details.", e);
        } finally {
            session.close();
        }
    }

    @Override
    public RegistrationDetails getDetailsById(Long detailsId) throws RemoteException {
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            String hql = "SELECT r FROM RegistrationDetails r " +
                         "LEFT JOIN FETCH r.student " +
                         "WHERE r.id = :id";
            return (RegistrationDetails) session.createQuery(hql)
                                                .setParameter("id", detailsId)
                                                .uniqueResult();
        } catch (Exception e) {
            throw new RemoteException("Error finding registration details by ID.", e);
        } finally {
            session.close();
        }
    }
}