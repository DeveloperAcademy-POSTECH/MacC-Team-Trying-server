package trying.cosmos.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import trying.cosmos.entity.component.Authority;
import trying.cosmos.entity.component.CreatedDateEntity;
import trying.cosmos.entity.component.UserStatus;
import trying.cosmos.exception.CustomException;
import trying.cosmos.exception.ExceptionType;
import trying.cosmos.utils.cipher.BCryptUtils;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static trying.cosmos.entity.component.UserStatus.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class User extends CreatedDateEntity {

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

    @OneToMany(mappedBy = "planet", cascade = CascadeType.ALL)
    private List<FollowPlanet> follows = new ArrayList<>();

    private static final String WITHDRAWN_USER_PREFIX = "[UNKNOWN] ";

    public User(String email, String password) {
        this.email = email;
        this.password = BCryptUtils.encrypt(password);
        this.status = UNCERTIFICATED;
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
    public void certificate() {
        if (!this.status.equals(UNCERTIFICATED)) {
            throw new CustomException(ExceptionType.CERTIFICATION_FAILED);
        }
        this.status = INCOMPLETE;
    }

    public void create(String name) {
        if (!this.status.equals(INCOMPLETE)) {
            throw new CustomException(ExceptionType.USER_CREATION_FAILED);
        }
        this.name = name;
        this.status = LOGOUT;
    }

    public void login(String deviceToken) {
        checkAccessibleUser();
        this.status = LOGIN;
        this.deviceToken = deviceToken;
    }

    public void resetPassword(String password) {
        checkAccessibleUser();
        this.password = password;
        this.status = LOGOUT;
    }

    public boolean isAccessibleUser() {
        return this.status.equals(LOGIN) || this.status.equals(LOGOUT);
    }

    public void update(String name, String password) {
        this.name = name;
        this.password = BCryptUtils.encrypt(password);
    }

    public void logout() {
        this.status = LOGOUT;
    }

    public void withdraw() {
        this.email = WITHDRAWN_USER_PREFIX + this.email;
        this.name = WITHDRAWN_USER_PREFIX + this.name;
        this.status = WITHDRAWN;
    }

    private void checkAccessibleUser() {
        switch (status) {
            case UNCERTIFICATED:
                throw new CustomException(ExceptionType.NOT_CERTIFICATED);
            case INCOMPLETE:
                throw new CustomException(ExceptionType.INCOMPLETE_CREATE_USER);
            case SUSPENDED:
                throw new CustomException(ExceptionType.SUSPENDED);
            case WITHDRAWN:
                throw new CustomException(ExceptionType.NO_DATA);
        }
    }
}
