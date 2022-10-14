package trying.cosmos.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import trying.cosmos.auth.TokenProvider;
import trying.cosmos.entity.User;
import trying.cosmos.entity.component.Certification;
import trying.cosmos.exception.CustomException;
import trying.cosmos.exception.ExceptionType;
import trying.cosmos.repository.CertificationRepository;
import trying.cosmos.repository.PlanetRepository;
import trying.cosmos.repository.UserRepository;
import trying.cosmos.utils.cipher.BCryptUtils;
import trying.cosmos.utils.email.EmailType;
import trying.cosmos.utils.email.EmailUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final CertificationRepository certificationRepository;
    private final PlanetRepository planetRepository;

    private final EmailUtils emailUtils;
    private final TokenProvider tokenProvider;

    public void validateEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new CustomException(ExceptionType.DUPLICATED);
        }
    }

    @Transactional
    public User join(String email, String password) {
        User user = new User(email, password);
        userRepository.save(user);

        String code = createRandomStringNumber(Certification.getLength());
        certificationRepository.save(new Certification(user, code));

        sendCertificationEmail(email, code);
        return user;
    }

    private void sendCertificationEmail(String email, String code) {
        Map<String, String> model = new HashMap<>();
        model.put("code", code);
        model.put("body1", "계정 보안을 위해 이메일 주소를 인증해주세요.");
        model.put("body2", "다음 인증코드를 10분 이내에 입력해주세요.");
        emailUtils.send(email, "이메일 인증을 완료해주세요", "email-template", EmailType.CERTIFICATION, model);
    }

    @Transactional
    public void certificate(String email, String code) {
        Certification certification = certificationRepository.findByUserEmail(email).orElseThrow();
        certification.certificate(code);
        certificationRepository.delete(certification);
    }

    @Async
    @Scheduled(fixedDelay = 1000 * 60 * 60) // 1시간
    @Transactional
    public void clearCertification() {
        certificationRepository.clearCertifications(LocalDateTime.now());
    }

    @Transactional
    public void create(String email, String name) {
        User user = userRepository.findByEmail(email).orElseThrow();
        user.create(name);
    }

    @Transactional
    public String login(String email, String password, String deviceToken) {
        User user = userRepository.findByEmail(email).orElseThrow();
        checkPassword(password, user);
        user.login(deviceToken);
        return tokenProvider.getAccessToken(user);
    }

    @Transactional
    public void resetPassword(String email) {
        User user = userRepository.findByEmail(email).orElseThrow();
        String password = createRandomStringNumber(10);
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

    public User find(String name) {
        return userRepository.findByName(name).orElseThrow();
    }

    public User find(Long id) {
        return userRepository.findById(id).orElseThrow();
    }

    @Transactional
    public void update(Long id, String name, String password) {
        User user = userRepository.findById(id).orElseThrow();
        user.update(name, password);
    }

    @Transactional
    public void logout(Long id) {
        User user = userRepository.findById(id).orElseThrow();
        user.logout();
    }

    @Transactional
    public void withdraw(Long id) {
        User user = userRepository.findById(id).orElseThrow();
        user.withdraw();
    }

    private String createRandomStringNumber(int length) {
        return RandomStringUtils.random(length, true, true);
    }

    private static void checkPassword(String password, User user) {
        if (!BCryptUtils.isMatch(password, user.getPassword())) {
            throw new CustomException(ExceptionType.INVALID_PASSWORD);
        }
    }
}
