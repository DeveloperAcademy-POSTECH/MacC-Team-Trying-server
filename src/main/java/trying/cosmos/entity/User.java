package trying.cosmos.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import trying.cosmos.entity.component.Authority;
import trying.cosmos.entity.component.DateEntity;
import trying.cosmos.entity.component.UserStatus;
import trying.cosmos.exception.CustomException;
import trying.cosmos.exception.ExceptionType;
import trying.cosmos.utils.cipher.BCryptUtils;

import javax.persistence.*;

import static trying.cosmos.entity.component.UserStatus.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class User extends DateEntity {

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

    private static final String WITHDRAWN_USER_PREFIX = "[UNKNOWN] ";

    public User(String email, String password, String name) {
        this.email = email;
        this.password = BCryptUtils.encrypt(password);
        this.name = name;
        this.status = LOGOUT;
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

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
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
            case SUSPENDED:
                throw new CustomException(ExceptionType.SUSPENDED);
            case WITHDRAWN:
                throw new CustomException(ExceptionType.NO_DATA);
        }
    }

    public Planet getPlanet() {
        if (this.planet == null) {
            throw new CustomException(ExceptionType.NO_DATA);
        }
        return planet;
    }

    public void setPlanet(Planet planet) {
        if (this.planet != null) {
            throw new CustomException(ExceptionType.CREATE_PLANET_FAILED);
        }
        this.planet = planet;
    }

    public boolean hasPlanet() {
        return planet != null;
    }

    public boolean hasMate() {
        return planet != null && planet.getMate(this) != null;
    }

    public User getMate() {
        if (planet == null) {
            return null;
        }
        return planet.getMate(this);
    }
}
