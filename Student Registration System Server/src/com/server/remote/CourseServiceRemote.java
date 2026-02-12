// File: StudentRegistrationSystemServer/src/com/server/remote/CourseServiceRemote.java
package com.server.remote;

import com.server.model.Course;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface CourseServiceRemote extends Remote {

    // CRUD de base
    void addCourse(Course course) throws RemoteException; 
    Course getCourseById(Long id) throws RemoteException;
    void updateCourse(Course course) throws RemoteException;
    List<Course> getAllCourses() throws RemoteException;
    
    // Suppression: deux surcharges pour la flexibilité
    void deleteCourse(Course course) throws RemoteException;
    void deleteCourse(long courseId) throws RemoteException; 

    // Méthode spécifique
    Course findCourseByCode(String code) throws RemoteException;
}