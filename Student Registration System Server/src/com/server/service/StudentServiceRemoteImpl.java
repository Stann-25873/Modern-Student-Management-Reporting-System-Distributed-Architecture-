package com.server.service;

import com.server.dao.StudentDAO;
import com.server.model.Student;
import com.server.remote.StudentServiceRemote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
// CRITICAL: Ensure we use JMS Connection, not SQL Connection
import javax.jms.*; 
import org.apache.activemq.ActiveMQConnectionFactory;

public class StudentServiceRemoteImpl extends UnicastRemoteObject implements StudentServiceRemote {

    private final StudentDAO studentDAO;
    // Cache to store OTPs temporarily: Key = Email, Value = OTP
    private final Map<String, String> otpCache = new ConcurrentHashMap<>();

    public StudentServiceRemoteImpl() throws RemoteException {
        super();
        this.studentDAO = new StudentDAO(); 
    }

    public StudentServiceRemoteImpl(StudentDAO studentDAO) throws RemoteException {
        super();
        this.studentDAO = studentDAO;
    }

    // --- OTP & Messaging Methods ---

    @Override
    public void initiateRegistration(String email) throws RemoteException {
        // 1. Generate 6-digit OTP
        String otp = String.valueOf((int)((Math.random() * 900000) + 100000));
        
        // 2. Store in server cache for later verification
        otpCache.put(email, otp);
        
        // 3. Send to ActiveMQ
        try {
            ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("tcp://localhost:61616");
            // Use javax.jms.Connection explicitly to avoid conflicts
            javax.jms.Connection connection = factory.createConnection();
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination queue = session.createQueue("REGISTRATION_OTP_QUEUE");
            
            MessageProducer producer = session.createProducer(queue);
            MapMessage msg = session.createMapMessage();
            msg.setString("email", email);
            msg.setString("otp", otp);
            
            producer.send(msg);
            
            // Cleanup
            producer.close();
            session.close();
            connection.close();
            
            System.out.println(">>> [ActiveMQ] OTP " + otp + " queued for email: " + email);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RemoteException("Messaging Service Error: " + e.getMessage());
        }
    }

    @Override
    public boolean verifyOTP(String email, String enteredOtp) throws RemoteException {
        if (otpCache.containsKey(email)) {
            String correctOtp = otpCache.get(email);
            if (correctOtp.equals(enteredOtp)) {
                otpCache.remove(email); // Remove after successful verification
                return true;
            }
        }
        return false;
    }

    // --- Student CRUD Methods ---

    @Override
    public void addStudent(Student student) throws RemoteException {
        try {
            if (student.getEmail() == null || student.getEmail().isEmpty()) {
                 throw new RemoteException("Email is required for registration.");
            }

            // Duplicate checks using DAO
            if (studentDAO.findByEmail(student.getEmail()) != null) {
                throw new RemoteException("Validation Error: Email '" + student.getEmail() + "' already exists.");
            }

            studentDAO.save(student);
            System.out.println("LOG: Registered student: " + student.getFirstName());
        } catch (RemoteException re) {
            throw re; 
        } catch (Exception e) {
            throw new RemoteException("Server error: " + e.getMessage());
        }
    }

    @Override
    public Student getStudentById(Long studentId) throws RemoteException {
        return studentDAO.findById(studentId);
    }

    @Override
    public List<Student> getAllStudents() throws RemoteException {
        return studentDAO.findAll();
    }

    @Override
    public void updateStudent(Student student) throws RemoteException {
        if (student.getId() == null) throw new RemoteException("ID required for update.");
        studentDAO.update(student);
    }

    @Override
    public void deleteStudent(Long studentId) throws RemoteException {
        Student s = studentDAO.findById(studentId);
        if (s != null) studentDAO.delete(s);
    }
}