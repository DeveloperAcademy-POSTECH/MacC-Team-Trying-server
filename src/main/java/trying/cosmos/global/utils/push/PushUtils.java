package trying.cosmos.global.utils.push;

import trying.cosmos.domain.notification.entity.Notification;
import trying.cosmos.domain.user.entity.User;

public interface PushUtils {

    void pushAll(String title, String body);
    void pushTo(User user, String title, String body, Notification notification);
}
