package trying.cosmos.test.user.service;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import trying.cosmos.domain.user.entity.User;
import trying.cosmos.domain.user.repository.UserRepository;
import trying.cosmos.domain.user.service.SocialAccountService;
import trying.cosmos.global.auth.repository.SessionRepository;
import trying.cosmos.global.exception.CustomException;
import trying.cosmos.global.exception.ExceptionType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static trying.cosmos.test.TestVariables.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@DisplayName("소셜 회원 가입(애플, 카카오)")
public class JoinBySocialTest {

    @Autowired
    SocialAccountService socialAccountService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    SessionRepository sessionRepository;

    @Nested
    @DisplayName("실패")
    class fail {

        @Test
        @DisplayName("식별자가 존재한다면 IDENTIFIER_DUPLICATED 오류를 발생시킨다.")
        void identifier_duplicated() throws Exception {
            // GIVEN
            socialAccountService.join(IDENTIFIER1, EMAIL1, NAME1, DEVICE_TOKEN);

            // WHEN THEN
            assertThatThrownBy(() -> socialAccountService.join(IDENTIFIER1, EMAIL2, NAME2, DEVICE_TOKEN))
                    .isInstanceOf(CustomException.class)
                    .hasMessage(ExceptionType.IDENTIFIER_DUPLICATED.getMessage());
        }

        @Test
        @DisplayName("이메일이 존재한다면 EMAIL_DUPLICATED 오류를 발생시킨다.")
        void email_duplicated() throws Exception {
            // GIVEN
            socialAccountService.join(IDENTIFIER1, EMAIL1, NAME1, DEVICE_TOKEN);

            // WHEN THEN
            assertThatThrownBy(() -> socialAccountService.join(IDENTIFIER2, EMAIL1, NAME2, DEVICE_TOKEN))
                    .isInstanceOf(CustomException.class)
                    .hasMessage(ExceptionType.EMAIL_DUPLICATED.getMessage());
        }

        @Test
        @DisplayName("닉네임이 존재한다면 NAME_DUPLICATED 오류를 발생시킨다.")
        void name_duplicated() throws Exception {
            // GIVEN
            socialAccountService.join(IDENTIFIER1, EMAIL1, NAME1, DEVICE_TOKEN);

            // WHEN THEN
            assertThatThrownBy(() -> socialAccountService.join(IDENTIFIER2, EMAIL2, NAME1, DEVICE_TOKEN))
                    .isInstanceOf(CustomException.class)
                    .hasMessage(ExceptionType.NAME_DUPLICATED.getMessage());
        }
    }

    @Nested
    @DisplayName("성공")
    class success {

        /**
         * <ol>
         *     <li>사용자 생성</li>
         *     <li>세션 생성</li>
         *     <li>토큰 반환</li>
         * </ol>
         */
        @Test
        @DisplayName("소셜 회원 가입")
        void join() throws Exception {
            // WHEN
            socialAccountService.join(IDENTIFIER1, EMAIL1, NAME1, DEVICE_TOKEN);

            // THEN
            assertThat(userRepository.findByIdentifier(IDENTIFIER1)).isPresent();
            User user = userRepository.findByIdentifier(IDENTIFIER1).orElseThrow();
            assertThat(sessionRepository.findByUserId(user.getId())).isPresent();
        }
    }
}
