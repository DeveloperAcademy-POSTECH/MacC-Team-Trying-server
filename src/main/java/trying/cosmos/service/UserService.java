package trying.cosmos.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import trying.cosmos.auth.TokenProvider;
import trying.cosmos.entity.component.Certification;
import trying.cosmos.entity.User;
import trying.cosmos.exception.CustomException;
import trying.cosmos.exception.ExceptionType;
import trying.cosmos.repository.CertificationRepository;
import trying.cosmos.repository.UserRepository;
import trying.cosmos.service.request.*;
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

    private final EmailUtils emailUtils;
    private final TokenProvider tokenProvider;

    public void validateEmail(UserValidateEmailRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(ExceptionType.DUPLICATED);
        }
    }

    @Transactional
    public User join(UserJoinRequest request) {
        User user = new User(request.getEmail(), request.getPassword());
        userRepository.save(user);

        String code = createRandomStringNumber(Certification.getLength());
        certificationRepository.save(new Certification(user, code));

        sendCertificationEmail(request.getEmail(), code);
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
    public void certificate(UserCertificationRequest request) {
        Certification certification = certificationRepository.findByUserEmail(request.getEmail()).orElseThrow();
        certification.certificate(request.getCode());
        certificationRepository.delete(certification);
    }

    @Async
    @Scheduled(fixedDelay = 1000 * 60 * 60) // 1시간
    @Transactional
    public void clearCertification() {
        certificationRepository.clearCertifications(LocalDateTime.now());
    }

    @Transactional
    public void create(UserCreateServiceRequest request) {
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        user.create(request.getName());
    }

    @Transactional
    public String login(UserLoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        checkPassword(request.getPassword(), user);
        user.login(request.getDeviceToken());
        return tokenProvider.getAccessToken(user);
    }

    @Transactional
    public void resetPassword(UserResetPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        String password = createRandomStringNumber(10);
        user.resetPassword(BCryptUtils.encrypt(password));
        sendResetPasswordEmail(request.getEmail(), password);
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
    public void update(UserUpdateServiceRequest request) {
        User user = userRepository.findById(request.getUserId()).orElseThrow();
        user.update(request.getName(), request.getPassword());
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
