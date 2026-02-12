// File: StudentRegistrationSystemServer/src/com/server/dao/CourseDAO.java
package com.server.dao;

import com.server.model.Course;
import java.io.Serializable;

public class CourseDAO extends BaseHibernateDAO<Course, Long> implements Serializable {

    private static final long serialVersionUID = 1L;

    public CourseDAO() {
        super(Course.class);
    }
    
}