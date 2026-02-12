// File: StudentRegistrationSystemServer/src/com/server/model/RegistrationDetails.java
package com.server.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "registration_details")
public class RegistrationDetails implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id; // Utilisation de Long pour correspondre à bigint

    @Column(name = "registration_status", nullable = false)
    private String registrationStatus;
    
    // Relation Many-to-One vers Student
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    // Colonne 'academic_year'
    @Column(name = "academic_year", nullable = false)
    private String academicYear;

    // Colonne 'is_paid'
    @Column(name = "is_paid", nullable = false)
    private Boolean isPaid; 
    
    // Colonne 'registration_date'
    @Temporal(TemporalType.DATE)
    @Column(name = "registration_date", nullable = false)
    private Date registrationDate;

    // Colonne 'current_semester'
    @Column(name = "current_semester", nullable = false)
    private String currentSemester;
    
    // Colonne 'deadline'
    @Temporal(TemporalType.DATE)
    @Column(name = "deadline", nullable = false)
    private Date deadline;

    // --- Constructors ---
    public RegistrationDetails() {}

    // --- Getters and Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getRegistrationStatus() { return registrationStatus; }
    public void setRegistrationStatus(String registrationStatus) { this.registrationStatus = registrationStatus; }
    
    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }
    
    public String getAcademicYear() { return academicYear; }
    public void setAcademicYear(String academicYear) { this.academicYear = academicYear; }
    
    public Boolean getIsPaid() { return isPaid; }
    public void setIsPaid(Boolean isPaid) { this.isPaid = isPaid; }
    
    public Date getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(Date registrationDate) { this.registrationDate = registrationDate; }
    
    public String getCurrentSemester() { return currentSemester; }
    public void setCurrentSemester(String currentSemester) { this.currentSemester = currentSemester; }
    
    public Date getDeadline() { return deadline; }
    public void setDeadline(Date deadline) { this.deadline = deadline; }

    @Override
    public String toString() {
        return "RegDetails ID: " + id + " - Year: " + academicYear;
    }
}