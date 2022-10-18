package trying.cosmos.test.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import trying.cosmos.domain.user.User;
import trying.cosmos.domain.user.UserRepository;
import trying.cosmos.domain.user.UserService;
import trying.cosmos.global.utils.cipher.BCryptUtils;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static trying.cosmos.domain.user.UserStatus.LOGIN;
import static trying.cosmos.domain.user.UserStatus.LOGOUT;
import static trying.cosmos.global.auth.Authority.USER;
import static trying.cosmos.test.component.TestVariables.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@DisplayName("(User.Service) 비밀번호 재설정")
public class ResetPasswordTest {

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    private String email;

    @BeforeEach
    void setup() {
        User user = userRepository.save(new User(EMAIL, PASSWORD, USER_NAME, LOGIN, USER));
        this.email = user.getEmail();
    }

    @Nested
    @DisplayName("성공")
    class success {

        @Test
        @DisplayName("비밀번호 재설정시 사용자 상태가 LOGOUT으로 변경")
        void update_name() throws Exception {
            userService.resetPassword(email);
            User user = userRepository.findByEmail(email).orElseThrow();
            assertThat(BCryptUtils.isMatch(PASSWORD, user.getPassword())).isFalse();
            assertThat(user.getStatus()).isEqualTo(LOGOUT);
        }
    }
}
