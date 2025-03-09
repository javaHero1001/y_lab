package org.example.service;

public class NotificationService {
    public void sendEmailNotification(String to, String subject, String body) {
        System.out.println("Sending notification to " + to);
        System.out.println("Subject: " + subject);
        System.out.println("Body: " + body);
        System.out.println("Notification sent successfully to " + to);
    }
}