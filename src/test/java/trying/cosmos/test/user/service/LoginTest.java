package trying.cosmos.test.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import trying.cosmos.domain.user.entity.User;
import trying.cosmos.domain.user.repository.UserRepository;
import trying.cosmos.domain.user.service.UserService;
import trying.cosmos.global.exception.CustomException;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static trying.cosmos.domain.user.entity.UserStatus.*;
import static trying.cosmos.global.auth.Authority.USER;
import static trying.cosmos.global.exception.ExceptionType.*;
import static trying.cosmos.test.component.TestVariables.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@DisplayName("(User.Service) 로그인")
public class LoginTest {

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void setup() {
        userRepository.save(new User(EMAIL, PASSWORD, USER_NAME));
    }

    @Nested
    @DisplayName("성공")
    class success {

        @Test
        @DisplayName("로그인에 성공하는 경우 사용자의 상태가 LOGIN으로 변경")
        void login() throws Exception {
            userService.login(EMAIL, PASSWORD, DEVICE_TOKEN);
            assertThat(userRepository.findByEmail(EMAIL)).isPresent();
            assertThat(userRepository.findByEmail(EMAIL).orElseThrow().getStatus()).isEqualTo(LOGIN);
        }
    }

    @Nested
    @DisplayName("실패")
    class fail {

        @Test
        @DisplayName("이메일이 일치하는 회원이 존재하지 않는 경우")
        void no_email() throws Exception {
            assertThatThrownBy(() -> userService.login("wrong@gmail.com", PASSWORD, DEVICE_TOKEN))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @Test
        @DisplayName("비밀번호가 일치하지 않는 경우")
        void wrong_password() throws Exception {
            assertThatThrownBy(() -> userService.login(EMAIL, "wrong", DEVICE_TOKEN))
                    .isInstanceOf(CustomException.class)
                    .hasMessage(INVALID_PASSWORD.getMessage());
        }

        @Test
        @DisplayName("사용자의 상태가 SUSPENDED인 경우")
        void suspended_user() throws Exception {
            userRepository.save(new User("suspended@gmail.com", PASSWORD, DEVICE_TOKEN, SUSPENDED, USER));
            assertThatThrownBy(() -> userService.login("suspended@gmail.com", PASSWORD, DEVICE_TOKEN))
                    .isInstanceOf(CustomException.class)
                    .hasMessage(SUSPENDED_USER.getMessage());
        }

        @Test
        @DisplayName("사용자의 상태가 WITHDRAWN인 경우")
        void withdrawn_user() throws Exception {
            userRepository.save(new User("withdrawn@gmail.com", PASSWORD, DEVICE_TOKEN, WITHDRAWN, USER));
            assertThatThrownBy(() -> userService.login("withdrawn@gmail.com", PASSWORD, DEVICE_TOKEN))
                    .isInstanceOf(CustomException.class)
                    .hasMessage(NO_DATA.getMessage());
        }
    }
}
