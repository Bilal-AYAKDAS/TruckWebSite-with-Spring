package org.truckwebsite.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private String uid;
    private String name;
    private String surname;
    private String birthDate;
    private String userType;
    private String email;
    private String password;
    private String phone;
}
