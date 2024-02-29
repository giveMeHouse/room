package com.sns.room.notification.controller;

import com.sns.room.global.jwt.UserDetailsImpl;
import com.sns.room.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Notification", description = "알림 컨트롤러")
public class NotificationController {

    private final NotificationService notificationService;
    public static Map<Long, SseEmitter> sseEmitters = new ConcurrentHashMap<>();

    @Operation(summary = "알림 생성", description = "알림 생성할 수 있는 API")
    @GetMapping("/notification/subscribe")
    public SseEmitter subscribe(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        SseEmitter sseEmitter = notificationService.subscribe(userDetails.getUser().getId());

        return sseEmitter;
    }

    @Operation(summary = "알림 삭제", description = "알림 삭제할 수 있는 API")
    @DeleteMapping("/notification/delete/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) throws IOException {
        notificationService.deleteNotification(id);
        return ResponseEntity.ok().build();
    }
}
