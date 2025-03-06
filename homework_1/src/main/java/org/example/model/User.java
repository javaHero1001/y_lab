package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private int id;
    private String username;
    private String firstname;
    private String lastname;
    private String email;
    private String password;
    private boolean isAdmin;
    private boolean isBlocked;

}
