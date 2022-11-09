package trying.cosmos.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import trying.cosmos.domain.certification.entity.Certification;
import trying.cosmos.domain.certification.repository.CertificationRepository;
import trying.cosmos.domain.course.dto.response.CourseFindContent;
import trying.cosmos.domain.course.entity.Course;
import trying.cosmos.domain.course.repository.CourseLikeRepository;
import trying.cosmos.domain.course.repository.CourseRepository;
import trying.cosmos.domain.user.dto.response.UserActivityResponse;
import trying.cosmos.domain.user.entity.User;
import trying.cosmos.domain.user.repository.UserRepository;
import trying.cosmos.global.auth.SessionService;
import trying.cosmos.global.auth.TokenProvider;
import trying.cosmos.global.auth.entity.Session;
import trying.cosmos.global.exception.CustomException;
import trying.cosmos.global.exception.ExceptionType;
import trying.cosmos.global.utils.cipher.BCryptUtils;
import trying.cosmos.global.utils.email.EmailType;
import trying.cosmos.global.utils.email.EmailUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.RandomStringUtils.random;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final SessionService sessionService;
    private final CertificationRepository certificationRepository;
    private final CourseRepository courseRepository;
    private final CourseLikeRepository courseLikeRepository;

    private final EmailUtils emailUtils;
    private final TokenProvider tokenProvider;

    public boolean isExist(String email) {
        return userRepository.existsByEmail(email);
    }

    @Transactional
    public String join(String email, String password, String name, String deviceToken) {
        if (userRepository.existsByEmail(email)) {
            throw new CustomException(ExceptionType.EMAIL_DUPLICATED);
        }
        if (userRepository.existsByName(name)) {
            throw new CustomException(ExceptionType.NAME_DUPLICATED);
        }
        Certification certification = certificationRepository.findByEmail(email).orElseThrow(() -> new CustomException(ExceptionType.CERTIFICATION_FAILED));
        if (!certification.isCertified()) {
            throw new CustomException(ExceptionType.CERTIFICATION_FAILED);
        }

        certificationRepository.delete(certification);
        User user = userRepository.save(User.createEmailUser(email, password, name, deviceToken));
        user.checkPassword(password);
        Session auth = sessionService.create(user);
        return tokenProvider.getAccessToken(auth);
    }

    @Transactional
    public String login(String email, String password, String deviceToken) {
        User user = userRepository.findByEmail(email).orElseThrow();
        user.checkPassword(password);
        user.login(deviceToken);
        Session auth = sessionService.create(user);
        return tokenProvider.getAccessToken(auth);
    }

    public User find(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        user.checkAccessibleUser();
        return user;
    }

    public UserActivityResponse findActivity(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        if (user.getPlanet() == null) {
            throw new CustomException(ExceptionType.NO_PLANET);
        }
        if (user.getMate() == null) {
            throw new CustomException(ExceptionType.NO_MATE);
        }
        return new UserActivityResponse(
                courseRepository.countByPlanet(user.getPlanet()),
                courseLikeRepository.countByUser(user)
        );
    }

    public Slice<CourseFindContent> findLikedCourses(Long userId, Pageable pageable) {
        Slice<Course> courseSlice = courseLikeRepository.searchCourseByUserId(userId, pageable);
        User user = userRepository.findById(userId).orElseThrow();
        List<CourseFindContent> contents = courseSlice.getContent().stream()
                .map(course -> new CourseFindContent(course, true))
                .collect(Collectors.toList());
        return new SliceImpl<>(contents, courseSlice.getPageable(), courseSlice.hasNext());
    }

    @Transactional
    public void resetPassword(String email) {
        User user = userRepository.findByEmail(email).orElseThrow();
        if (user.isSocialAccount()) {
            throw new CustomException(ExceptionType.SOCIAL_ACCOUNT);
        }

        String password = random(6, true, true);
        user.resetPassword(BCryptUtils.encrypt(password));
        sendResetPasswordEmail(email, password);
    }

    private void sendResetPasswordEmail(String email, String password) {
        Map<String, String> model = new HashMap<>();
        model.put("code", password);
        model.put("body1", "임시 비밀번호가 발급되었습니다.");
        model.put("body2", "보안을 위해 로그인 후 비밀번호를 변경해주세요.");
        emailUtils.send(email, "임시 비밀번호가 발급되었습니다.", "email-template", EmailType.RESET_PASSWORD, model);
    }

    @Transactional
    public void updateName(Long userId, String name) {
        User user = userRepository.findById(userId).orElseThrow();
        user.setName(name);
    }

    @Transactional
    public void updatePassword(Long userId, String password) {
        User user = userRepository.findById(userId).orElseThrow();
        if (user.isSocialAccount()) {
            throw new CustomException(ExceptionType.SOCIAL_ACCOUNT);
        }
        user.setPassword(BCryptUtils.encrypt(password));
    }

    @Transactional
    public void logout(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        sessionService.delete(userId);
        user.logout();
    }

    @Transactional
    public void withdraw(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        sessionService.delete(userId);
        user.withdraw();
    }
}
