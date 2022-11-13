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
import trying.cosmos.global.exception.CustomException;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static trying.cosmos.global.exception.ExceptionType.CERTIFICATION_FAILED;
import static trying.cosmos.test.TestVariables.EMAIL1;
import static trying.cosmos.test.TestVariables.WRONG_CERTIFICATION_CODE;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@DisplayName("인증코드 확인")
public class CertificateTest {

    @Autowired
    CertificationService certificationService;

    @Autowired
    CertificationRepository certificationRepository;

    @Nested
    @DisplayName("실패")
    class fail {

        @Test
        @DisplayName("인증 객체가 존재하지 않으면 NO_DATA 오류를 발생시킨다.")
        void no_certification() throws Exception {
            // WHEN THEN
            assertThatThrownBy(() -> certificationService.certificate(EMAIL1, WRONG_CERTIFICATION_CODE))
                    .isInstanceOf(NoSuchElementException.class);
        }
        
        @Test
        @DisplayName("인증 객체가 만료되었다면 CERTIFICATION_FAILED 오류를 발생시킨다.")
        void expired() throws Exception {
            // GIVEN
            Certification certification = certificationRepository.save(
                    new Certification(EMAIL1, 0)
            );

            // WHEN THEN
            assertThatThrownBy(() -> certificationService.certificate(EMAIL1, certification.getCode()))
                    .isInstanceOf(CustomException.class)
                    .hasMessage(CERTIFICATION_FAILED.getMessage());
        }
        
        @Test
        @DisplayName("인증 코드가 일치하지 않는다면 CERTIFICATION_FAILED 오류를 발생시킨다.")
        void wrong_code() throws Exception {
            // GIVEN
            certificationService.generate(EMAIL1);
            
            // WHEN THEN
            assertThatThrownBy(() -> certificationService.certificate(EMAIL1, WRONG_CERTIFICATION_CODE))
                    .isInstanceOf(CustomException.class)
                    .hasMessage(CERTIFICATION_FAILED.getMessage());
        }
    }

    @Nested
    @DisplayName("성공")
    class success {

        /**
         * 인증이 완료되면 isCertificated 변수를 true로 바꾼다.
         *
         */
        @Test
        @DisplayName("인증")
        void certificate() throws Exception {
            // GIVEN
            certificationService.generate(EMAIL1);
            Certification certification = certificationRepository.findByEmail(EMAIL1).orElseThrow();

            // WHEN
            certificationService.certificate(EMAIL1, certification.getCode());
            
            // THEN
            assertThat(certificationRepository.findByEmail(EMAIL1).orElseThrow().isCertified()).isTrue();
        }
    }
}
