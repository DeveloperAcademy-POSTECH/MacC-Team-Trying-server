package trying.cosmos.test.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import trying.cosmos.domain.certification.entity.Certification;
import trying.cosmos.domain.certification.repository.CertificationRepository;
import trying.cosmos.domain.certification.service.CertificationService;
import trying.cosmos.domain.user.entity.User;
import trying.cosmos.domain.user.repository.UserRepository;
import trying.cosmos.domain.user.service.UserService;
import trying.cosmos.global.auth.SessionService;
import trying.cosmos.global.auth.repository.SessionRepository;
import trying.cosmos.global.exception.CustomException;
import trying.cosmos.global.exception.ExceptionType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static trying.cosmos.test.TestVariables.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@DisplayName("이메일을 이용한 회원가입")
public class JoinByEmailTest {

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CertificationService certificationService;

    @Autowired
    CertificationRepository certificationRepository;

    @Autowired
    SessionService sessionService;

    @Autowired
    SessionRepository sessionRepository;

    @BeforeEach
    void setup() {
        sessionService.clear();
    }

    @Nested
    @DisplayName("실패")
    class fail {

        @Test
        @DisplayName("인증 객체가 존재하지 않으면 NOT_CERTIFICATED 오류를 발생시킨다.")
        void no_certification() throws Exception {
            // WHEN THEN
            assertThatThrownBy(() -> userService.join(EMAIL1, PASSWORD, NAME1, DEVICE_TOKEN, true))
                    .isInstanceOf(CustomException.class)
                    .hasMessage(ExceptionType.NOT_CERTIFICATED.getMessage());
        }

        @Test
        @DisplayName("인증 객체가 인증되지 않았다면 NOT_CERTIFICATED 오류를 발생시킨다.")
        void not_certificated() throws Exception {
            // GIVEN
            certificationService.generate(EMAIL1);

            // WHEN THEN
            assertThatThrownBy(() -> userService.join(EMAIL1, PASSWORD, NAME1, DEVICE_TOKEN, true))
                    .isInstanceOf(CustomException.class)
                    .hasMessage(ExceptionType.NOT_CERTIFICATED.getMessage());
        }

        @Test
        @DisplayName("이메일에 해당하는 사용자가 존재한다면 EMAIL_DUPLICATED 오류를 발생시킨다.")
        void email_duplicated() throws Exception {
            // GIVEN
            userRepository.save(User.createEmailUser(EMAIL1, PASSWORD, NAME2, DEVICE_TOKEN, true));
            Certification certification = certificationRepository.save(new Certification(EMAIL1));
            certificationService.certificate(EMAIL1, certification.getCode());

            // WHEN THEN
            assertThatThrownBy(() -> userService.join(EMAIL1, PASSWORD, NAME1, DEVICE_TOKEN, true))
                    .isInstanceOf(CustomException.class)
                    .hasMessage(ExceptionType.EMAIL_DUPLICATED.getMessage());
        }

        @Test
        @DisplayName("닉네임에 해당하는 사용자가 존재한다면 NAME_DUPLICATED 오류를 발생시킨다.")
        void name_duplicated() throws Exception {
            userRepository.save(User.createEmailUser(EMAIL2, PASSWORD, NAME1, DEVICE_TOKEN, true));
            String code = certificationRepository.save(new Certification(EMAIL1)).getCode();
            certificationService.certificate(EMAIL1, code);

            // WHEN THEN
            assertThatThrownBy(() -> userService.join(EMAIL1, PASSWORD, NAME1, DEVICE_TOKEN, true))
                    .isInstanceOf(CustomException.class)
                    .hasMessage(ExceptionType.NAME_DUPLICATED.getMessage());
        }
    }

    @Nested
    @DisplayName("성공")
    class success {

        /**
         * <ol>
         *     <li>인증 객체 삭제</li>
         *     <li>사용자 생성</li>
         *     <li>세션 생성</li>
         * </ol>
         */
        @Test
        @DisplayName("회원 가입")
        void join() throws Exception {
            // GIVEN
            Certification certification = certificationRepository.save(new Certification(EMAIL1));
            certificationService.certificate(EMAIL1, certification.getCode());

            // WHEN
            userService.join(EMAIL1, PASSWORD, NAME1, DEVICE_TOKEN, true);

            // THEN
            assertThat(certificationRepository.findByEmail(EMAIL1)).isEmpty();
            assertThat(userRepository.findByEmail(EMAIL1)).isPresent();
            User user = userRepository.findByEmail(EMAIL1).orElseThrow();
            assertThat(sessionRepository.findByUserId(user.getId())).isPresent();
        }
    }
}
