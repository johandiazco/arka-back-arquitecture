package com.arkaback.useCase.Notification;

import com.arkaback.entity.Notification;
import com.arkaback.entity.NotificationChannel;
import com.arkaback.ports.input.Notification.SendNotification;
import com.arkaback.ports.output.EmailServicePort;
import com.arkaback.ports.output.NotificationPersistencePort;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SendNotificationUseCase implements SendNotification {

    private final NotificationPersistencePort notificationPersistencePort;
    private final EmailServicePort emailServicePort;
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(SendNotificationUseCase.class);

    @Override
    public Notification send(Notification notification) {
        //Guarda notificación en la BD (estado PENDING)
        Notification savedNotification = notificationPersistencePort.save(notification);

        //Envia según el canal
        boolean sent = false;

        if (notification.getChannel() == NotificationChannel.EMAIL) {
            sent = sendEmailNotification(notification);
        } else if (notification.getChannel() == NotificationChannel.SMS) {
            // TODO: Implementar SMS (ej: AWS SNS)
            log.warn("SMS notifications not implemented yet");
        } else if (notification.getChannel() == NotificationChannel.PUSH) {
            // TODO: Implementar PUSH (ej: Firebase)
            log.warn("Push notifications not implemented yet");
        }

        //Actualiza estado segun el resultado
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
            log.error("Error sending email notification: {}", e.getMessage(), e);
            return false;
        }
    }
}
