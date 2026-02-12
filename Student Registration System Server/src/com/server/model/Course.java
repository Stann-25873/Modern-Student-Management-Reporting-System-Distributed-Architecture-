// File: StudentRegistrationSystemServer/src/com/server/model/Course.java
package com.server.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "course")
public class Course implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id; // Utilise 'id' de type Long (bigint dans DB)

    @Column(name = "code", unique = true, nullable = false, length = 10) // Ajusté à length=10
    private String code;

    @Column(name = "title", nullable = false, length = 200) // Ajusté à length=200
    private String title;

    @Column(name = "credits", nullable = false)
    private Integer credits;

  
    // Relationship: Many-to-Many with Program (inverse side: courses est mappé par programs)
    @ManyToMany(mappedBy = "courses", fetch = FetchType.EAGER) 
    private Set<Program> programs = new HashSet<>();

    // Relationship: One Course has many Enrollments
    // Laissez-la en LAZY car elle n'est pas nécessaire sur l'écran des cours.
    @OneToMany(mappedBy = "course", fetch = FetchType.EAGER, cascade = CascadeType.ALL) 
    private Set<Enrollment> enrollments = new HashSet<>();
    
   
    
    // --- Constructors ---
    public Course() {}
    
    

    // --- Getters and Setters (Harmonisés) ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public Integer getCredits() { return credits; } 
    public void setCredits(Integer credits) { this.credits = credits; } 
    
    public Set<Program> getPrograms() { return programs; }
    public void setPrograms(Set<Program> programs) { this.programs = programs; }
    
    public Set<Enrollment> getEnrollments() { return enrollments; }
    public void setEnrollments(Set<Enrollment> enrollments) { this.enrollments = enrollments; }
    
    /* Getter/Setter pour la nouvelle relation Department
    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }
    */
    @Override
    public String toString() {
        return code + " - " + title; 
    }
}