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
import trying.cosmos.controller.request.user.*;
import trying.cosmos.docs.utils.ApiDocumentUtils;
import trying.cosmos.entity.Certification;
import trying.cosmos.entity.Planet;
import trying.cosmos.entity.User;
import trying.cosmos.entity.component.Authority;
import trying.cosmos.entity.component.PlanetImageType;
import trying.cosmos.entity.component.UserStatus;
import trying.cosmos.repository.CertificationRepository;
import trying.cosmos.repository.UserRepository;
import trying.cosmos.service.CertificationService;
import trying.cosmos.service.PlanetService;
import trying.cosmos.service.UserService;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
    @Autowired CertificationService certificationService;
    @Autowired CertificationRepository certificationRepository;

    @Autowired PlanetService planetService;

    private static final String JSON_CONTENT_TYPE = "application/json";

    private static final String EMAIL = "email@gmail.com";
    private static final String PASSWORD = "password";
    private static final String NAME = "name";
    private static final String DEVICE_TOKEN = "device_token";
    private static final String PLANET_NAME = "포딩행성";

    @Test
    @DisplayName("회원가입")
    void join() throws Exception {
        certificationService.createCertificationCode(EMAIL);
        Certification certification = certificationRepository.findByEmail(EMAIL).orElseThrow();
        certificationService.certificate(certification.getEmail(), certification.getCode());

        String content = objectMapper.writeValueAsString(new UserJoinRequest(EMAIL, PASSWORD, NAME));

        ResultActions actions = mvc.perform(post("/users")
                .content(content)
                .contentType(JSON_CONTENT_TYPE))
                .andExpect(status().isOk());

        actions.andDo(document("user/join",
                ApiDocumentUtils.getDocumentRequest(),
                ApiDocumentUtils.getDocumentResponse(),
                requestFields(
                        fieldWithPath("email").description("이메일"),
                        fieldWithPath("password").description("비밀번호"),
                        fieldWithPath("name").description("닉네임")
                )
        ));
    }

    @Test
    @DisplayName("이메일 체크")
    void check_email() throws Exception {
        userRepository.save(new User(EMAIL, PASSWORD, NAME, UserStatus.LOGOUT, Authority.USER));

        ResultActions actions = mvc.perform(get("/users/exist")
                .param("email", EMAIL)
                .contentType(JSON_CONTENT_TYPE))
                .andExpect(status().isOk());

        actions.andDo(document("user/check-email",
                ApiDocumentUtils.getDocumentRequest(),
                ApiDocumentUtils.getDocumentResponse(),
                requestParameters(
                        parameterWithName("email").description("확인하려는 이메일")
                ),
                responseFields(
                        fieldWithPath("exist").description("기존 이메일 존재 여부")
                )
        ));
    }

    @Test
    @DisplayName("로그인")
    void login() throws Exception {
        userRepository.save(new User(EMAIL, PASSWORD, NAME, UserStatus.LOGOUT, Authority.USER));

        String content = objectMapper.writeValueAsString(new UserLoginRequest(EMAIL, PASSWORD, DEVICE_TOKEN));

        ResultActions actions = mvc.perform(post("/users/login")
                .content(content)
                .contentType(JSON_CONTENT_TYPE))
                .andExpect(status().isOk());

        actions.andDo(document("user/login",
                ApiDocumentUtils.getDocumentRequest(),
                ApiDocumentUtils.getDocumentResponse(),
                requestFields(
                        fieldWithPath("email").description("이메일"),
                        fieldWithPath("password").description("비밀번호"),
                        fieldWithPath("deviceToken").description("푸시 알림을 위한 기기 고유 토큰")
                ),
                responseFields(
                        fieldWithPath("accessToken").description("인증 토큰")
                )
        ));
    }

    @Test
    @DisplayName("로그아웃")
    void logout() throws Exception {
        userRepository.save(new User(EMAIL, PASSWORD, NAME, UserStatus.LOGOUT, Authority.USER));
        String accessToken = userService.login(EMAIL, PASSWORD, DEVICE_TOKEN);

        ResultActions actions = mvc.perform(delete("/users/logout")
                        .header("accessToken", accessToken)
                .contentType(JSON_CONTENT_TYPE))
                .andExpect(status().isOk());

        actions.andDo(document("user/logout",
                ApiDocumentUtils.getDocumentRequest(),
                ApiDocumentUtils.getDocumentResponse(),
                requestHeaders(
                        headerWithName("accessToken").description("인증 토큰")
                )
        ));
    }

    @Test
    @DisplayName("회원탈퇴")
    void withdraw() throws Exception {
        userRepository.save(new User(EMAIL, PASSWORD, NAME, UserStatus.LOGOUT, Authority.USER));
        String accessToken = userService.login(EMAIL, PASSWORD, DEVICE_TOKEN);

        ResultActions actions = mvc.perform(delete("/users")
                        .header("accessToken", accessToken)
                .contentType(JSON_CONTENT_TYPE))
                .andExpect(status().isOk());

        actions.andDo(document("user/withdraw",
                ApiDocumentUtils.getDocumentRequest(),
                ApiDocumentUtils.getDocumentResponse(),
                requestHeaders(
                        headerWithName("accessToken").description("인증 토큰")
                )
        ));
    }

    @Test
    @DisplayName("내 정보 조회")
    void findMe() throws Exception {
        User user = userRepository.save(new User(EMAIL, PASSWORD, NAME, UserStatus.LOGOUT, Authority.USER));
        String accessToken = userService.login(EMAIL, PASSWORD, DEVICE_TOKEN);

        Planet planet = planetService.create(user.getId(), PLANET_NAME, PlanetImageType.EARTH);
        User mate = userRepository.save(new User("mate@gmail.com", PASSWORD, "mate", UserStatus.LOGIN, Authority.USER));
        planetService.join(mate.getId(), planet.getInviteCode());

        ResultActions actions = mvc.perform(get("/users/me")
                        .header("accessToken", accessToken)
                .contentType(JSON_CONTENT_TYPE))
                .andExpect(status().isOk());

        actions.andDo(document("user/find-me",
                ApiDocumentUtils.getDocumentRequest(),
                ApiDocumentUtils.getDocumentResponse(),
                requestHeaders(
                        headerWithName("accessToken").description("인증 토큰")
                ),
                responseFields(
                        fieldWithPath("me.name").description("내 닉네임"),
                        fieldWithPath("mate.name").description("메이트 닉네임").optional(),
                        fieldWithPath("planet.name").description("내가 속한 행성 이름").optional(),
                        fieldWithPath("planet.dday").description("행성 디데이").optional(),
                        fieldWithPath("planet.image").description("행성 이미지").optional()
                )
        ));
    }

    @Test
    @DisplayName("닉네임 변경")
    void update_name() throws Exception {
        userRepository.save(new User(EMAIL, PASSWORD, NAME, UserStatus.LOGOUT, Authority.USER));
        String accessToken = userService.login(EMAIL, PASSWORD, DEVICE_TOKEN);

        String content = objectMapper.writeValueAsString(new UserUpdateNameRequest("name1234"));

        ResultActions actions = mvc.perform(put("/users/name")
                .header("accessToken", accessToken)
                .content(content)
                .contentType(JSON_CONTENT_TYPE))
                .andExpect(status().isOk());

        actions.andDo(document("user/update-name",
                ApiDocumentUtils.getDocumentRequest(),
                ApiDocumentUtils.getDocumentResponse(),
                requestHeaders(
                        headerWithName("accessToken").description("인증 토큰")
                ),
                requestFields(
                        fieldWithPath("name").description("변경하려는 이름")
                )
        ));
    }

    @Test
    @DisplayName("비밀번호 변경")
    void update_password() throws Exception {
        userRepository.save(new User(EMAIL, PASSWORD, NAME, UserStatus.LOGOUT, Authority.USER));
        String accessToken = userService.login(EMAIL, PASSWORD, DEVICE_TOKEN);

        String content = objectMapper.writeValueAsString(new UserUpdatePasswordRequest("password1234"));

        ResultActions actions = mvc.perform(put("/users/password")
                .header("accessToken", accessToken)
                .content(content)
                .contentType(JSON_CONTENT_TYPE))
                .andExpect(status().isOk());

        actions.andDo(document("user/update-password",
                ApiDocumentUtils.getDocumentRequest(),
                ApiDocumentUtils.getDocumentResponse(),
                requestHeaders(
                        headerWithName("accessToken").description("인증 토큰")
                ),
                requestFields(
                        fieldWithPath("password").description("변경하려는 비밀번호")
                )
        ));
    }

    @Test
    @DisplayName("비밀번호 재설정")
    void reset_password() throws Exception {
        userRepository.save(new User(EMAIL, PASSWORD, NAME, UserStatus.LOGOUT, Authority.USER));

        String content = objectMapper.writeValueAsString(new UserResetPasswordRequest(EMAIL));

        ResultActions actions = mvc.perform(patch("/users/password")
                .content(content)
                .contentType(JSON_CONTENT_TYPE))
                .andExpect(status().isOk());

        actions.andDo(document("user/reset-password",
                ApiDocumentUtils.getDocumentRequest(),
                ApiDocumentUtils.getDocumentResponse(),
                requestFields(
                        fieldWithPath("email").description("가입한 이메일")
                )
        ));
    }
}