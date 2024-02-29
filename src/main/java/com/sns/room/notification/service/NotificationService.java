package com.sns.room.notification.service;

import com.sns.room.comment.entity.Comment;
import com.sns.room.comment.service.CommentService;
import com.sns.room.follow.entity.Follow;
import com.sns.room.follow.service.FollowService;
import com.sns.room.like.entity.Like;
import com.sns.room.like.service.LikeService;
import com.sns.room.notification.controller.NotificationController;
import com.sns.room.notification.entity.Notification;
import com.sns.room.notification.repository.NotificationRepository;
import com.sns.room.post.entity.Post;
import com.sns.room.post.service.PostService;
import com.sns.room.user.service.AuthService;
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
    private final AuthService authService;
    private final PostService postService;
    private final CommentService commentService;
    private final LikeService likeService;
    private final FollowService followService;

    private static Map<Long, Integer> notificationCounts = new HashMap<>();

    public SseEmitter subscribe(Long userId) { // userId : 알림 받을 계정
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

    public void notifyComment(Long postId) { // postId : 게시글의 주인 (알림 받을 계정)
        Post post = postService.findPost(postId);
        Comment receiveComment = commentService.findLatestComment(postId); // 알림에 담을 댓글 객체
        long userId = postService.findPost(postId).getUser().getId(); // 알림을 받을 유저.

        if (NotificationController.sseEmitters.containsKey(userId)) {
            SseEmitter sseEmitter = NotificationController.sseEmitters.get(
                userId); //userId가 알림을 받을 유저 설정. (sseEmitter객체에 알림을 받을 유저의 정보가 들어있음)
            try {
                Map<String, String> eventData = new HashMap<>();
                eventData.put("sender",
                    receiveComment.getUser().getUsername() + " 님이 댓글을 작성했습니다."); //댓글 작성자
                eventData.put("contents", receiveComment.getComment()); //댓글 내용

                sseEmitter.send(SseEmitter.event().name("addComment").data(eventData));

                // DB 저장
                Notification notification = new Notification();
                notification.setPostId(post.getId());
                notification.setSender(receiveComment.getUser().getUsername());
                notification.setContents(receiveComment.getComment());
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

    public void notifyLike(Long postId) {
        Post post = postService.findPost(postId);
        Like receiveLike = likeService.findLatestLike(postId);
        long userId = postService.findPost(postId).getUser().getId();

        if (NotificationController.sseEmitters.containsKey(userId)) {
            SseEmitter sseEmitter = NotificationController.sseEmitters.get(userId);
            try {
                Map<String, String> eventData = new HashMap<>();
                eventData.put("contents",
                    authService.findUser(receiveLike.getUserId()).getUsername()
                        + "님이 회원님의 게시물을 좋아합니다.❤\uFE0F");

                sseEmitter.send(SseEmitter.event().name("addLike").data(eventData));

                // DB 저장
                Notification notification = new Notification();
                notification.setPostId(postId);
                notification.setContents("좋아요");
                notification.setSender(authService.findUser(receiveLike.getUserId()).getUsername());
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

    public void notifyFollow(Long toUserId) {
        long userId = authService.findUser(toUserId).getId();
        Follow follow = followService.findLatestUser(toUserId);

        if (NotificationController.sseEmitters.containsKey(userId)) {
            SseEmitter sseEmitter = NotificationController.sseEmitters.get(userId);
            try {
                Map<String, String> eventData = new HashMap<>();
                eventData.put("sender", authService.findUser(follow.getFromUserId()).getUsername());
                eventData.put("contents", authService.findUser(follow.getFromUserId()).getUsername()
                    + "님이 팔로우 요청을 보냈습니다.");

                sseEmitter.send(SseEmitter.event().name("addFollow").data(eventData));

                // DB 저장
                Notification notification = new Notification();
                notification.setUserId(authService.findUser(userId).getId());
                notification.setContents("팔로우 요청");
                notification.setSender(authService.findUser(follow.getFromUserId()).getUsername());
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


    // 알림 삭제
    public void deleteNotification(Long id) throws IOException {
        Notification notification = notificationRepository.findById(id).orElseThrow(
            () -> new IllegalArgumentException("알림을 찾을 수 없습니다.")
        );

        Long userId = 0L; // 알림 받는 유저의 유저아이디.

        if (notification.getPostId() != null) { // 게시글의 좋아요, 댓글 알림일 경우
            userId = postService.findPost(notification.getPostId()).getUser().getId();
        } else { // 팔로우 일경우
            userId = notification.getUserId();
        }

        notificationRepository.delete(notification);

        // 알림 개수 감소
        if (notificationCounts.containsKey(userId)) {
            int currentCount = notificationCounts.get(userId);
            if (currentCount > 0) {
                notificationCounts.put(userId, currentCount - 1);
            }
        }

        // 현재 알림 개수 전송
        SseEmitter sseEmitter = NotificationController.sseEmitters.get(userId);
        sseEmitter.send(
            SseEmitter.event().name("notificationCount").data(notificationCounts.get(userId)));
    }
}
