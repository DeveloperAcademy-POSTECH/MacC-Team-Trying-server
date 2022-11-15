package trying.cosmos.domain.notification.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import trying.cosmos.domain.notification.entity.Notification;
import trying.cosmos.global.utils.DateUtils;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NotificationContent {

    private Long notificationId;
    private String title;
    private String body;
    private String target;
    private Long targetId;
    private String createdDate;
    private boolean isChecked;

    public NotificationContent(Notification notification) {
        this.notificationId = notification.getId();
        this.title = notification.getTitle();
        this.body = notification.getBody();
        this.target = notification.getTarget().toString();
        this.targetId = notification.getTargetId();
        this.createdDate = DateUtils.getFormattedDate(notification.getCreatedDate(), "yyyy-MM-dd HH:mm:ss");
        this.isChecked = notification.isChecked();
    }
}
