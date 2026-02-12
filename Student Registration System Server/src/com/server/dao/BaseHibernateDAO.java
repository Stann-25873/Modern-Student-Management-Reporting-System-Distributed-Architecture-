// File: StudentRegistrationSystemServer/src/com/server/dao/BaseHibernateDAO.java

package com.server.dao;

import com.server.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.Query;
import java.io.Serializable;
import java.util.List;

public abstract class BaseHibernateDAO<T, ID extends Serializable> implements GenericDAO<T, ID> {

    private final Class<T> entityClass;

    public BaseHibernateDAO(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    @Override
    public T save(T entity) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.saveOrUpdate(entity);
            transaction.commit();
            return entity;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.err.println("Error saving/updating " + entityClass.getSimpleName() + ": " + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public T findById(ID id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            return (T) session.get(entityClass, id); 
        } catch (Exception e) {
            System.err.println("Error finding " + entityClass.getSimpleName() + " by ID: " + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override

    public List<T> findAll() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            Query query = session.createQuery("FROM " + entityClass.getSimpleName());
            @SuppressWarnings("unchecked")
            List<T> entities = query.list();
            return entities;
        } catch (Exception e) {
            System.err.println("Error fetching all " + entityClass.getSimpleName() + "s: " + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public void update(T entity) {
        save(entity); // saveOrUpdate gère l'update
    }
    
    @Override
    public void delete(T entity) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.delete(entity); 
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.err.println("Error deleting " + entityClass.getSimpleName() + ": " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
    
    
    
    
}