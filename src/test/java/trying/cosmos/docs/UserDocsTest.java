package trying.cosmos.docs;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import trying.cosmos.auth.TokenProvider;
import trying.cosmos.controller.UserController;
import trying.cosmos.controller.request.*;
import trying.cosmos.entity.User;
import trying.cosmos.entity.component.Authority;
import trying.cosmos.entity.component.Certification;
import trying.cosmos.entity.component.UserStatus;
import trying.cosmos.repository.CertificationRepository;
import trying.cosmos.repository.UserRepository;
import trying.cosmos.service.UserService;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static trying.cosmos.docs.utils.ApiDocumentUtils.getDocumentRequest;
import static trying.cosmos.docs.utils.ApiDocumentUtils.getDocumentResponse;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Transactional
@ActiveProfiles("test")

public class UserDocsTest {

    @Autowired MockMvc mvc;
    @Autowired UserController userController;
    @Autowired ObjectMapper objectMapper;
    @Autowired TokenProvider tokenProvider;
    @Autowired UserService userService;
    @Autowired UserRepository userRepository;
    @Autowired CertificationRepository certificationRepository;

    private static final String JSON_CONTENT_TYPE = "application/json";

    private static final String EMAIL = "email@gmail.com";
    private static final String PASSWORD = "password";
    private static final String NAME = "name";
    private static final String DEVICE_TOKEN = "device_token";

    @Test
    @DisplayName("이메일 확인")
    void validate_email() throws Exception {
        String json = objectMapper.writeValueAsString(new UserValidateEmailRequest(EMAIL));

        ResultActions actions = mvc.perform(post("/users/validate-email")
                        .content(json)
                        .contentType(JSON_CONTENT_TYPE))
                .andExpect(status().isOk());

        // docs
        actions.andDo(document("validate-email",
                getDocumentRequest(),
                getDocumentResponse(),
                requestFields(
                    fieldWithPath("email").description("확인하려는 이메일 주소")
                )
        ));
    }

    @Test
    @DisplayName("회원 가입")
    void join() throws Exception {
        String json = objectMapper.writeValueAsString(new UserJoinRequest(EMAIL, PASSWORD));

        ResultActions actions = mvc.perform(post("/users")
                        .content(json)
                        .contentType(JSON_CONTENT_TYPE))
                .andExpect(status().isOk());

        // docs
        actions.andDo(document("join",
                getDocumentRequest(),
                getDocumentResponse(),
                requestFields(
                        fieldWithPath("email").description("사용자 이메일"),
                        fieldWithPath("password").description("사용자 비밀번호")
                )
        ));
    }

    @Test
    @DisplayName("인증코드 확인")
    void certificate() throws Exception {
        User user = userService.join(EMAIL, PASSWORD);
        Certification certification = certificationRepository.findByUserEmail(user.getEmail()).orElseThrow();
        String json = objectMapper.writeValueAsString(new UserCertificationRequest(EMAIL, certification.getCode()));

        ResultActions actions = mvc.perform(post("/users/certificate")
                        .content(json)
                        .contentType(JSON_CONTENT_TYPE))
                .andExpect(status().isOk());

        // docs
        actions.andDo(document("certification",
                getDocumentRequest(),
                getDocumentResponse(),
                requestFields(
                        fieldWithPath("email").description("사용자 이메일"),
                        fieldWithPath("code").description("이메일을 통해 받은 인증코드")
                )
        ));
    }

    @Test
    @DisplayName("유저 생성")
    void create_user() throws Exception {
        User user = userRepository.save(new User(EMAIL, PASSWORD, NAME, UserStatus.INCOMPLETE, Authority.USER));
        String json = objectMapper.writeValueAsString(new UserCreateRequest(EMAIL, NAME));

        ResultActions actions = mvc.perform(post("/users/create")
                        .content(json)
                        .contentType(JSON_CONTENT_TYPE))
                .andExpect(status().isOk());

        // docs
        actions.andDo(document("create-user",
                getDocumentRequest(),
                getDocumentResponse(),
                requestFields(
                        fieldWithPath("email").description("회원가입할 때 사용한 이메일"),
                        fieldWithPath("name").description("사용자 이름")
                )
        ));
    }

    @Test
    @DisplayName("로그인")
    void login() throws Exception {
        User user = userRepository.save(new User(EMAIL, PASSWORD, NAME, UserStatus.LOGOUT, Authority.USER));
        String json = objectMapper.writeValueAsString(new UserLoginRequest(EMAIL, PASSWORD, DEVICE_TOKEN));

        ResultActions actions = mvc.perform(post("/users/login")
                        .content(json)
                        .contentType(JSON_CONTENT_TYPE))
                .andExpect(status().isOk());

        // docs
        actions.andDo(document("login",
                getDocumentRequest(),
                getDocumentResponse(),
                requestFields(
                        fieldWithPath("email").description("사용자 이메일"),
                        fieldWithPath("password").description("사용자 비밀번호"),
                        fieldWithPath("deviceToken").description("푸시 알림을 위한 토큰")
                ),
                responseFields(
                        fieldWithPath("accessToken").description("사용자 인증을 위한 토큰")
                )
        ));
    }

    @Test
    @DisplayName("임시 비밀번호 발급")
    void reset_password() throws Exception {
        User user = userRepository.save(new User(EMAIL, PASSWORD, NAME, UserStatus.LOGOUT, Authority.USER));
        String json = objectMapper.writeValueAsString(new UserResetPasswordRequest(EMAIL));

        ResultActions actions = mvc.perform(patch("/users/reset-password")
                        .content(json)
                        .contentType(JSON_CONTENT_TYPE))
                .andExpect(status().isOk());

        // docs
        actions.andDo(document("reset-password",
                getDocumentRequest(),
                getDocumentResponse(),
                requestFields(
                        fieldWithPath("email").description("사용자 이메일")
                )
        ));
    }

    @Test
    @DisplayName("로그아웃")
    void logout() throws Exception {
        User user = userRepository.save(new User(EMAIL, PASSWORD, NAME, UserStatus.LOGIN, Authority.USER));
        String accessToken = tokenProvider.getAccessToken(user);

        ResultActions actions = mvc.perform(delete("/users/logout")
                        .header("accessToken", accessToken))
                .andExpect(status().isOk());

        // docs
        actions.andDo(document("logout",
                getDocumentRequest(),
                getDocumentResponse(),
                requestHeaders(
                        headerWithName("accessToken").description("사용자 인증 토큰")
                )
        ));
    }

    @Test
    @DisplayName("회원 탈퇴")
    void withdraw() throws Exception {
        User user = userRepository.save(new User(EMAIL, PASSWORD, NAME, UserStatus.LOGIN, Authority.USER));
        String accessToken = tokenProvider.getAccessToken(user);

        ResultActions actions = mvc.perform(delete("/users")
                        .header("accessToken", accessToken))
                .andExpect(status().isOk());

        // docs
        actions.andDo(document("withdraw",
                getDocumentRequest(),
                getDocumentResponse(),
                requestHeaders(
                        headerWithName("accessToken").description("사용자 인증 토큰")
                )
        ));
    }
}
