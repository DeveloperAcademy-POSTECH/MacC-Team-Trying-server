package trying.cosmos.test.user.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import trying.cosmos.domain.certification.service.CertificationService;
import trying.cosmos.domain.user.entity.User;
import trying.cosmos.domain.user.repository.UserRepository;
import trying.cosmos.domain.user.service.UserService;

import static org.assertj.core.api.Assertions.assertThat;
import static trying.cosmos.test.component.TestVariables.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@DisplayName("(User.Service) 이메일 존재 여부 확인")
public class EmailExistTest {

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CertificationService certificationService;

    @Nested
    @DisplayName("성공")
    class success {

        @Test
        @DisplayName("인증코드로 등록된 이메일이 존재하는 경우 true")
        void email_in_certification() throws Exception {
            certificationService.createCertificationCode(EMAIL);
            assertThat(userService.isExist(EMAIL)).isTrue();
        }

        @Test
        @DisplayName("같은 이메일의 유저가 존재하는 경우 true")
        void email_in_user() throws Exception {
            userRepository.save(new User(EMAIL, PASSWORD, USER_NAME));
            assertThat(userService.isExist(EMAIL)).isTrue();
        }

        @Test
        @DisplayName("등록되지 않은 이메일인 경우 false")
        void no_email() throws Exception {
            assertThat(userService.isExist(EMAIL)).isFalse();
        }
    }
}
