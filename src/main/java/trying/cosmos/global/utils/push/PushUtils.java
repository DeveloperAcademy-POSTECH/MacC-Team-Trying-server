package trying.cosmos.global.utils.push;

import trying.cosmos.domain.notification.entity.NotificationTarget;
import trying.cosmos.domain.user.entity.User;

public interface PushUtils {

    void pushAll(String title, String body);
    void pushTo(User user, String title, String body, NotificationTarget type, Long targetId);
}
