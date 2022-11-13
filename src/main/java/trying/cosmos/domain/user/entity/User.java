package trying.cosmos.domain.user.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.RandomStringUtils;
import trying.cosmos.domain.planet.entity.Planet;
import trying.cosmos.global.auditing.DateAuditingEntity;
import trying.cosmos.global.auth.entity.Authority;
import trying.cosmos.global.exception.CustomException;
import trying.cosmos.global.exception.ExceptionType;
import trying.cosmos.global.utils.BCryptUtils;

import javax.persistence.*;

@ToString
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class User extends DateAuditingEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private boolean isSocialAccount;

    private String password;

    @Column(unique = true)
    private String identifier;

    @Column(unique = true, nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @Enumerated(EnumType.STRING)
    private Authority authority;

    private String deviceToken;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "planet_id")
    private Planet planet;

    @ToString.Exclude
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mate_id")
    private User mate;

    private User(String email, String password, String name) {
        this.email = email;
        this.password = BCryptUtils.encrypt(password);
        this.name = name;
        this.status = UserStatus.LOGOUT;
        this.authority = Authority.USER;
        this.deviceToken = "";
        this.isSocialAccount = false;
    }

    private User(String email, String password, String name, UserStatus status, Authority authority) {
        this.email = email;
        this.password = BCryptUtils.encrypt(password);
        this.name = name;
        this.status = status;
        this.authority = authority;
        this.deviceToken = "";
        this.isSocialAccount = false;
    }

    // Convenient Method
    public static User createEmailUser(String email, String password, String name, String deviceToken) {
        User user = new User();
        user.email = email;
        user.isSocialAccount = false;
        user.password = BCryptUtils.encrypt(password);
        user.identifier = null;
        user.name = name;
        user.status = UserStatus.LOGIN;
        user.authority = Authority.USER;
        user.deviceToken = deviceToken;
        return user;
    }

    public static User createSocialUser(String identifier, String email, String name, String deviceToken) {
        User user = new User();
        user.email = email;
        user.isSocialAccount = true;
        user.password = null;
        user.identifier = identifier;
        user.name = name;
        user.status = UserStatus.LOGIN;
        user.authority = Authority.USER;
        user.deviceToken = deviceToken;
        return user;
    }

    public void login(String deviceToken) {
        checkAccessibleUser();
        this.status = UserStatus.LOGIN;
        this.deviceToken = deviceToken;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void logout() {
        checkLoginUser();
        this.status = UserStatus.LOGOUT;
    }

    public void withdraw() {
        checkLoginUser();
        String prefix = createWithdrawPrefix();
        this.email = prefix + this.email;
        this.name = prefix + this.name;
        this.status = UserStatus.WITHDRAWN;
        if (this.planet != null) {
            this.planet.leave(this);
            this.planet = null;
        }
        if (this.mate != null) {
            this.mate.setMate(null);
            this.mate = null;
        }
    }

    public void setPlanet(Planet planet) {
        this.planet = planet;
    }

    public void setMate(User mate) {
        this.mate = mate;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public void checkPassword(String password) {
        if (!BCryptUtils.isMatch(password, this.password)) {
            throw new CustomException(ExceptionType.INVALID_PASSWORD);
        }
    }

    private String createWithdrawPrefix() {
        return "[" + RandomStringUtils.random(6, true, true) + "] ";
    }

    public void checkAccessibleUser() {
        switch (getStatus()) {
            case SUSPENDED:
                throw new CustomException(ExceptionType.SUSPENDED_USER);
            case WITHDRAWN:
                throw new CustomException(ExceptionType.NO_DATA);
        }
    }

    private void checkLoginUser() {
        checkAccessibleUser();
        if (!getStatus().equals(UserStatus.LOGIN)) {
            throw new CustomException(ExceptionType.NOT_AUTHENTICATED);
        }
    }
}
