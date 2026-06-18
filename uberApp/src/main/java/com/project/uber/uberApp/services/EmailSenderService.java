package com.project.uber.uberApp.services;

public interface EmailSenderService {

    public void sendEmail(String toEmail,String Subject,String body);

    public void sendEmail(String toEmail[],String Subject,String body);
}
