package trying.cosmos.test.user.service;

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
import trying.cosmos.domain.user.service.SocialAccountService;
import trying.cosmos.global.auth.TokenProvider;
import trying.cosmos.global.auth.entity.Session;
import trying.cosmos.global.auth.repository.SessionRepository;
import trying.cosmos.global.exception.CustomException;
import trying.cosmos.global.exception.ExceptionType;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static trying.cosmos.test.TestVariables.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@DisplayName("소셜 로그인(애플, 카카오)")
public class LoginBySocialTest {

    @Autowired
    SocialAccountService socialAccountService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    SessionRepository sessionRepository;

    @Autowired
    TokenProvider tokenProvider;

    @Nested
    @DisplayName("실패")
    class fail {

        @Test
        @DisplayName("사용자가 존재하지 않으면 NO_DATA 오류를 발생시킨다.")
        void no_user() throws Exception {
            // WHEN THEN
            assertThatThrownBy(() -> socialAccountService.login(IDENTIFIER1, DEVICE_TOKEN))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @Test
        @DisplayName("정지된 사용자라면 SUSPENDED_USER 오류를 발생시킨다.")
        void suspended_user() throws Exception {
            // GIVEN
            User user = userRepository.save(User.createSocialUser(IDENTIFIER1, EMAIL1, PASSWORD, NAME1, true));
            user.setStatus(UserStatus.SUSPENDED);

            // WHEN THEN
            assertThatThrownBy(() -> socialAccountService.login(IDENTIFIER1, DEVICE_TOKEN))
                    .isInstanceOf(CustomException.class)
                    .hasMessage(ExceptionType.SUSPENDED_USER.getMessage());
        }

        @Test
        @DisplayName("삭제된 사용자라면 NO_DATA 오류를 발생시킨다.")
        void withdrawn_user() throws Exception {
            // GIVEN
            User user = userRepository.save(User.createSocialUser(IDENTIFIER1, EMAIL1, PASSWORD, NAME1, true));
            user.setStatus(UserStatus.WITHDRAWN);

            // WHEN THEN
            assertThatThrownBy(() -> socialAccountService.login(IDENTIFIER1, DEVICE_TOKEN))
                    .isInstanceOf(CustomException.class)
                    .hasMessage(ExceptionType.NO_DATA.getMessage());
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
            User user = userRepository.save(User.createSocialUser(IDENTIFIER1, EMAIL1, NAME1, DEVICE_TOKEN, true));

            // WHEN
            String token = socialAccountService.login(IDENTIFIER1, DEVICE_TOKEN);

            // THEN
            assertThat(sessionRepository.findByUserId(user.getId()))
                    .isPresent();
            assertThat(sessionRepository.findById(tokenProvider.getSubject(token)).orElseThrow().getId())
                    .isEqualTo(sessionRepository.findByUserId(user.getId()).orElseThrow().getId());
        }

        /**
         * 기존 세션 없으면,
         * <ol>
         *     <li>기존 세션 삭제</li>
         *     <li>새로운 세션 생성</li>
         *     <li>토큰 반환</li>
         * </ol>
         */
        @Test
        @DisplayName("세션이 존재하면 기존 세션을 삭제하고 새로운 세션을 생성한다.")
        void session_exist() throws Exception {
            // GIVEN
            User user = userRepository.save(User.createSocialUser(IDENTIFIER1, EMAIL1, NAME1, DEVICE_TOKEN, true));
            socialAccountService.login(IDENTIFIER1, DEVICE_TOKEN);
            Session session = sessionRepository.findByUserId(user.getId()).orElseThrow();

            // WHEN
            String token = socialAccountService.login(IDENTIFIER1, DEVICE_TOKEN);

            // THEN
            assertThat(sessionRepository.findById(tokenProvider.getSubject(token)).orElseThrow().getId())
                    .isNotEqualTo(session.getId());
        }
    }
}
