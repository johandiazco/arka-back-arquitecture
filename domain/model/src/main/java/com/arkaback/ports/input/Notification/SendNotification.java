package com.arkaback.ports.input.Notification;

import com.arkaback.entity.Notification;

public interface SendNotification {
    //Envía una notificación
    Notification send(Notification notification);
}
