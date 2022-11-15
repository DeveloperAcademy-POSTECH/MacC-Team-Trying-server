package trying.cosmos.domain.notification.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import trying.cosmos.domain.notification.entity.Notification;
import trying.cosmos.domain.user.entity.User;

import java.time.LocalDate;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long>, NotificationRepositoryCustom {

    @Query("select n from Notification n where n.user = :user order by n.createdDate desc")
    List<Notification> findByUser(User user);

    @Modifying
    @Query("delete from Notification n where n.expiredDate <= :now")
    void deleteExpired(LocalDate now);

    void deleteByUser(User user);
}
