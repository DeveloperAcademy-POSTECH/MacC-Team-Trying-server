package trying.cosmos.domain.notification.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import trying.cosmos.domain.user.entity.User;

import static trying.cosmos.domain.notification.entity.QNotification.notification;

@RequiredArgsConstructor
public class NotificationRepositoryImpl implements NotificationRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public boolean existsUnreadNotification(User user) {
        Integer fetchOne = queryFactory
                .selectOne()
                .from(notification)
                .where(
                        notification.user.eq(user),
                        notification.isChecked.isFalse()
                )
                .fetchFirst();

        return fetchOne != null;
    }
}
