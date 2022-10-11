package trying.cosmos.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import trying.cosmos.entity.component.Authority;
import trying.cosmos.entity.component.MemberStatus;

import javax.persistence.*;

import static trying.cosmos.entity.component.MemberStatus.LOGIN;
import static trying.cosmos.entity.component.MemberStatus.LOGOUT;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    private String email;

    private String password;

    private String name;

    @Enumerated(EnumType.STRING)
    private MemberStatus status;

    @Enumerated(EnumType.STRING)
    private Authority authority;

    private String deviceToken;

    @Builder
    public User(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.status = LOGOUT;
        this.authority = Authority.USER;
        this.deviceToken = "";
    }

    @Builder
    public User(String email, String password, String name, Authority authority) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.status = LOGOUT;
        this.authority = authority;
        this.deviceToken = "";
    }

    // Convenient Method
    public void login(String deviceToken) {
        this.status = LOGIN;
        this.deviceToken = deviceToken;
    }

    public boolean isAccessibleUser() {
        return this.status.equals(LOGIN) || this.status.equals(LOGOUT);
    }
}
