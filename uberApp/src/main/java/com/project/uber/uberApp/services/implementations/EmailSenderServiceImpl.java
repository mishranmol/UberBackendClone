package com.project.uber.uberApp.services.implementations;

import com.project.uber.uberApp.services.EmailSenderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailSenderServiceImpl implements EmailSenderService {
    /*
    Added dependency --> spring-boot-starter-mail is a Spring Boot Starter Dependency that provides everything needed to send emails from a Spring Boot application.
       +
Added SMTP Config. inside application.properties
       |
       v
Spring Boot Auto Configuration
       |
Creates JavaMailSender Bean
       |
       v
@Autowired/Constructor Injection works
     */
//spring-boot-starter-mail Dependency + SMTP configuration in application.properties automatically creates a "JavaMailSender" bean, which can then
//be injected and used to send emails.

    private final JavaMailSender javaMailSender;

    @Override
    public void sendEmail(String toEmail, String Subject, String body) {

        //To test this JavaMailSender we are checking by passing a @Test inside @UberAppApplicationTests
        try{
            //To send the email we will be creating an email message using SimpleMailMessage.
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();

            //setTo -> Email of the Receiver
            simpleMailMessage.setTo(toEmail);
            simpleMailMessage.setSubject(Subject);
            simpleMailMessage.setText(body);

            //Will use javaMailSender to send the email .
            //javaMailSender.send() takes in a MimeMessage(Note->MimeMessage is an interface) as an input and this MimeMessage is being implemented
            //by SimpleMailMessage .
            javaMailSender.send(simpleMailMessage);
            log.info("Email Sent Successfully");

        } catch (Exception e){
            System.out.println("The error is " + e.getMessage());
        }
    }

    //This will send same Email to many user's since we are taking array of toEmail[].
    @Override
    public void sendEmail(String[] toEmail, String Subject, String body) {

        try{
            //To send the email we will be creating an email message using SimpleMailMessage.
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();

            //setTo -> Email of the Receiver
            simpleMailMessage.setTo(toEmail);
            simpleMailMessage.setSubject(Subject);
            simpleMailMessage.setText(body);

            //Will use javaMailSender to send the email .
            //javaMailSender.send() takes in a MimeMessage(Note->MimeMessage is an interface) as an input and this MimeMessage is being implemented
            //by SimpleMailMessage .
            javaMailSender.send(simpleMailMessage);
            log.info("Email Sent Successfully");

        } catch (Exception e){
            System.out.println("The error is " + e.getMessage());
        }

    }
}

