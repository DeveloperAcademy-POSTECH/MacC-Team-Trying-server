package trying.cosmos.domain.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import trying.cosmos.domain.certification.Certification;
import trying.cosmos.domain.certification.CertificationRepository;
import trying.cosmos.global.auth.TokenProvider;
import trying.cosmos.global.exception.CustomException;
import trying.cosmos.global.exception.ExceptionType;
import trying.cosmos.global.utils.cipher.BCryptUtils;
import trying.cosmos.global.utils.email.EmailType;
import trying.cosmos.global.utils.email.EmailUtils;

import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.lang3.RandomStringUtils.random;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final CertificationRepository certificationRepository;

    private final EmailUtils emailUtils;
    private final TokenProvider tokenProvider;

    public boolean isExist(String email) {
        return certificationRepository.existsByEmail(email) || userRepository.existsByEmail(email);
    }

    @Transactional
    public User join(String email, String password, String name) {
        Certification certification = certificationRepository.findByEmail(email).orElseThrow(() -> new CustomException(ExceptionType.CERTIFICATION_FAILED));
        if (!certification.isCertified()) {
            throw new CustomException(ExceptionType.CERTIFICATION_FAILED);
        }

        certificationRepository.delete(certification);
        return userRepository.save(new User(email, password, name));
    }

    @Transactional
    public String login(String email, String password, String deviceToken) {
        User user = userRepository.findByEmail(email).orElseThrow();
        checkPassword(password, user);
        checkAccessibleUser(user);
        user.login(deviceToken);
        return tokenProvider.getAccessToken(user);
    }

    public User find(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        checkAccessibleUser(user);
        return user;
    }

    private static void checkPassword(String password, User user) {
        if (!BCryptUtils.isMatch(password, user.getPassword())) {
            throw new CustomException(ExceptionType.INVALID_PASSWORD);
        }
    }

    @Transactional
    public void resetPassword(String email) {
        User user = userRepository.findByEmail(email).orElseThrow();
        String password = random(10, true, true);
        checkAccessibleUser(user);
        user.resetPassword(BCryptUtils.encrypt(password));
        sendResetPasswordEmail(email, password);
    }

    private void sendResetPasswordEmail(String email, String password) {
        Map<String, String> model = new HashMap<>();
        model.put("password", password);
        model.put("body1", "임시 비밀번호가 발급되었습니다.");
        model.put("body2", "보안을 위해 로그인 후 비밀번호를 변경해주세요.");
        emailUtils.send(email, "임시 비밀번호가 발급되었습니다.", "email-template", EmailType.RESET_PASSWORD, model);
    }

    @Transactional
    public void updateName(Long userId, String name) {
        User user = userRepository.findById(userId).orElseThrow();
        checkLoginUser(user);
        user.setName(name);
    }

    @Transactional
    public void updatePassword(Long userId, String password) {
        User user = userRepository.findById(userId).orElseThrow();
        checkLoginUser(user);
        user.setPassword(BCryptUtils.encrypt(password));
    }

    @Transactional
    public void logout(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        checkLoginUser(user);
        user.logout();
    }

    @Transactional
    public void withdraw(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        checkLoginUser(user);
        user.withdraw();
    }

    private void checkAccessibleUser(User user) {
        switch (user.getStatus()) {
            case SUSPENDED:
                throw new CustomException(ExceptionType.SUSPENDED_USER);
            case WITHDRAWN:
                throw new CustomException(ExceptionType.NO_DATA);
        }
    }

    private void checkLoginUser(User user) {
        checkAccessibleUser(user);
        if (!user.getStatus().equals(UserStatus.LOGIN)) {
            throw new CustomException(ExceptionType.NOT_LOGIN);
        }
    }
}
