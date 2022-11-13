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
import trying.cosmos.domain.user.service.UserService;
import trying.cosmos.global.exception.CustomException;
import trying.cosmos.global.utils.cipher.BCryptUtils;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static trying.cosmos.global.exception.ExceptionType.*;
import static trying.cosmos.test.TestVariables.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@DisplayName("사용자 비밀번호 초기화")
public class ResetPasswordTest {

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;
    
    @Nested
    @DisplayName("실패")
    class fail {
    
        @Test
        @DisplayName("사용자가 존재하지 않는다면 NO_DATA 오류를 발생시킨다.")
        void no_user() throws Exception {
            // WHEN THEN
            assertThatThrownBy(() -> userService.resetPassword("NOT_EXIST"))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @Test
        @DisplayName("정지된 사용자라면 SUSPENDED_USER 오류를 발생시킨다.")
        void suspended_user() throws Exception {
            // GIVEN
            User user = userRepository.save(User.createEmailUser(EMAIL1, PASSWORD, NAME1, DEVICE_TOKEN));
            user.setStatus(UserStatus.SUSPENDED);

            // WHEN THEN
            assertThatThrownBy(() -> userService.resetPassword(EMAIL1))
                    .isInstanceOf(CustomException.class)
                    .hasMessage(SUSPENDED_USER.getMessage());
        }

        @Test
        @DisplayName("삭제된 사용자라면 NO_DATA 오류를 발생시킨다.")
        void withdrawn_user() throws Exception {
            // GIVEN
            User user = userRepository.save(User.createEmailUser(EMAIL1, PASSWORD, NAME1, DEVICE_TOKEN));
            user.setStatus(UserStatus.WITHDRAWN);

            // WHEN THEN
            assertThatThrownBy(() -> userService.resetPassword(EMAIL1))
                    .isInstanceOf(CustomException.class)
                    .hasMessage(NO_DATA.getMessage());
        }

        @Test
        @DisplayName("소셜 회원가입한 사용자라면 SOCIAL_ACCOUNT 오류를 발생시킨다.")
        void social_account() throws Exception {
            // GIVEN
            User user = userRepository.save(User.createSocialUser(IDENTIFIER1, EMAIL1, NAME1, DEVICE_TOKEN));

            // WHEN THEN
            assertThatThrownBy(() -> userService.resetPassword(EMAIL1))
                    .isInstanceOf(CustomException.class)
                    .hasMessage(SOCIAL_ACCOUNT.getMessage());
        }
    }
    
    @Nested
    @DisplayName("성공")
    class success {

        /**
         * <ol>
         *     <li>비밀번호 초기화</li>
         *     <li>로그아웃</li>
         * </ol>
         */
        @Test
        @DisplayName("비밀번호를 초기화한다.")
        void reset_password() throws Exception {
            // GIVEN
            User user = userRepository.save(User.createEmailUser(EMAIL1, PASSWORD, NAME1, DEVICE_TOKEN));

            // WHEN
            userService.resetPassword(EMAIL1);
            
            // THEN
            assertThat(BCryptUtils.isMatch(PASSWORD, user.getPassword()))
                    .isFalse();
            assertThat(user.getStatus())
                    .isEqualTo(UserStatus.LOGOUT);
        }
    }
}
