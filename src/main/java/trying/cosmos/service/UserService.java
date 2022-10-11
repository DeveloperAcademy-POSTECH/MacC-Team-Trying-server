package trying.cosmos.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import trying.cosmos.auth.TokenProvider;
import trying.cosmos.entity.User;
import trying.cosmos.exception.CustomException;
import trying.cosmos.exception.ExceptionType;
import trying.cosmos.repository.UserRepository;
import trying.cosmos.service.request.UserJoinRequest;
import trying.cosmos.service.request.UserLoginRequest;
import trying.cosmos.utils.cipher.BCryptUtils;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;

    @Transactional
    public User join(UserJoinRequest request) {
        return userRepository.save(
                User.builder()
                        .email(request.getEmail())
                        .password(BCryptUtils.encrypt(request.getPassword()))
                        .name(request.getName())
                        .build()
        );
    }

    @Transactional
    public String login(UserLoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        checkPassword(request, user);
        user.login(request.getDeviceToken());
        return tokenProvider.getAccessToken(user);
    }

    private static void checkPassword(UserLoginRequest request, User user) {
        if (!BCryptUtils.isMatch(request.getPassword(), user.getPassword())) {
            throw new CustomException(ExceptionType.INVALID_PASSWORD);
        }
    }
}
