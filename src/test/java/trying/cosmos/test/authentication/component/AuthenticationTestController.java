package trying.cosmos.test.authentication.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import trying.cosmos.domain.user.User;
import trying.cosmos.domain.user.UserRepository;
import trying.cosmos.global.auth.AuthKey;
import trying.cosmos.global.auth.AuthorityOf;

import static trying.cosmos.global.auth.Authority.ADMIN;
import static trying.cosmos.global.auth.Authority.USER;

@RestController
public class AuthenticationTestController {

    @Autowired
    UserRepository repository;

    @AuthorityOf(ADMIN)
    @GetMapping("/admin")
    public String testAdminRequest() {
        User user = repository.findById(AuthKey.getKey()).orElseThrow();
        return user.getName();
    }

    @AuthorityOf(USER)
    @GetMapping("/user")
    public String testUserRequest() {
        User user = repository.findById(AuthKey.getKey()).orElseThrow();
        return user.getName();
    }
}
