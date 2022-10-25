package trying.cosmos.test.certification.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import trying.cosmos.domain.certification.repository.CertificationRepository;
import trying.cosmos.domain.certification.service.CertificationService;
import trying.cosmos.domain.user.entity.User;
import trying.cosmos.domain.user.repository.UserRepository;
import trying.cosmos.global.exception.CustomException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static trying.cosmos.global.exception.ExceptionType.DUPLICATED;
import static trying.cosmos.global.exception.ExceptionType.EMAIL_DUPLICATED;
import static trying.cosmos.test.component.TestVariables.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@DisplayName("(Certification.Service) 인증코드 생성")
public class CreateTest {

    @Autowired
    CertificationService certificationService;

    @Autowired
    CertificationRepository certificationRepository;

    @Autowired
    UserRepository userRepository;

    @Nested
    @DisplayName("성공")
    class success {

        @Test
        @DisplayName("인증코드 생성")
        void create() throws Exception {
            certificationService.createCertificationCode(EMAIL);
            assertThat(certificationRepository.findByEmail(EMAIL)).isPresent();
        }
    }

    @Nested
    @DisplayName("실패")
    class fail {

        @Test
        @DisplayName("중복된 이메일의 인증 코드가 존재하는 경우")
        void duplicate_email_certification() throws Exception {
            certificationService.createCertificationCode(EMAIL);
            assertThatThrownBy(() -> certificationService.createCertificationCode(EMAIL))
                    .isInstanceOf(DataIntegrityViolationException.class);
        }

        @Test
        @DisplayName("중복된 이메일의 사용자가 존재하는 경우")
        void duplicate_email_user() throws Exception {
            userRepository.save(new User(EMAIL, PASSWORD, USER_NAME));
            assertThatThrownBy(() -> certificationService.createCertificationCode(EMAIL))
                    .isInstanceOf(CustomException.class)
                    .hasMessage(EMAIL_DUPLICATED.getMessage());
        }
    }
}
