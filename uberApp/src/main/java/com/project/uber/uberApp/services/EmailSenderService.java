package com.project.uber.uberApp.services;

public interface EmailSenderService {

    //"toEmail" -> to whom you want to send Email , Subject->Subject of Email, body->Body of Email
    public void sendEmail(String toEmail,String Subject,String body);

    public void sendEmail(String toEmail[],String Subject,String body);
}
