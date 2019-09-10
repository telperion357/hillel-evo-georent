package com.georent.domain;

import lombok.Data;
import javax.persistence.*;

@Data
@Entity
public class GeoRentUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String recoveryToken;
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private UserRole role;
}
