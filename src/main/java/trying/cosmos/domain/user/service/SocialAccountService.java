package trying.cosmos.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import trying.cosmos.domain.user.entity.User;
import trying.cosmos.domain.user.repository.UserRepository;
import trying.cosmos.global.auth.SessionService;
import trying.cosmos.global.auth.TokenProvider;
import trying.cosmos.global.auth.entity.Session;
import trying.cosmos.global.exception.CustomException;
import trying.cosmos.global.exception.ExceptionType;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SocialAccountService {

    private final UserRepository userRepository;
    private final SessionService sessionService;
    private final TokenProvider tokenProvider;

    @Transactional
    public String join(String identifier, String email, String name, String deviceToken) {
        if (userRepository.existsByIdentifier(identifier)) {
            throw new CustomException(ExceptionType.IDENTIFIER_DUPLICATED);
        }
        if (email != null && userRepository.existsByEmail(email)) {
            throw new CustomException(ExceptionType.EMAIL_DUPLICATED);
        }
        if (userRepository.existsByName(name)) {
            throw new CustomException(ExceptionType.NAME_DUPLICATED);
        }

        User user = userRepository.save(User.createSocialUser(identifier, email, name, deviceToken));
        Session auth = sessionService.create(user);
        return tokenProvider.getAccessToken(auth);
    }

    @Transactional
    public String login(String identifier, String deviceToken) {
        User user = userRepository.findByIdentifier(identifier).orElseThrow();
        user.login(deviceToken);
        Session auth = sessionService.create(user);
        return tokenProvider.getAccessToken(auth);
    }
}
