package trying.cosmos.controller.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.filter.CharacterEncodingFilter;
import trying.cosmos.controller.UserController;
import trying.cosmos.entity.User;
import trying.cosmos.entity.component.Authority;
import trying.cosmos.entity.component.UserStatus;
import trying.cosmos.exception.CustomExceptionAdvice;
import trying.cosmos.exception.ExceptionType;
import trying.cosmos.repository.UserRepository;
import trying.cosmos.service.UserService;
import trying.cosmos.service.request.UserLoginRequest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@DisplayName("UserController - 이용 불가 사용자 예외")
@AutoConfigureMockMvc
public class UserAvailableTest {

    @Autowired MockMvc mvc;
    @Autowired UserController userController;

    @Autowired UserService userService;
    @Autowired UserRepository userRepository;
    @Autowired ObjectMapper objectMapper;

    private static final String URL = "/users/login";
    private static final String JSON_CONTENT_TYPE = "application/json";
    private static final String EMAIL = "email@gmail.com";
    private static final String PASSWORD = "password";
    private static final String NAME = "name";
    private static final String DEVICE_TOKEN = "deviceToken";

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new CustomExceptionAdvice())
                .addFilter(new CharacterEncodingFilter("UTF-8", true))
                .build();
    }

    @Test
    @DisplayName("미인증 사용자인 경우")
    void uncertificated() throws Exception {
        User user = userRepository.save(new User(EMAIL, PASSWORD, NAME, UserStatus.UNCERTIFICATED, Authority.USER));
        String json = objectMapper.writeValueAsString(new UserLoginRequest(EMAIL, PASSWORD, DEVICE_TOKEN));

        ResultActions actions = mvc.perform(post(URL)
                .content(json)
                .contentType(JSON_CONTENT_TYPE));

        actions.andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(ExceptionType.NOT_CERTIFICATED.toString()));
    }

    @Test
    @DisplayName("사용자 생성이 완료되지 않은 경우")
    void incomplete() throws Exception {
        User user = userRepository.save(new User(EMAIL, PASSWORD, NAME, UserStatus.INCOMPLETE, Authority.USER));
        String json = objectMapper.writeValueAsString(new UserLoginRequest(EMAIL, PASSWORD, DEVICE_TOKEN));

        ResultActions actions = mvc.perform(post(URL)
                .content(json)
                .contentType(JSON_CONTENT_TYPE));

        actions.andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(ExceptionType.INCOMPLETE_CREATE_USER.toString()));
    }

    @Test
    @DisplayName("정지된 사용자인 경우")
    void suspended() throws Exception {
        User user = userRepository.save(new User(EMAIL, PASSWORD, NAME, UserStatus.SUSPENDED, Authority.USER));
        String json = objectMapper.writeValueAsString(new UserLoginRequest(EMAIL, PASSWORD, DEVICE_TOKEN));

        ResultActions actions = mvc.perform(post(URL)
                .content(json)
                .contentType(JSON_CONTENT_TYPE));

        actions.andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(ExceptionType.SUSPENDED.toString()));
    }

    @Test
    @DisplayName("탈퇴한 사용자인 경우")
    void withdrawn() throws Exception {
        User user = userRepository.save(new User(EMAIL, PASSWORD, NAME, UserStatus.WITHDRAWN, Authority.USER));
        String json = objectMapper.writeValueAsString(new UserLoginRequest(EMAIL, PASSWORD, DEVICE_TOKEN));

        ResultActions actions = mvc.perform(post(URL)
                .content(json)
                .contentType(JSON_CONTENT_TYPE));

        actions.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(ExceptionType.NO_DATA.toString()));
    }
}
