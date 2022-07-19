package com.example.dossier.services;

import com.example.dossier.clients.FeignConveyor;
import com.example.dossier.dto.Application;
import com.example.dossier.dto.Client;
import com.example.dossier.dto.Credit;
import com.example.dossier.dto.EmailMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;

import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;

@Slf4j
@Service
public class EmailService {

    @Value("${spring.mail.username}")
    String smtp;

    @Value("${dossier.link.finishRegistration}")
    String finishLink;

    @Value("${dossier.link.createDocuments}")
    String docsLink;

    @Value("${dossier.link.createSES}")
    String sesLink;

    @Value("${dossier.link.enterSES}")
    String enterLink;

    @Value("${dossier.link.clientDenied}")
    String clientDeniedLink;

    public final JavaMailSender emailSender;

    final
    FeignConveyor feignConveyor;

    public EmailService(JavaMailSender emailSender, FeignConveyor conveyor) {
        this.emailSender = emailSender;
        this.feignConveyor = conveyor;
    }

    public void sendSimpleEmail(String toAddress, String subject, String message) {
        log.info("Вызвана функция sendSimpleEmail класса EmailService для осуществления отправки письма без вложений");
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(smtp);
        simpleMailMessage.setTo(toAddress);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(message);
        emailSender.send(simpleMailMessage);

    }

    public void sendFinishRegistration(EmailMessage emailMessage) {
        log.info("Вызвана функция sendFinishRegistration класса EmailService для отправки письма по DTO EmailMessage: " + emailMessage);
        String message = "Ваша заявка №" + emailMessage.getApplicationId() + " предварительно одобрена!\n"
                + "Для завершения оформления перейдите по указанной ссылке и завершите регистрацию:\n"
                + finishLink;


        sendSimpleEmail(emailMessage.getAddress(), "Завершение регистрации", message);

    }

    public void sendCreateDocuments(EmailMessage emailMessage) {
        log.info("Вызвана функция sendCreateDocuments класса EmailService для отправки письма по DTO EmailMessage: " + emailMessage);
        String message = "Ваша заявка № " + emailMessage.getApplicationId() + " прошла все проверки и одобрена!\n"
                + "Для формирования документов перейдите по указанной ссылке и укажите Id вашей заявки:\n"
                + docsLink;

        sendSimpleEmail(emailMessage.getAddress(), "Создание документов", message);

    }

    public void sendSES(EmailMessage emailMessage) {
        log.info("Вызвана функция sendSES класса EmailService для отправки письма по DTO EmailMessage: " + emailMessage);
        Application application = feignConveyor.getApplication(emailMessage.getApplicationId()).getBody();

        String message = "Для подписания документов и выдаче кредита по заявке № " + emailMessage.getApplicationId() + " перейдите по указанной ссылке и введите код "
                + application.getSesCode() + "\n"
                + enterLink;

        sendSimpleEmail(emailMessage.getAddress(), "Подписание документов", message);

    }

    public void congratulation(EmailMessage emailMessage) {
        log.info("Вызвана функция congratulation класса EmailService для отправки письма по DTO EmailMessage: " + emailMessage);

        String message = "Поздравляем! Вам выдан кредит по заявке № " + emailMessage.getApplicationId() + "!\n"
                + "Денежные средства скоро поступят на Ваш счёт";

        sendSimpleEmail(emailMessage.getAddress(), "Выдача кредита", message);
    }

    public void deniedCredit(EmailMessage emailMessage) {
        log.info("Вызвана функция congratulation класса EmailService для отправки письма по DTO EmailMessage: " + emailMessage);
        String message = "По вашему запросу заявка о выдаче кредита № " + emailMessage.getApplicationId() + " закрыта.";

        sendSimpleEmail(emailMessage.getAddress(), "Закрытие заявки", message);
    }


    public void sendDocuments(EmailMessage emailMessage) throws IOException, MessagingException {
        log.info("Вызвана функция sendDocuments класса EmailService для отправки письма c вложением документов по DTO EmailMessage: " + emailMessage);
        log.info("Вызвана функция getApplication класса FeignConveyor для запроса в MS Deal для получения Application");
        Application application = feignConveyor.getApplication(emailMessage.getApplicationId()).getBody();
        String message = "Необходимые документы по Вашей заявке № " + emailMessage.getApplicationId() + " созданы!\n"
                + "Для получения кода для подписания указанных документов перейдите по указанной ссылке и укажите Id вашей заявки:\n"
                + sesLink + "\n"
                + "Если вы хотите отказаться от кредита и закрыть заявку - перейдите по указанной ссылке и укажите Id вашей заявки:\n" +
                clientDeniedLink;

        MimeMessage mimeMessage = emailSender.createMimeMessage();
        mimeMessage.setFrom(smtp);
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
        messageHelper.setTo(emailMessage.getAddress());
        messageHelper.setSubject("Формирование простой электронной подписи");
        messageHelper.setText(message);
        messageHelper.addAttachment("Credit-application.txt", createApplicationDocument(application));
        messageHelper.addAttachment("Credit-contract.txt", createContractDocument(application));
        messageHelper.addAttachment("Credit-payment-schedule.txt", createPaymentSchedule(application));

        log.info("Вызвана функция updateStatus класса FeignConveyor для запроса в MS Deal для обновления статуса Application");
        feignConveyor.updateStatus(emailMessage.getApplicationId());
        emailSender.send(mimeMessage);
    }

    public InputStreamSource createApplicationDocument(Application application) throws IOException {

        log.info("Вызвана функция createApplicationDocument класса EmailService для формирования Credit-application.txt по заявке с id " + application.getId());
        Client client = application.getClient();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(("Credit application №" + application.getId() + " from " + application.getCreationDate() + "\n").getBytes());
        outputStream.write(("Client info:\n").getBytes());
        outputStream.write(("full name: " + client.getLastName() + " " + client.getFirstName() + " " + client.getMiddleName() + "\n").indent(2).getBytes());
        outputStream.write(("gender: " + client.getGender() + "\n").indent(2).getBytes());
        outputStream.write(("passport: " + client.getPassport().getNumber() + " issued "
                + client.getPassport().getIssueDate() + " branch code " + client.getPassport().getIssueBranch() + "\n").indent(2).getBytes());
        outputStream.write(("email: " + client.getEmail() + "\n").indent(2).getBytes());
        outputStream.write(("martial status: " + client.getMaritalStatus() + "\n").indent(2).getBytes());
        outputStream.write(("dependent amount: " + client.getDependentAmount() + "\n").indent(2).getBytes());
        outputStream.write(("Employment:\n").indent(2).getBytes());
        outputStream.write(("employment status: " + client.getEmployment().getEmploymentStatus() + "\n").indent(4).getBytes());
        outputStream.write(("employer INN: " + client.getEmployment().getEmployer() + "\n").indent(4).getBytes());
        outputStream.write(("salary: " + client.getEmployment().getSalary() + "\n").indent(4).getBytes());
        outputStream.write(("employment position: " + client.getEmployment().getPosition() + "\n").indent(4).getBytes());
        outputStream.write(("work experience (total): " + client.getEmployment().getWorkExperienceTotal() + "\n").indent(4).getBytes());
        outputStream.write(("work experience (current):" + client.getEmployment().getWorkExperienceCurrent() + "\n").indent(4).getBytes());
        outputStream.close();

        return new ByteArrayResource(outputStream.toByteArray());
    }

    public InputStreamSource createContractDocument(Application application) throws IOException {

        log.info("Вызвана функция createContractDocument класса EmailService для формирования Credit-contract.txt по заявке с id " + application.getId());
        Client client = application.getClient();
        Credit credit = application.getCredit();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(("Credit contract №" + application.getId() + " from " + LocalDate.now() + "\n").getBytes());
        outputStream.write(("Client's full name: " + client.getLastName() + " " + client.getFirstName() + " " + client.getMiddleName() + "\n").getBytes());
        outputStream.write(("Client's passport: " + client.getPassport().getNumber() + " issued "
                + client.getPassport().getIssueDate() + " branch code " + client.getPassport().getIssueBranch() + "\n").getBytes());
        outputStream.write(("Credit info:\n").getBytes());
        outputStream.write(("amount: " + credit.getAmount() + "\n").indent(2).getBytes());
        outputStream.write(("term: " + credit.getTerm() + "\n").indent(2).getBytes());
        outputStream.write(("monthly payment: " + credit.getMonthlyPayment() + "\n").indent(2).getBytes());
        outputStream.write(("rate: " + credit.getRate() + "\n").indent(2).getBytes());
        outputStream.write(("psk: " + credit.getPsk() + "\n").indent(2).getBytes());
        outputStream.write(("services:\n").indent(2).getBytes());
        outputStream.write(("insurance: " + credit.getIsInsuranceEnabled() + "\n").indent(4).getBytes());
        outputStream.write(("salary client: " + credit.getIsSalaryClient() + "\n").indent(4).getBytes());
        outputStream.close();
        return new ByteArrayResource(outputStream.toByteArray());
    }

    public InputStreamSource createPaymentSchedule(Application application) throws IOException {

        log.info("Вызвана функция createContractDocument класса EmailService для формирования Credit-payment-schedule.txt по заявке с id " + application.getId());
        Credit credit = application.getCredit();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        String value = mapper.writeValueAsString(credit.getPaymentSchedule());

        outputStream.write(value.getBytes());
        outputStream.close();

        return new ByteArrayResource(outputStream.toByteArray());
    }

}
