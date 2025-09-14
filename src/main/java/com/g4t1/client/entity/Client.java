package com.g4t1.client.entity;

import java.time.LocalDate;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "clients")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Client {

    @Id
    @Column(name = "client_id", updatable = false, nullable = false)
    private String id;

    @Size(min = 2, max = 50)
    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Size(min = 2, max = 50)
    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Past
    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Size(min = 1, max = 20)
    @Column(name = "gender", nullable = false, length = 20)
    private String gender;

    @Email
    @Column(name = "email_address", nullable = false, length = 50)
    private String emailAddress;

    @Column(name = "phone_number", nullable = false, length = 20)
    private String phoneNumber;

    @Size(min = 5, max = 100)
    @Column(name = "address", nullable = false, length = 100)
    private String address;

    @Size(min = 2, max = 50)
    @Column(name = "city", nullable = false, length = 50)
    private String city;

    @Size(min = 2, max = 50)
    @Column(name = "state", nullable = false, length = 50)
    private String state;

    @Size(min = 2, max = 50)
    @Column(name = "country", nullable = false, length = 50)
    private String country;

    @Size(min = 4, max = 10)
    @Column(name = "postal_code", nullable = false, length = 10)
    private String postalCode;

    @Column(name = "validated", nullable = false)
    private boolean validated;

}
