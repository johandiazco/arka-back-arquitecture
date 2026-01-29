package com.arkaback.ports.input.Notification;

import com.arkaback.entity.notification.Notification;

public interface SendNotification {
    //Envía una notificación
    Notification send(Notification notification);
}
