package trying.cosmos;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import trying.cosmos.entity.User;
import trying.cosmos.entity.component.UserStatus;
import trying.cosmos.repository.UserRepository;
import trying.cosmos.service.UserService;
import trying.cosmos.service.request.UserLoginRequest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static trying.cosmos.entity.component.Authority.ADMIN;
import static trying.cosmos.entity.component.Authority.USER;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AuthenticationTest {

    @Autowired
    UserRepository userRepository;
    @Autowired
    UserService userService;
    @Autowired MockMvc mvc;

    @Test
    @DisplayName("User 권한 사용자는 User 권한 요청이 허용된다.")
    void user_to_user() throws Exception {
        String token = createUser();

        ResultActions actions = mvc.perform(get("/user")
                .header("accessToken", token));

        actions.andExpect(status().isOk());
    }

    @Test
    @DisplayName("User 권한 사용자는 Admin 권한 요청이 금지된다.")
    void user_to_admin() throws Exception {
        String token = createUser();

        ResultActions actions = mvc.perform(get("/admin")
                .header("accessToken", token));

        actions.andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Admin 권한 사용자는 User 권한 요청이 허용된다.")
    void admin_to_user() throws Exception {
        String token = createAdmin();

        ResultActions actions = mvc.perform(get("/user")
                .header("accessToken", token));

        actions.andExpect(status().isOk());
    }

    @Test
    @DisplayName("Admin 권한 사용자는 Admin 권한 요청이 허용된다.")
    void admin_to_admin() throws Exception {
        String token = createAdmin();

        ResultActions actions = mvc.perform(get("/admin")
                .header("accessToken", token));

        actions.andExpect(status().isOk());
    }

    private String createAdmin() {
        userRepository.save(new User("email", "password", "admin", UserStatus.LOGOUT, ADMIN));
        return userService.login(new UserLoginRequest("email", "password", "deviceToken"));
    }

    private String createUser() {
        userRepository.save(new User("email", "password", "admin", UserStatus.LOGOUT, USER));
        return userService.login(new UserLoginRequest("email", "password", "deviceToken"));
    }
}
