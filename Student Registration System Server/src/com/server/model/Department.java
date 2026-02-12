// File: StudentRegistrationSystemServer/src/com/server/model/Department.java
package com.server.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "department")
public class Department implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", unique = true, nullable = false, length = 100)
    private String name;

    @Column(name = "head_of_dept", length = 100)
    private String headOfDept; // Maintenu du modèle original pour la cohérence

    @Column(name = "code", unique = true, nullable = false, length = 255)
    private String code;

    // Relationship: One Department has many Programs
    @OneToMany(mappedBy = "department", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Program> programs = new HashSet<>();

    // Relationship: One Department has many Students
    @OneToMany(mappedBy = "department", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Student> students = new HashSet<>();

    public Department() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getHeadOfDept() { return headOfDept; }
    public void setHeadOfDept(String headOfDept) { this.headOfDept = headOfDept; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    
    public Set<Program> getPrograms() { return programs; }
    public void setPrograms(Set<Program> programs) { this.programs = programs; }

    public Set<Student> getStudents() { return students; }
    public void setStudents(Set<Student> students) { this.students = students; }
    
    

    @Override
    public String toString() {
        return this.name;
    }
}