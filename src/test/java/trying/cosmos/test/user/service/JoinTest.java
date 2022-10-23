package trying.cosmos.test.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import trying.cosmos.domain.certification.entity.Certification;
import trying.cosmos.domain.certification.repository.CertificationRepository;
import trying.cosmos.domain.certification.service.CertificationService;
import trying.cosmos.domain.user.entity.User;
import trying.cosmos.domain.user.repository.UserRepository;
import trying.cosmos.domain.user.service.UserService;
import trying.cosmos.global.exception.CustomException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static trying.cosmos.domain.user.entity.UserStatus.LOGOUT;
import static trying.cosmos.global.exception.ExceptionType.CERTIFICATION_FAILED;
import static trying.cosmos.test.component.TestVariables.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@DisplayName("(User.Service) 회원 가입")
public class JoinTest {

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CertificationService certificationService;

    @Autowired
    CertificationRepository certificationRepository;

    @BeforeEach
    void setup() {
        certificationService.createCertificationCode(EMAIL);
        Certification certification = certificationRepository.findByEmail(EMAIL).orElseThrow();
        certificationService.certificate(EMAIL, certification.getCode());
    }

    @Nested
    @DisplayName("성공")
    class success {

        @Test
        @DisplayName("회원 가입하면 사용자의 상태는 LOGOUT으로 설정")
        void join() throws Exception {
            userService.join(EMAIL, PASSWORD, USER_NAME);
            assertThat(userRepository.findByEmail(EMAIL)).isPresent();
            assertThat(userRepository.findByEmail(EMAIL).orElseThrow().getStatus()).isEqualTo(LOGOUT);
        }
    }

    @Nested
    @DisplayName("실패")
    class fail {

        @Test
        @DisplayName("인증 코드가 만들어지지 않은 경우")
        void no_email_certification() throws Exception {
            assertThatThrownBy(() -> userService.join("new@gmail.com", PASSWORD, USER_NAME))
                    .isInstanceOf(CustomException.class)
                    .hasMessage(CERTIFICATION_FAILED.getMessage());
        }

        @Test
        @DisplayName("이메일이 아직 인증되지 않은 경우")
        void email_not_certified() throws Exception {
            certificationService.createCertificationCode("before@gmail.com");
            assertThatThrownBy(() -> userService.join("before@gmail.com", PASSWORD, USER_NAME))
                    .isInstanceOf(CustomException.class)
                    .hasMessage(CERTIFICATION_FAILED.getMessage());
        }

        @Test
        @DisplayName("중복된 이메일이 존재하는 경우")
        void duplicate_email() throws Exception {
            userRepository.save(new User(EMAIL, PASSWORD, "user"));
            assertThatThrownBy(() -> userService.join(EMAIL, PASSWORD, USER_NAME))
                    .isInstanceOf(DataIntegrityViolationException.class);
        }

        @Test
        @DisplayName("중복된 닉네임이 존재하는 경우")
        void duplicate_name() throws Exception {
            userRepository.save(new User("different@gmail.com", PASSWORD, USER_NAME));
            assertThatThrownBy(() -> userService.join(EMAIL, PASSWORD, USER_NAME))
                    .isInstanceOf(DataIntegrityViolationException.class);
        }
    }
}
