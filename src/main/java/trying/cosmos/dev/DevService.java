package trying.cosmos.dev;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import trying.cosmos.dev.request.TestUserJoinRequest;
import trying.cosmos.entity.User;
import trying.cosmos.entity.component.Authority;
import trying.cosmos.entity.component.UserStatus;
import trying.cosmos.repository.UserRepository;
import trying.cosmos.service.UserService;
import trying.cosmos.service.request.UserLoginRequest;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Profile({"local", "dev"})
public class DevService {

    private final UserRepository userRepository;
    private final UserService userService;

    @Transactional
    public String createUser(TestUserJoinRequest request) {
        userRepository.save(new User(request.getEmail(), request.getPassword(), request.getName(), UserStatus.LOGOUT, Authority.USER));
        return userService.login(new UserLoginRequest(request.getEmail(), request.getPassword(), "deviceToken"));
    }
}
