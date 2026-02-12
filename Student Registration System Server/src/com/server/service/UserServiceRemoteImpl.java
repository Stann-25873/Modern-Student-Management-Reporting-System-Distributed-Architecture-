package com.server.service;

import com.server.remote.UserServiceRemote;
import com.server.model.User;
import com.server.util.HibernateUtil;
import com.server.util.PasswordUtil;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.jms.*;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.Query;
import org.hibernate.exception.ConstraintViolationException;

public class UserServiceRemoteImpl extends UnicastRemoteObject implements UserServiceRemote {

    private final Map<String, String> otpCache = new ConcurrentHashMap<>();
    private final String ACTIVEMQ_URL = "tcp://localhost:61616";

    public UserServiceRemoteImpl() throws RemoteException {
        super();
    }

    // --- 1. OTP & MESSAGING LOGIC (ActiveMQ Integration) ---

    @Override
    public void initiateRegistration(String email) throws RemoteException {
        String otp = String.valueOf((int)((Math.random() * 900000) + 100000));
        otpCache.put(email, otp);

        try {
            ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(ACTIVEMQ_URL);
            javax.jms.Connection connection = factory.createConnection();
            connection.start();

            javax.jms.Session session = connection.createSession(false, javax.jms.Session.AUTO_ACKNOWLEDGE);
            Destination queue = session.createQueue("REGISTRATION_OTP_QUEUE");
            MessageProducer producer = session.createProducer(queue);

            MapMessage msg = session.createMapMessage();
            msg.setString("email", email);
            msg.setString("otp", otp);

            producer.send(msg);

            producer.close();
            session.close();
            connection.close();
            System.out.println(">>> [ActiveMQ] OTP " + otp + " queued for: " + email);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RemoteException("Messaging error: Failed to send OTP.");
        }
    }

    @Override
    public boolean verifyOTP(String email, String enteredOtp) throws RemoteException {
        String correctOtp = otpCache.get(email);
        return correctOtp != null && correctOtp.equals(enteredOtp);
    }

    @Override
    public User loginWithOTP(String email, String enteredOtp) throws RemoteException {
        if (verifyOTP(email, enteredOtp)) {
            Session session = HibernateUtil.getSessionFactory().openSession();
            try {
                Query query = session.createQuery("FROM User WHERE email = :email");
                query.setParameter("email", email);
                User user = (User) query.uniqueResult();
                
                if (user != null) {
                    otpCache.remove(email); // Success: clean up
                    return user;
                }
            } finally {
                session.close();
            }
        }
        return null;
    }

    @Override
    public boolean resetPassword(String email, String otp, String newPassword) throws RemoteException {
        if (verifyOTP(email, otp)) {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                Query query = session.createQuery("FROM User WHERE email = :email");
                query.setParameter("email", email);
                User user = (User) query.uniqueResult();

                if (user != null) {
                    user.setPasswordHash(PasswordUtil.hashPassword(newPassword));
                    session.update(user);
                    tx.commit();
                    otpCache.remove(email);
                    return true;
                }
            } catch (Exception e) {
                if (tx != null) tx.rollback();
                throw new RemoteException("Database error during password reset.", e);
            } finally {
                session.close();
            }
        }
        return false;
    }

    // --- 2. AUTHENTICATION & REGISTRATION ---

    @Override
    public User authenticate(String username, String password) throws RemoteException {
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            Query query = session.createQuery("FROM User WHERE username = :username");
            query.setParameter("username", username);
            User user = (User) query.uniqueResult();

            if (user != null && PasswordUtil.checkPassword(password, user.getPasswordHash())) {
                return user;
            }
        } catch (Exception e) {
            throw new RemoteException("Authentication server error.", e);
        } finally {
            session.close();
        }
        return null;
    }

    @Override
    public User registerUser(User newUser, String firstName, String lastName, String email) throws RemoteException {
        // Ensuring data from registration UI is set
        newUser.setFirstName(firstName);
        newUser.setLastName(lastName);
        newUser.setEmail(email);
        addUser(newUser);
        return newUser;
    }

    // --- 3. CRUD OPERATIONS (Hibernate Integration) ---

    @Override
    public void addUser(User user) throws RemoteException {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            // Hash password if it's plain text
            if (user.getPasswordHash() != null && !user.getPasswordHash().startsWith("$2a$")) {
                user.setPasswordHash(PasswordUtil.hashPassword(user.getPasswordHash()));
            }
            session.save(user);
            transaction.commit();
        } catch (ConstraintViolationException e) {
            if (transaction != null) transaction.rollback();
            throw new RemoteException("Constraint violation: Username or Email already exists.");
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new RemoteException("Database error while adding user.", e);
        } finally {
            session.close();
        }
    }

    @Override
    public void updateUser(User user) throws RemoteException {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            // Check if password was changed and needs re-hashing
            if (user.getPasswordHash() != null && !user.getPasswordHash().startsWith("$2a$")) {
                user.setPasswordHash(PasswordUtil.hashPassword(user.getPasswordHash()));
            }
            session.update(user);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new RemoteException("Error updating user: " + e.getMessage());
        } finally {
            session.close();
        }
    }

    @Override
    public void deleteUser(long userId) throws RemoteException {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            User user = (User) session.get(User.class, userId);
            if (user != null) {
                session.delete(user);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new RemoteException("Error deleting user: " + e.getMessage());
        } finally {
            session.close();
        }
    }

    @Override
    public User getUserById(long userId) throws RemoteException {
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            return (User) session.get(User.class, userId);
        } finally {
            session.close();
        }
    }

    @Override
    public void requestPasswordReset(String username) throws RemoteException {
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            Query query = session.createQuery("FROM User WHERE username = :username");
            query.setParameter("username", username);
            User user = (User) query.uniqueResult();

            if (user != null) {
                initiateRegistration(user.getEmail()); // Reuses OTP logic
            } else {
                throw new RemoteException("Username not found.");
            }
        } finally {
            session.close();
        }
    }

    // --- 4. DATA EXPORT SUPPORT ---

    @Override
    public List<User> getAllUsers() throws RemoteException {
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            // HQL Query to fetch all users for the Reporting engine
            return session.createQuery("FROM User").list();
        } catch (Exception e) {
            throw new RemoteException("Failed to retrieve user list for export: " + e.getMessage());
        } finally {
            session.close();
        }
    }
}