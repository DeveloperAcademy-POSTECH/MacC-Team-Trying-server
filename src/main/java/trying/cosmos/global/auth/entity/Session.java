package trying.cosmos.global.auth.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;
import trying.cosmos.domain.user.entity.User;
import trying.cosmos.domain.user.entity.UserStatus;

import javax.persistence.Id;

@Getter
@ToString
@RedisHash("session")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Session {

    @Id
    private String id;

    @Indexed
    private Long userId;

    private Authority authority;

    private UserStatus status;

    public Session(User user) {
        this.userId = user.getId();
        this.authority = user.getAuthority();
        this.status = user.getStatus();
    }
}
