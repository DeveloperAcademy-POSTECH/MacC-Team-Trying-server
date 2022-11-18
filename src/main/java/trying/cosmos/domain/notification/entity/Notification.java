package trying.cosmos.domain.notification.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import trying.cosmos.domain.user.entity.User;
import trying.cosmos.global.auditing.DateAuditingEntity;

import javax.persistence.*;
import java.time.LocalDate;

@ToString
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends DateAuditingEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String title;

    private String body;

    @Enumerated(EnumType.STRING)
    private NotificationTarget target;

    private Long targetId;

    private boolean isChecked;

    private LocalDate expiredDate;

    private static final int LIFETIME = 7;

    public Notification(User user, String title, String body, NotificationTarget target, Long targetId) {
        this.user = user;
        this.title = title;
        this.body = body;
        this.target = target;
        this.targetId = targetId;
        this.isChecked = false;
        this.expiredDate = LocalDate.now().plusDays(LIFETIME);
    }

    public void check() {
        this.isChecked = true;
    }
}
