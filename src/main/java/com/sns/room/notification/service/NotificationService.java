package com.sns.room.notification.service;

import com.sns.room.comment.entity.Comment;
import com.sns.room.comment.repository.CommentRepository;
import com.sns.room.notification.controller.NotificationController;
import com.sns.room.notification.entity.Notification;
import com.sns.room.notification.repository.NotificationRepository;
import com.sns.room.post.entity.Post;
import com.sns.room.post.service.PostService;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final PostService postService;
    private final CommentRepository commentRepository;

    private static Map<Long, Integer> notificationCounts = new HashMap<>();

    public SseEmitter subscribe(Long userId) {
        // 현재 클라이언트를 위한 sseEmitter 생성
        SseEmitter sseEmitter = new SseEmitter(Long.MAX_VALUE);
        try {
            // 연결
            sseEmitter.send(SseEmitter.event().name("connect"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // user 의 pk 값을 key 값으로 해서 sseEmitter 를 저장
        NotificationController.sseEmitters.put(userId, sseEmitter);

        // 4. 연결 종료 처리, 더이상 필요하지 않은 연결 정보를 정리
        sseEmitter.onCompletion(() -> NotificationController.sseEmitters.remove(userId));
        sseEmitter.onTimeout(() -> NotificationController.sseEmitters.remove(userId));
        sseEmitter.onError((e) -> NotificationController.sseEmitters.remove(userId));

        return sseEmitter;
    }

    public void notifyComment(Long postId) {
        Post post = postService.findPost(postId);

        Comment receiveComment = commentRepository.findFirstByPostIdOrderByCreatedAtDesc(
            post.getId()).orElseThrow(
            () -> new IllegalArgumentException("댓글을 찾을 수 없습니다.")
        );

        Long userId = post.getUser().getId();

        if (NotificationController.sseEmitters.containsKey(userId)) {
            SseEmitter sseEmitter = NotificationController.sseEmitters.get(userId);
            try {
                Map<String, String> eventData = new HashMap<>();
                eventData.put("sender", receiveComment.getUser().getUsername());
                eventData.put("contents", receiveComment.getComment());
                eventData.put("createdAt", receiveComment.getCreatedAt().toString());

                sseEmitter.send(SseEmitter.event().name("addComment").data(eventData));

                // DB 저장
                Notification notification = new Notification();
                notification.setSender(receiveComment.getUser().getUsername());
                notification.setContents(receiveComment.getComment());
                notification.setCreatedAt(receiveComment.getCreatedAt());
                notification.setPost(post);
                notificationRepository.save(notification);

                // 알림 개수 증가
                notificationCounts.put(userId, notificationCounts.getOrDefault(userId, 0) + 1);

                // 현재 알림 개수 전송
                sseEmitter.send(SseEmitter.event().name("notificationCount")
                    .data(notificationCounts.get(userId)));

            } catch (IOException e) {
                NotificationController.sseEmitters.remove(userId);
            }
        }
    }
}
