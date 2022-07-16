package com.example.dossier.dto;


import com.example.dossier.enums.Gender;
import com.example.dossier.enums.MaritalStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Setter
@ToString

public class Client {


    Long id;

    String lastName;

    String firstName;

    String middleName;

    LocalDate birthDate;

    String email;

    Gender gender;

    MaritalStatus maritalStatus;

    Integer dependentAmount;

    Passport passport;

    Employment employment;

    String account;


}
