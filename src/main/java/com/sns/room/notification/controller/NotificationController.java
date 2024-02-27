package com.sns.room.notification.controller;

import com.sns.room.global.jwt.UserDetailsImpl;
import com.sns.room.notification.service.NotificationService;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    public static Map<Long, SseEmitter> sseEmitters = new ConcurrentHashMap<>();

    @GetMapping("/notification/subscribe")
    public SseEmitter subscribe(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        SseEmitter sseEmitter = notificationService.subscribe(userDetails.getUser().getId());

        return sseEmitter;
    }

    @DeleteMapping("/notification/delete/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) throws IOException {
        notificationService.deleteNotification(id);
        return ResponseEntity.ok().build();
    }
}
