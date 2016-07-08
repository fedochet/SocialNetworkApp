package model;

import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;

/**
 * Created by roman on 05.07.2016.
 */

@Data
public class User {
    private int id;

    private String username;
    private String password;
    private String firstName;
    private String lastName;

    private LocalDate birthDate;
    private Instant registrationTime;

}
