package com.devrun.entity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "user")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "uno")
    private int uno;

    @Column(name = "id", length = 15, nullable = false)
    private String id;

    @Column(name = "name", length = 15, nullable = false)
    private String name;

    @Column(name = "password", length = 2000)
    private String password;

    @Column(name = "email", length = 50, nullable = false)
    private String email;

    @Column(name = "phonenumber", length = 13, nullable = false)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 10, nullable = false)
    private String status;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", length = 10, nullable = false)
    private String role;

    @Column(name = "signup", nullable = false)
    private LocalDate signup;

    @Column(name = "lastlogin")
    private LocalDate lastLogin;

    @Column(name = "export")
    private LocalDate export;

    // Getters and setters, constructors, and other methods

}
