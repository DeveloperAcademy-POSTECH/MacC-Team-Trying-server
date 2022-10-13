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
import trying.cosmos.exception.CustomExceptionAdvice;
import trying.cosmos.exception.ExceptionType;
import trying.cosmos.service.UserService;
import trying.cosmos.service.request.UserJoinRequest;
import trying.cosmos.service.request.UserValidateEmailRequest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static trying.cosmos.exception.ExceptionType.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class UserValidateEmailTest {

    @Autowired MockMvc mvc;
    @Autowired UserController userController;

    @Autowired UserService userService;
    @Autowired ObjectMapper objectMapper;

    private static final String JSON_CONTENT_TYPE = "application/json";

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new CustomExceptionAdvice())
                .addFilter(new CharacterEncodingFilter("UTF-8", true))
                .build();
    }

    @Test
    @DisplayName("이메일 중복 체크 - POST /users/validate-email")
    void validate_email() throws Exception {
        String json = objectMapper.writeValueAsString(new UserValidateEmailRequest("email@gmail.com"));

        ResultActions actions = mvc.perform(post("/users/validate-email")
                .content(json)
                .contentType(JSON_CONTENT_TYPE));

        actions.andExpect(status().isOk());
    }

    @Test
    @DisplayName("이메일 중복 체크 - POST /users/validate-email - 이메일이 형식에 맞지 않는 경우")
    void validate_email_validation() throws Exception {
        String json = objectMapper.writeValueAsString(new UserValidateEmailRequest("email"));

        ResultActions actions = mvc.perform(post("/users/validate-email")
                .content(json)
                .contentType(JSON_CONTENT_TYPE));

        actions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].code").value(INVALID_INPUT.toString()));
    }

    @Test
    @DisplayName("이메일 중복 체크 - POST /users/validate-email - 중복된 이메일이 존재하는 경우")
    void validate_email_duplicated() throws Exception {
        userService.join(new UserJoinRequest("email@gmail.com", "password"));
        String json = objectMapper.writeValueAsString(new UserValidateEmailRequest("email@gmail.com"));

        ResultActions actions = mvc.perform(post("/users/validate-email")
                .content(json)
                .contentType(JSON_CONTENT_TYPE));

        actions.andExpect(status().isConflict())
                .andExpect(jsonPath("$.errors[0].code").value(DUPLICATED.toString()));
    }
}
