package trying.cosmos.domain.user;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import trying.cosmos.domain.common.DateAuditingEntity;
import trying.cosmos.domain.planet.Planet;
import trying.cosmos.global.auth.Authority;
import trying.cosmos.global.exception.CustomException;
import trying.cosmos.global.exception.ExceptionType;
import trying.cosmos.global.utils.cipher.BCryptUtils;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class User extends DateAuditingEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(unique = true)
    private String email;

    private String password;

    @Column(unique = true)
    private String name;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @Enumerated(EnumType.STRING)
    private Authority authority;

    private String deviceToken;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "planet_id")
    private Planet planet;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mate_id")
    private User mate;

    public User(String email, String password, String name) {
        this.email = email;
        this.password = BCryptUtils.encrypt(password);
        this.name = name;
        this.status = UserStatus.LOGOUT;
        this.authority = Authority.USER;
        this.deviceToken = "";
    }

    public User(String email, String password, String name, UserStatus status, Authority authority) {
        this.email = email;
        this.password = BCryptUtils.encrypt(password);
        this.name = name;
        this.status = status;
        this.authority = authority;
        this.deviceToken = "";
    }

    // Convenient Method
    public void login(String password, String deviceToken) {
        checkPassword(password);
        checkAccessibleUser();

        this.status = UserStatus.LOGIN;
        this.deviceToken = deviceToken;
    }

    public void resetPassword(String password) {
        this.password = password;
        this.status = UserStatus.LOGOUT;
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
    }

    public String getOriginEmail() {
        if (this.status.equals(UserStatus.WITHDRAWN)) {
            return this.email.substring(9);
        }
        return this.email;
    }

    public String getOriginName() {
        if (this.status.equals(UserStatus.WITHDRAWN)) {
            return this.name.substring(9);
        }
        return this.name;
    }

    public void setPlanet(Planet planet) {
        this.planet = planet;
    }

    public void setMate(User mate) {
        this.mate = mate;
    }

    private void checkPassword(String password) {
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
            throw new CustomException(ExceptionType.NOT_LOGIN);
        }
    }
}
