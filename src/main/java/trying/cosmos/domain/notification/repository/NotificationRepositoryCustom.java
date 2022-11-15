package trying.cosmos.domain.notification.repository;

import trying.cosmos.domain.user.entity.User;

public interface NotificationRepositoryCustom {

    boolean existsUnreadNotification(User user);
}
