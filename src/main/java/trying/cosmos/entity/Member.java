package trying.cosmos.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import trying.cosmos.entity.component.Authority;
import trying.cosmos.entity.component.MemberStatus;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
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
    public Member(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.status = MemberStatus.LOGOUT;
        this.authority = Authority.USER;
        this.deviceToken = "";
    }

    // Convenient Method
    public void login() {
        this.status = MemberStatus.LOGIN;
    }
}
