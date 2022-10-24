package trying.cosmos.test.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import trying.cosmos.domain.user.dto.request.UserLoginRequest;
import trying.cosmos.domain.user.entity.User;
import trying.cosmos.domain.user.repository.UserRepository;
import trying.cosmos.domain.user.service.UserService;
import trying.cosmos.global.auth.repository.SessionRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static trying.cosmos.test.component.TestVariables.*;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Session 테스트")
public class SessionTest {

    @Autowired
    UserRepository userRepository;
    @Autowired
    SessionRepository sessionRepository;
    @Autowired
    UserService userService;
    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper objectMapper;

    @AfterEach
    void clear() {
        sessionRepository.deleteAll();
    }

    @Test
    @DisplayName("로그인시 세션 생성")
    void login_create() throws Exception {
        User user = userRepository.save(new User(EMAIL, PASSWORD, USER_NAME));
        String content = objectMapper.writeValueAsString(new UserLoginRequest(EMAIL, PASSWORD, DEVICE_TOKEN));

        ResultActions actions = mvc.perform(post("/users/login")
                        .content(content)
                        .contentType("application/json"));

        actions.andExpect(status().isOk());
        assertThat(sessionRepository.findByUserId(user.getId())).isPresent();
    }

    @Test
    @DisplayName("로그아웃시 세션 삭제")
    void logout_delete() throws Exception {
        User user = userRepository.save(new User(EMAIL, PASSWORD, USER_NAME));
        String token = userService.login(EMAIL, PASSWORD, DEVICE_TOKEN);

        ResultActions actions = mvc.perform(delete("/users/logout")
                        .header("accessToken", token)
                        .contentType("application/json"));

        actions.andExpect(status().isOk());
        assertThat(sessionRepository.findByUserId(user.getId())).isEmpty();
    }

    @Test
    @DisplayName("회원 탈퇴시 세션 삭제")
    void withdraw_delete() throws Exception {
        User user = userRepository.save(new User(EMAIL, PASSWORD, USER_NAME));
        String token = userService.login(EMAIL, PASSWORD, DEVICE_TOKEN);

        ResultActions actions = mvc.perform(delete("/users")
                        .header("accessToken", token)
                        .contentType("application/json"));

        actions.andExpect(status().isOk());
        assertThat(sessionRepository.findByUserId(user.getId())).isEmpty();
    }
}
