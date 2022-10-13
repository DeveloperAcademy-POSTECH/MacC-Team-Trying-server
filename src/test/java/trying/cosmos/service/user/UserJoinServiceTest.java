package trying.cosmos.service.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import trying.cosmos.entity.User;
import trying.cosmos.repository.UserRepository;
import trying.cosmos.service.UserService;
import trying.cosmos.service.request.UserJoinRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class UserJoinTest {

    @Autowired UserService userService;
    @Autowired UserRepository userRepository;

    @Test
    @DisplayName("회원가입")
    void join() {
        User user = userService.join(new UserJoinRequest("email", "password"));
        assertThat(userRepository.findByEmail("email").orElseThrow()).isEqualTo(user);
    }

    @Test
    @DisplayName("이메일이 중복된 경우 오류")
    void join_duplicated() {
        userService.join(new UserJoinRequest("email", "password"));
        assertThatThrownBy(() -> userService.join(new UserJoinRequest("email", "password")))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
