package com.arkaback.useCase.Notification;

import com.arkaback.entity.notification.Notification;
import com.arkaback.entity.notification.NotificationChannel;
import com.arkaback.ports.input.Notification.SendNotification;
import com.arkaback.ports.output.EmailServicePort;
import com.arkaback.ports.output.NotificationPersistencePort;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SendNotificationUseCase implements SendNotification {

    private final NotificationPersistencePort notificationPersistencePort;
    private final EmailServicePort emailServicePort;

    @Override
    public Notification send(Notification notification) {
        //Guarda notificación en la BD-estado PENDING
        Notification savedNotification = notificationPersistencePort.save(notification);

        //Envia según el canal
        boolean sent = false;

        if (notification.getChannel() == NotificationChannel.EMAIL) {
            sent = sendEmailNotification(notification);
        }

        // Actualiza estado según resultado
        Notification finalNotification = sent
                ? savedNotification.markAsSent()
                : savedNotification.markAsFailed();

        return notificationPersistencePort.save(finalNotification);
    }

    private boolean sendEmailNotification(Notification notification) {
        try {
            String recipientEmail = notification.getRecipient().getEmail();
            return emailServicePort.sendEmail(
                    recipientEmail,
                    notification.getSubject(),
                    notification.getMessage()
            );
        } catch (Exception e) {
            return false;
        }
    }
}
