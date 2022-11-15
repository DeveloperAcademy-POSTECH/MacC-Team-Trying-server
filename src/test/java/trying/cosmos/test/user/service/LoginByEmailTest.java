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
import trying.cosmos.domain.user.entity.UserStatus;
import trying.cosmos.domain.user.repository.UserRepository;
import trying.cosmos.domain.user.service.UserService;
import trying.cosmos.global.auth.SessionService;
import trying.cosmos.global.auth.TokenProvider;
import trying.cosmos.global.auth.entity.Session;
import trying.cosmos.global.auth.repository.SessionRepository;
import trying.cosmos.global.exception.CustomException;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static trying.cosmos.global.exception.ExceptionType.*;
import static trying.cosmos.test.TestVariables.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@DisplayName("이메일을 이용한 로그인")
public class LoginByEmailTest {

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    SessionService sessionService;

    @Autowired
    SessionRepository sessionRepository;

    @Autowired
    TokenProvider tokenProvider;

    @BeforeEach
    void setup() {
        sessionService.clear();
    }
    
    @Nested
    @DisplayName("실패")
    class fail {
    
        @Test
        @DisplayName("사용자가 존재하지 않으면 NO_DATA 오류를 발생시킨다.")
        void no_user() throws Exception {
            // WHEN THEN
            assertThatThrownBy(() -> userService.login(EMAIL1, PASSWORD, NAME1))
                    .isInstanceOf(NoSuchElementException.class);
        }
    
        @Test
        @DisplayName("비밀번호가 일치하지 않으면 INVALID_PASSWORD 오류를 발생시킨다.")
        void wrong_password() throws Exception {
            // GIVEN
            User user = userRepository.save(User.createEmailUser(EMAIL1, PASSWORD, NAME1, DEVICE_TOKEN, true));
            
            // WHEN THEN
            assertThatThrownBy(() -> userService.login(EMAIL1, WRONG_PASSWORD, DEVICE_TOKEN))
                    .isInstanceOf(CustomException.class)
                    .hasMessage(INVALID_PASSWORD.getMessage());
        }
    
        @Test
        @DisplayName("정지된 사용자라면 SUSPENDED_USER 오류를 발생시킨다.")
        void suspended_user() throws Exception {
            // GIVEN
            User user = userRepository.save(User.createEmailUser(EMAIL1, PASSWORD, NAME1, DEVICE_TOKEN, true));
            user.setStatus(UserStatus.SUSPENDED);

            // WHEN THEN
            assertThatThrownBy(() -> userService.login(EMAIL1, PASSWORD, DEVICE_TOKEN))
                    .isInstanceOf(CustomException.class)
                    .hasMessage(SUSPENDED_USER.getMessage());
        }
    
        @Test
        @DisplayName("삭제된 사용자라면 NO_DATA 오류를 발생시킨다.")
        void withdrawn_user() throws Exception {
            // GIVEN
            User user = userRepository.save(User.createEmailUser(EMAIL1, PASSWORD, NAME1, DEVICE_TOKEN, true));
            user.setStatus(UserStatus.WITHDRAWN);

            // WHEN THEN
            assertThatThrownBy(() -> userService.login(EMAIL1, PASSWORD, DEVICE_TOKEN))
                    .isInstanceOf(CustomException.class)
                    .hasMessage(NO_DATA.getMessage());
        }
    }
    
    @Nested
    @DisplayName("성공")
    class success {

        /**
         * 기존 세션 없으면,
         * <ol>
         *     <li>새로운 세션 생성</li>
         *     <li>토큰 반환</li>
         * </ol>
         */
        @Test
        @DisplayName("세션이 존재하지 않으면 새로운 세션을 생성한다.")
        void session_not_exist() throws Exception {
            // GIVEN
            User user = userRepository.save(User.createEmailUser(EMAIL1, PASSWORD, NAME1, DEVICE_TOKEN, true));

            // WHEN
            String token = userService.login(EMAIL1, PASSWORD, DEVICE_TOKEN);

            // THEN
            assertThat(sessionRepository.findByUserId(user.getId()))
                    .isPresent();
            assertThat(sessionRepository.findById(tokenProvider.getSubject(token)).orElseThrow().getId())
                    .isEqualTo(sessionRepository.findByUserId(user.getId()).orElseThrow().getId());
        }

        /**
         * 기존 세션이 있으면,
         * <ol>
         *     <li>기존 세션 삭제</li>
         *     <li>새로운 세션 생성</li>
         * </ol>
         */
        @Test
        @DisplayName("세션이 존재하면 기존 세션을 삭제하고 새로운 세션을 생성한다.")
        void session_exist() throws Exception {
            // GIVEN
            User user = userRepository.save(User.createEmailUser(EMAIL1, PASSWORD, NAME1, DEVICE_TOKEN, true));
            userService.login(EMAIL1, PASSWORD, DEVICE_TOKEN);
            Session session = sessionRepository.findByUserId(user.getId()).orElseThrow();

            // WHEN
            String token = userService.login(EMAIL1, PASSWORD, DEVICE_TOKEN);

            // THEN
            assertThat(sessionRepository.findById(tokenProvider.getSubject(token)).orElseThrow().getId())
                    .isNotEqualTo(session.getId());
        }
    }
}
