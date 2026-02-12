// File: StudentRegistrationSystemServer/src/com/server/dao/StudentDAO.java
package com.server.dao;

import com.server.model.Student;
import com.server.util.HibernateUtil;
import java.io.Serializable;
import java.util.List;
import org.hibernate.Session;

// Using Long as ID type for consistency with JPA model (bigint)
public class StudentDAO extends BaseHibernateDAO<Student, Long> implements Serializable {

    private static final long serialVersionUID = 1L;

    public StudentDAO() {
        super(Student.class);
    }
    @Override
    public List<Student> findAll() {
    Session session = HibernateUtil.getSessionFactory().openSession();
    try {
        // "JOIN FETCH" forces Hibernate to load the objects immediately
        return session.createQuery(
            "SELECT DISTINCT s FROM Student s " +
            "LEFT JOIN FETCH s.program p " +
            "LEFT JOIN FETCH p.department")
            .list();
    } finally {
        session.close();
    }
}
    public Student findByEmail(String email) {
    Session session = HibernateUtil.getSessionFactory().openSession();
    try {
        return (Student) session.createQuery("FROM Student WHERE email = :email")
                                .setParameter("email", email)
                                .uniqueResult();
    } finally {
        session.close();
    }
}
    
  public Student findByUniqueId(String student_unique_id) {
    Session session = HibernateUtil.getSessionFactory().openSession();
    try {
        // 1. Use the Java field name 'studentUniqueId'
        // 2. The parameter name ':uid' must match the setParameter name exactly
        return (Student) session.createQuery("FROM Student WHERE studentUniqueId = :uid")
                                .setParameter("uid", student_unique_id)
                                .uniqueResult();
    } finally {
        session.close();
    }
}
    
    // Specific implementations if necessary... student_unique_id
}