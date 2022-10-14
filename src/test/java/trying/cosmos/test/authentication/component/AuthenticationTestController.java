package trying.cosmos.test.authentication.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import trying.cosmos.auth.AuthKey;
import trying.cosmos.auth.AuthorityOf;
import trying.cosmos.entity.User;
import trying.cosmos.repository.UserRepository;

import static trying.cosmos.entity.component.Authority.ADMIN;
import static trying.cosmos.entity.component.Authority.USER;

@RestController
public class AuthenticationTestController {

    @Autowired
    UserRepository repository;

    @AuthorityOf(ADMIN)
    @GetMapping("/admin")
    public String testAdminRequest() {
        User user = repository.findById(AuthKey.get()).orElseThrow();
        return user.getName();
    }

    @AuthorityOf(USER)
    @GetMapping("/user")
    public String testUserRequest() {
        User user = repository.findById(AuthKey.get()).orElseThrow();
        return user.getName();
    }
}
