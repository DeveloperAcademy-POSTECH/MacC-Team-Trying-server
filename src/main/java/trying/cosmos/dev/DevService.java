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

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Profile({"local", "dev"})
public class DevService {

    private final UserRepository userRepository;
    private final UserService userService;

    private static final String MOCK_DEVICE_TOKEN = "deviceToken";

    @Transactional
    public String createUser(TestUserJoinRequest request) {
        userRepository.save(new User(request.getEmail(), request.getPassword(), request.getName(), UserStatus.LOGOUT, Authority.USER));
        return userService.login(request.getEmail(), request.getPassword(), MOCK_DEVICE_TOKEN);
    }
}
