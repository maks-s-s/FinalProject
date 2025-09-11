package com.epam.rd.autocode.spring.project.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "EMPLOYEES")
@Getter
@Setter
@NoArgsConstructor
public class Employee extends User {

    @Column(name = "BIRTH_DATE", nullable = false)
    private java.time.LocalDate birthDate;

    @Column(nullable = false)
    private String phone;

    public Employee(Long id, String email, String password, String name,
                    java.time.LocalDate birthDate, String phone) {
        super(id, email, password, name);
        this.birthDate = birthDate;
        this.phone = phone;
    }
}
