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

import static org.assertj.core.api.Assertions.assertThat;
import static trying.cosmos.test.TestVariables.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@DisplayName("회원탈퇴")
public class WithdrawTest {

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @Nested
    @DisplayName("성공")
    class success {

        @Test
        @DisplayName("사용자 계정을 삭제한다.")
        void withdraw() throws Exception {
            // GIVEN
            User user = userRepository.save(User.createEmailUser(EMAIL1, PASSWORD, NAME1, DEVICE_TOKEN, true));

            // WHEN
            userService.withdraw(user.getId());

            // THEN
            assertThat(user.getStatus()).isEqualTo(UserStatus.WITHDRAWN);
        }
    }
}
