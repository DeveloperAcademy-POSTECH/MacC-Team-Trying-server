package trying.cosmos.domain.notification.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import trying.cosmos.domain.notification.entity.Notification;
import trying.cosmos.domain.notification.entity.NotificationTarget;
import trying.cosmos.domain.notification.repository.NotificationRepository;
import trying.cosmos.domain.user.entity.User;
import trying.cosmos.domain.user.repository.UserRepository;
import trying.cosmos.global.exception.CustomException;
import trying.cosmos.global.exception.ExceptionType;
import trying.cosmos.global.utils.push.PushUtils;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final PushUtils pushUtils;

    @Transactional
    public void create(User user, String title, String body, NotificationTarget target, Long targetId) {
        Notification notification = notificationRepository.save(new Notification(user, title, body, target, targetId));
        pushUtils.pushTo(user, title, body, notification);
    }

    public List<Notification> findByUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        return notificationRepository.findByUser(user);
    }

    @Transactional
    public void markAsRead(Long userId, Long eventId) {
        User user = userRepository.findById(userId).orElseThrow();
        Notification notification = notificationRepository.findById(eventId).orElseThrow();
        if (!notification.getUser().equals(user)) {
            throw new CustomException(ExceptionType.NO_DATA);
        }

        notification.check();
    }

    @Transactional
    public void delete(Long userId, Long eventId) {
        User user = userRepository.findById(userId).orElseThrow();
        Notification notification = notificationRepository.findById(eventId).orElseThrow();
        if (!notification.getUser().equals(user)) {
            throw new CustomException(ExceptionType.NO_DATA);
        }

        notificationRepository.delete(notification);
    }

    @Transactional
    public void deleteAll(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        notificationRepository.deleteByUser(user);
    }

    @Async
    @Transactional
    @Scheduled(cron = "0 0 0 * * *")
    public void clear() {
        notificationRepository.deleteExpired(LocalDate.now());
    }
}
