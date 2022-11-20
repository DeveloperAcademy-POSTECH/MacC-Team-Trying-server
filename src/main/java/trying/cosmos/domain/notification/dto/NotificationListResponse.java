package trying.cosmos.domain.notification.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import trying.cosmos.domain.notification.entity.Notification;

import java.util.List;
import java.util.stream.Collectors;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NotificationListResponse {

    private List<NotificationContent> notifications;

    public NotificationListResponse(List<Notification> notifications) {
        this.notifications = notifications.stream()
                .map(NotificationContent::new)
                .collect(Collectors.toList());
    }
}
