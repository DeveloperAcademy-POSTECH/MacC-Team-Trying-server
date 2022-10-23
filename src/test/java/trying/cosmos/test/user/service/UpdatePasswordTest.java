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
import trying.cosmos.global.utils.cipher.BCryptUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static trying.cosmos.domain.user.entity.UserStatus.LOGIN;
import static trying.cosmos.global.auth.Authority.USER;
import static trying.cosmos.test.component.TestVariables.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@DisplayName("(User.Service) 비밀번호 변경")
public class UpdatePasswordTest {

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    private Long userId;

    @BeforeEach
    void setup() {
        User user = userRepository.save(new User(EMAIL, PASSWORD, USER_NAME, LOGIN, USER));
        this.userId = user.getId();
    }

    @Nested
    @DisplayName("성공")
    class success {

        @Test
        @DisplayName("비밀번호 변경")
        void update_name() throws Exception {
            userService.updatePassword(userId, "updated");
            assertThat(BCryptUtils.isMatch("updated", userRepository.findById(userId).orElseThrow().getPassword())).isTrue();
        }
    }
}
