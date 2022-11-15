package trying.cosmos.domain.notification.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import trying.cosmos.domain.notification.dto.NotificationListResponse;
import trying.cosmos.domain.notification.service.NotificationService;
import trying.cosmos.global.auth.AuthorityOf;
import trying.cosmos.global.auth.entity.AuthKey;
import trying.cosmos.global.auth.entity.Authority;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @AuthorityOf(Authority.USER)
    @GetMapping
    public NotificationListResponse getEvents() {
        return new NotificationListResponse(notificationService.findByUser(AuthKey.getKey()));
    }

    @AuthorityOf(Authority.USER)
    @PostMapping("/{notificationId}")
    public void markAsRead(@PathVariable Long notificationId) {
        notificationService.markAsRead(AuthKey.getKey(), notificationId);
    }

    @AuthorityOf(Authority.USER)
    @DeleteMapping("/{notificationId}")
    public void delete(@PathVariable Long notificationId) {
        notificationService.delete(AuthKey.getKey(), notificationId);
    }

    @AuthorityOf(Authority.USER)
    @DeleteMapping
    public void deleteAll() {
        notificationService.deleteAll(AuthKey.getKey());
    }
}
