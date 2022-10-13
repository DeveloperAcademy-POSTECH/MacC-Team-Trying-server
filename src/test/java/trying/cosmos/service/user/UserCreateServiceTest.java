package trying.cosmos.service.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import trying.cosmos.entity.User;
import trying.cosmos.entity.component.Authority;
import trying.cosmos.entity.component.UserStatus;
import trying.cosmos.exception.CustomException;
import trying.cosmos.exception.ExceptionType;
import trying.cosmos.repository.UserRepository;
import trying.cosmos.service.UserService;
import trying.cosmos.service.request.UserCreateRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class UserNameServiceTest {

    @Autowired UserService userService;
    @Autowired UserRepository userRepository;

    private static final String EMAIL = "email@gmail.com";
    private static final String PASSWORD = "password";

    @Test
    @DisplayName("회원가입 완료하기")
    void complete() throws Exception {
        userRepository.save(new User(EMAIL, PASSWORD, null, UserStatus.INCOMPLETE, Authority.USER));
        userService.create(new UserCreateRequest(EMAIL, "poding"));
        assertThat(userRepository.findByEmail(EMAIL).orElseThrow().getName()).isEqualTo("poding");
    }

    @Test
    @DisplayName("회원 상태가 미완성이 아닌 경우 오류")
    void complete_wrong_status() throws Exception {
        userRepository.save(new User(EMAIL, PASSWORD, null, UserStatus.LOGIN, Authority.USER));
        assertThatThrownBy(() -> userService.create(new UserCreateRequest(EMAIL, "poding")))
                .isInstanceOf(CustomException.class)
                .hasMessage(ExceptionType.USER_CREATION_FAILED.getMessage());
    }
}
