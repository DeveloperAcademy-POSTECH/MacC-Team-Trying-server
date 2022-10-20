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
import trying.cosmos.global.exception.CustomException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static trying.cosmos.domain.user.UserStatus.LOGIN;
import static trying.cosmos.domain.user.UserStatus.LOGOUT;
import static trying.cosmos.global.auth.Authority.USER;
import static trying.cosmos.global.exception.ExceptionType.NOT_AUTHENTICATED;
import static trying.cosmos.test.component.TestVariables.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@DisplayName("(User.Service) 로그아웃")
public class LogoutTest {

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    private Long userId;

    @BeforeEach
    void setup() {
        User user = userRepository.save(new User(EMAIL, PASSWORD, USER_NAME, LOGIN, USER));
        userId = user.getId();
    }

    @Nested
    @DisplayName("성공")
    class success {

        @Test
        @DisplayName("로그아웃시 사용자의 상태가 LOGOUT으로 변경")
        void logout() throws Exception {
            userService.logout(userId);
            assertThat(userRepository.findById(userId).orElseThrow().getStatus()).isEqualTo(LOGOUT);
        }
    }

    @Nested
    @DisplayName("실패")
    class fail {

        @Test
        @DisplayName("사용자의 상태가 LOGOUT인 경우")
        void logout_user() throws Exception {
            userService.logout(userId);
            assertThatThrownBy(() -> userService.logout(userId))
                    .isInstanceOf(CustomException.class)
                    .hasMessage(NOT_AUTHENTICATED.getMessage());
        }
    }
}
