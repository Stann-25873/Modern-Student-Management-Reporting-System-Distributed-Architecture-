// File: StudentRegistrationSystemServer/src/com/server/dao/UserDAO.java

package com.server.dao;

import com.server.model.User;
import com.server.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Query; // Garde l'import org.hibernate.Query (le plus compatible avec l'HQL non typé initial)
import java.io.Serializable;
import java.util.List;

public class UserDAO extends BaseHibernateDAO<User, Long> implements Serializable {

    private static final long serialVersionUID = 1L;
    
    public UserDAO() {
        super(User.class);
    }
    
    
    public User findByUsername(String username) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            // Utilisation de la syntaxe HQL
            Query query = session.createQuery("FROM User WHERE username = :username");
            query.setParameter("username", username);
            
            @SuppressWarnings("unchecked")
            List<User> users = query.list();
            return users.isEmpty() ? null : users.get(0);
        } catch (Exception e) {
            System.err.println("Error finding user by username: " + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
}