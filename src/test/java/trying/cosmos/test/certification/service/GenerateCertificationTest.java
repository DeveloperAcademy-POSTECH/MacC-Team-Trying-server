package trying.cosmos.test.certification.service;

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
import trying.cosmos.global.exception.CustomException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static trying.cosmos.global.exception.ExceptionType.EMAIL_DUPLICATED;
import static trying.cosmos.test.TestVariables.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@DisplayName("인증코드 생성")
public class GenerateCertificationTest {

    @Autowired
    CertificationService certificationService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CertificationRepository certificationRepository;

    @Nested
    @DisplayName("실패")
    class fail {

        @Test
        @DisplayName("사용자가 존재한다면 EMAIL_DUPLICATED 오류를 발생시킨다.")
        void email_duplicate() throws Exception {
            // GIVEN
            userRepository.save(User.createEmailUser(EMAIL1, PASSWORD, NAME1, DEVICE_TOKEN, true));

            // WHEN THEN
            assertThatThrownBy(() -> certificationService.generate(EMAIL1))
                    .isInstanceOf(CustomException.class)
                    .hasMessage(EMAIL_DUPLICATED.getMessage());
        }
    }

    @Nested
    @DisplayName("성공")
    class success {

        @Test
        @DisplayName("기존 객체가 존재하지 않는다면 새로운 인증 객체만 생성한다.")
        void not_existing_certification() throws Exception {
            // WHEN
            certificationService.generate(EMAIL1);

            // THEN
            assertThat(certificationRepository.existsByEmail(EMAIL1))
                    .isTrue();
        }

        @Test
        @DisplayName("기존 객체가 존재하면 기존 인증 객체를 삭제하고 새로운 인증 객체를 생성한다.")
        void exist_certification() throws Exception {
            // GIVEN
            certificationService.generate(EMAIL1);
            Certification certification = certificationRepository.findByEmail(EMAIL1).orElseThrow();

            // WHEN
            certificationService.generate(EMAIL1);

            // THEN
            assertThat(certificationRepository.existsByEmail(EMAIL1))
                    .isTrue();
            assertThat(certificationRepository.findByEmail(EMAIL1).orElseThrow())
                    .isNotEqualTo(certification);
        }
    }
}
