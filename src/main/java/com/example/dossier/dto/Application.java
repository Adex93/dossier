package com.example.dossier.dto;


import com.example.dossier.enums.Status;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@ToString

public class Application {


    Long id;


    Client client;


    Credit credit;


    Status status;

    LocalDate creationDate;

    LoanOfferDTO appliedOffer;

    LocalDate signDate;

    Integer sesCode;


    List<ApplicationStatusHistoryDTO> statusHistory;

}
