package com.example.dossier.dto;

import com.example.dossier.enums.CreditStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@ToString

public class Credit {


    Long id;

    BigDecimal amount;

    Integer term;

    BigDecimal monthlyPayment;

    BigDecimal rate;

    BigDecimal psk;

    List<PaymentScheduleElement> paymentSchedule;

    Boolean isInsuranceEnabled;

    Boolean isSalaryClient;

    CreditStatus creditStatus;


}
