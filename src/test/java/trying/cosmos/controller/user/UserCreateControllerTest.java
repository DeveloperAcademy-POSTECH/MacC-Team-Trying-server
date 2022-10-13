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
import trying.cosmos.repository.CertificationRepository;
import trying.cosmos.repository.UserRepository;
import trying.cosmos.service.UserService;
import trying.cosmos.service.request.UserNameRequest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class UserNameControllerTest {

    @Autowired MockMvc mvc;
    @Autowired UserController userController;

    @Autowired UserService userService;
    @Autowired UserRepository userRepository;
    @Autowired CertificationRepository certificationRepository;
    @Autowired ObjectMapper objectMapper;

    private static final String URL = "/users/create";
    private static final String JSON_CONTENT_TYPE = "application/json";
    private static final String EMAIL = "email@gmail.com";
    private static final String PASSWORD = "password";

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new CustomExceptionAdvice())
                .addFilter(new CharacterEncodingFilter("UTF-8", true))
                .build();
    }

    @Test
    @DisplayName("닉네임 설정하기 - POST /users/name")
    void init_name() throws Exception {
        userRepository.save(new User(EMAIL, PASSWORD, null, UserStatus.INCOMPLETE, Authority.USER));
        String json = objectMapper.writeValueAsString(new UserNameRequest(EMAIL, "poding"));

        ResultActions actions = mvc.perform(post(URL)
                .content(json)
                .contentType(JSON_CONTENT_TYPE));

        actions.andExpect(status().isOk());
    }

    @Test
    @DisplayName("닉네임 설정하기 - POST /users/name - 닉네임이 형식에 맞지 않는 경우")
    void init_name_validation() throws Exception {
        userRepository.save(new User(EMAIL, PASSWORD, null, UserStatus.INCOMPLETE, Authority.USER));
        String json = objectMapper.writeValueAsString(new UserNameRequest(EMAIL, "poding()"));

        ResultActions actions = mvc.perform(post(URL)
                .content(json)
                .contentType(JSON_CONTENT_TYPE));

        actions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(ExceptionType.INVALID_INPUT.toString()));
    }

    @Test
    @DisplayName("닉네임 설정하기 - POST /users/name - 사용자가 존재하지 않는 경우")
    void init_name_no_user() throws Exception {
        String json = objectMapper.writeValueAsString(new UserNameRequest("email@gmail.com", "poding"));

        ResultActions actions = mvc.perform(post(URL)
                .content(json)
                .contentType(JSON_CONTENT_TYPE));

        actions.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(ExceptionType.NO_DATA.toString()));
    }
}
