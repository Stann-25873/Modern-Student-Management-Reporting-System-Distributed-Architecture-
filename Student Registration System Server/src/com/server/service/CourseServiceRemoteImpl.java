// File: StudentRegistrationSystemServer/src/com/server/service/CourseServiceRemoteImpl.java
package com.server.service;

import com.server.dao.CourseDAO;
import com.server.model.Course;
import com.server.remote.CourseServiceRemote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class CourseServiceRemoteImpl extends UnicastRemoteObject implements CourseServiceRemote {

    private final CourseDAO courseDAO;

    public CourseServiceRemoteImpl() throws RemoteException {
        super();
        this.courseDAO = new CourseDAO();
    }
    
    private void validateCourse(Course course) throws RemoteException {
        if (course.getCode() == null || course.getCode().trim().isEmpty()) {
            throw new RemoteException("Course code cannot be empty.");
        }
        if (course.getTitle() == null || course.getTitle().trim().isEmpty()) {
            throw new RemoteException("Course title cannot be empty.");
        }
        if (course.getCredits() == null || course.getCredits() <= 0) {
            throw new RemoteException("Course credits must be a positive number.");
        }
        /* Validation de la relation Department
        if (course.getDepartment() == null || course.getDepartment().getId() == null) {
            throw new RemoteException("Department is mandatory for the course.");
        }*/
        
        // NOTE: La relation Many-to-Many 'Program' n'est généralement pas obligatoire pour l'objet Course
    }


    @Override
    public void addCourse(Course course) throws RemoteException {
        try {
            validateCourse(course);
            courseDAO.save(course);
        } catch (Exception e) {
            System.err.println("Error saving course: " + e.getMessage());
            throw new RemoteException("Server error saving course.", e);
        }
    }

    @Override
    public Course getCourseById(Long id) throws RemoteException {
        try {
            return courseDAO.findById(id); 
        } catch (Exception e) {
            throw new RemoteException("Error finding course by ID.", e);
        }
    }
    
    @Override
    public Course findCourseByCode(String code) throws RemoteException {
        // Logique métier: trouve le cours par code en mémoire (si le DAO n'a pas de findByCode)
        try {
            List<Course> courses = courseDAO.findAll();
            return courses.stream()
                         .filter(c -> c.getCode().equalsIgnoreCase(code))
                         .findFirst()
                         .orElse(null);
        } catch (Exception e) {
            throw new RemoteException("Error finding course by code.", e);
        }
    }

    // Dans CourseServiceRemoteImpl.java (après avoir appelé courseDAO.findAll())
@Override
public List<Course> getAllCourses() throws RemoteException { 
    try {
        List<Course> courses = courseDAO.findAll(); // La session est encore ouverte ici
        
        for (Course course : courses) {
            if (course.getPrograms() != null) {
                org.hibernate.Hibernate.initialize(course.getPrograms()); // 🚀 SOLUTION MANUELLE 
            }
        }
        
        return courses; 
    } catch (Exception e) {
        throw new RemoteException("Error fetching all courses.", e);
    }
}

    @Override
    public void updateCourse(Course course) throws RemoteException {
        try {
             if (course.getId() == null || course.getId() <= 0) {
                 throw new RemoteException("Course ID is required for update.");
            }
            validateCourse(course);
            courseDAO.update(course);
        } catch (Exception e) {
            System.err.println("Error updating course: " + e.getMessage());
            throw new RemoteException("Server error updating course.", e);
        }
    }
    
    @Override
    public void deleteCourse(long courseId) throws RemoteException {
        try {
            Course courseToDelete = courseDAO.findById(courseId);
            if (courseToDelete != null) {
                courseDAO.delete(courseToDelete);
            } else {
                throw new RemoteException("Course with ID " + courseId + " not found for deletion.");
            }
        } catch (Exception e) {
            System.err.println("Error deleting course by ID: " + e.getMessage());
            throw new RemoteException("Server error deleting course.", e);
        }
    }
    
    @Override
    public void deleteCourse(Course course) throws RemoteException {
        if (course == null || course.getId() == null) {
            throw new RemoteException("Course object must be valid for deletion.");
        }
        try {
            courseDAO.delete(course); 
        } catch (Exception e) {
            System.err.println("Error deleting course by object: " + e.getMessage());
            throw new RemoteException("Server error deleting course.", e);
        }
    }
}