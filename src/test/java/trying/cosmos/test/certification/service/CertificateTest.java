package trying.cosmos.test.certification.service;

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
import trying.cosmos.global.exception.CustomException;
import trying.cosmos.global.exception.ExceptionType;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static trying.cosmos.test.component.TestVariables.EMAIL;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@DisplayName("(Certification.Service) 인증")
public class CertificateTest {

    @Autowired
    CertificationService certificationService;

    @Autowired
    CertificationRepository certificationRepository;

    private String code;

    @BeforeEach
    void setup() {
        certificationService.generate(EMAIL);
        Certification certification = certificationRepository.findByEmail(EMAIL).orElseThrow();
        this.code = certification.getCode();
    }

    @Nested
    @DisplayName("성공")
    class success {

        @Test
        @DisplayName("인증에 성공하면 인증의 isCertificated 변수가 true로 변경")
        void certificate() throws Exception {
            certificationService.certificate(EMAIL, code);
            assertThat(certificationRepository.findByEmail(EMAIL).orElseThrow().isCertified()).isTrue();
        }
    }

    @Nested
    @DisplayName("실패")
    class fail {

        @Test
        @DisplayName("인증 이메일이 존재하지 않는 경우")
        void email_not_exist() throws Exception {
            assertThatThrownBy(() -> certificationService.certificate("wrong@gmail.com", code))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @Test
        @DisplayName("인증 코드가 일치하지 않는 경우")
        void wrong_code() throws Exception {
            assertThatThrownBy(() -> certificationService.certificate(EMAIL, "wrongcode"))
                    .isInstanceOf(CustomException.class)
                    .hasMessage(ExceptionType.CERTIFICATION_FAILED.getMessage());
        }
    }
}
