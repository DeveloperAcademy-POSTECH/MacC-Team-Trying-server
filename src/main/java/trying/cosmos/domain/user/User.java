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
    public void login(String deviceToken) {
        checkAccessibleUser();
        this.status = UserStatus.LOGIN;
        this.deviceToken = deviceToken;
    }

    public void resetPassword(String password) {
        checkAccessibleUser();
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
        this.status = UserStatus.LOGOUT;
    }

    public void withdraw() {
        String prefix = createWithdrawPrefix();
        this.email = prefix + this.email;
        this.name = prefix + this.name;
        this.status = UserStatus.WITHDRAWN;
    }

    public String getOriginData(String data) {
        return data.substring(9);
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
            throw new CustomException(ExceptionType.PLANET_CREATE_FAILED);
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

    public String createWithdrawPrefix() {
        return "[" + RandomStringUtils.random(6, true, true) + "] ";
    }
}
