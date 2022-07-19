package com.example.dossier.dto;

import com.example.dossier.enums.Theme;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class EmailMessage {

    String address;
    Theme theme;
    Long applicationId;

}
