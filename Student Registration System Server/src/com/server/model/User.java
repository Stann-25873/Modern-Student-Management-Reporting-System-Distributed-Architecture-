package com.server.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "app_user")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id",nullable = false)
    private long id; 

    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "email", unique = true, nullable = false) // NEW: Required for OTP/Forgot Password
    private String email;

    @Column(name = "first_name") // Optional: Matches your Registration UI
    private String firstName;

    @Column(name = "last_name")  // Optional: Matches your Registration UI
    private String lastName;

    @Column(name = "user_role", nullable = false)
    private String userRole = "STUDENT"; // Default role

    // --- Constructors ---
    public User() {}

    // --- Getters and Setters ---
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getUserRole() { return userRole; }
    public void setUserRole(String userRole) { this.userRole = userRole; }

   
}