package trying.cosmos.component;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import trying.cosmos.auth.AuthorityOf;
import trying.cosmos.auth.LoginMember;
import trying.cosmos.entity.Member;

import static trying.cosmos.entity.component.Authority.ADMIN;
import static trying.cosmos.entity.component.Authority.USER;

@RestController
public class AuthenticationTestController {

    @AuthorityOf(ADMIN)
    @GetMapping("/admin")
    public String testAdminRequest() {
        Member member = LoginMember.getLoginMember();
        return member.getName();
    }

    @AuthorityOf(USER)
    @GetMapping("/user")
    public String testUserRequest() {
        Member member = LoginMember.getLoginMember();
        return member.getName();
    }
}
