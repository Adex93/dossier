package com.example.dossier.dto;

import com.example.dossier.enums.Status;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class ApplicationStatusHistoryDTO {
    Status status;
    LocalDateTime time;
    Status changeType;



}
