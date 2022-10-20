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
import static trying.cosmos.domain.user.UserStatus.*;
import static trying.cosmos.global.auth.Authority.USER;
import static trying.cosmos.global.exception.ExceptionType.NOT_AUTHENTICATED;
import static trying.cosmos.test.component.TestVariables.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@DisplayName("(User.Service) 회원 탈퇴")
public class WithdrawTest {

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    private Long userId;
    private String email;
    private String name;
    
    @BeforeEach
    void setup() {
        User user = userRepository.save(new User(EMAIL, PASSWORD, USER_NAME, LOGIN, USER));
        userId = user.getId();
        email = user.getEmail();
        name = user.getName();
    }

    @Nested
    @DisplayName("성공")
    class success {

        @Test
        @DisplayName("회원 탈퇴시 사용자의 상태가 WITHDRAWN으로 변경")
        void withdraw() throws Exception {
            userService.withdraw(userId);
            assertThat(userRepository.findById(userId).orElseThrow().getStatus()).isEqualTo(WITHDRAWN);
        }

        @Test
        @DisplayName("회원 탈퇴시 사용자의 이메일에 \"[랜덤한 6자리] \"가 삽입")
        void withdraw_email() throws Exception {
            userService.withdraw(userId);
            User withdrawnUser = userRepository.findById(userId).orElseThrow();
            assertThat(withdrawnUser.getEmail()).isNotEqualTo(email);
            assertThat(withdrawnUser.getOriginEmail()).isEqualTo(email);
        }

        @Test
        @DisplayName("회원 탈퇴시 사용자의 닉네임에 \"[랜덤한 6자리] \"가 삽입")
        void withdraw_name() throws Exception {
            userService.withdraw(userId);
            User withdrawnUser = userRepository.findById(userId).orElseThrow();
            assertThat(withdrawnUser.getName()).isNotEqualTo(name);
            assertThat(withdrawnUser.getOriginName()).isEqualTo(name);
        }
    }

    @Nested
    @DisplayName("실패")
    class fail {

        @Test
        @DisplayName("사용자의 상태가 LOGOUT인 경우")
        void logout_user() throws Exception {
            User user = userRepository.save(new User("logout@gmail.com", PASSWORD, "logout", LOGOUT, USER));
            assertThatThrownBy(() -> userService.withdraw(user.getId()))
                    .isInstanceOf(CustomException.class)
                    .hasMessage(NOT_AUTHENTICATED.getMessage());
        }
    }
}
