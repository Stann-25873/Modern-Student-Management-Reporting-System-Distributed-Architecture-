// File: StudentRegistrationSystemServer/src/com/server/model/Enrollment.java
package com.server.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date; // Added for enrollment_date

@Entity
@Table(name = "enrollment")
public class Enrollment implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // Relationship: Many-to-One with Student
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    // Relationship: Many-to-One with Course
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(name = "semester", length = 50)
    private String semester; 

    @Column(name = "grade", length = 5)
    private String grade;

    @Temporal(TemporalType.DATE)
    @Column(name = "enrollment_date", nullable = false) // Added based on DB schema
    private Date enrollmentDate;

    // The 'enrollment_id' column in DB is likely a foreign key to the primary ID (id), 
    // but its presence in the table definition seems redundant/misleading 
    // since 'id' is the primary key. We map the primary key 'id' only.

    // --- Constructors ---
    public Enrollment() {}

    // --- Getters and Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }
    
    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }
    
    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }
    
    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }

    public Date getEnrollmentDate() { return enrollmentDate; }
    public void setEnrollmentDate(Date enrollmentDate) { this.enrollmentDate = enrollmentDate; }
}