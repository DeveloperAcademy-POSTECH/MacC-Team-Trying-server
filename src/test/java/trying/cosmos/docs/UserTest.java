package trying.cosmos.docs;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import trying.cosmos.docs.utils.RestDocsConfiguration;
import trying.cosmos.domain.planet.entity.Planet;
import trying.cosmos.domain.planet.repository.PlanetRepository;
import trying.cosmos.domain.user.dto.request.UserResetPasswordRequest;
import trying.cosmos.domain.user.dto.request.UserSetNotificationRequest;
import trying.cosmos.domain.user.dto.request.UserUpdateNameRequest;
import trying.cosmos.domain.user.dto.request.UserUpdatePasswordRequest;
import trying.cosmos.domain.user.entity.User;
import trying.cosmos.domain.user.repository.UserRepository;
import trying.cosmos.domain.user.service.UserService;
import trying.cosmos.global.auth.SessionService;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static trying.cosmos.docs.utils.DocsVariable.*;
import static trying.cosmos.docs.utils.RestDocsConfiguration.constraints;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@DisplayName("[DOCS] 유저")
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
@ExtendWith({RestDocumentationExtension.class})
public class UserTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    @Autowired
    PlanetRepository planetRepository;

    // Docs
    @Autowired
    MockMvc mvc;

    @Autowired
    RestDocumentationResultHandler restDocs;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    SessionService sessionService;

    @BeforeEach
    void setUp(RestDocumentationContextProvider provider, WebApplicationContext context){
        this.mvc= MockMvcBuilders.webAppContextSetup(context)
                .apply(MockMvcRestDocumentation.documentationConfiguration(provider))
                .alwaysDo(MockMvcResultHandlers.print())
                .alwaysDo(restDocs)
                .addFilters(new CharacterEncodingFilter("UTF-8",true))
                .build();
    }

    @AfterEach
    void clearSession() {
        sessionService.clear();
    }

    @Test
    @DisplayName("로그아웃")
    void logout() throws Exception {
        // GIVEN
        User user = userRepository.save(User.createEmailUser(MY_EMAIL, PASSWORD, MY_NAME, DEVICE_TOKEN));
        String accessToken = userService.login(MY_EMAIL, PASSWORD, DEVICE_TOKEN);

        // WHEN
        ResultActions actions = mvc.perform(delete("/users/logout")
                .header(ACCESS_TOKEN, accessToken)
                .contentType(JSON_CONTENT_TYPE));

        // THEN
        actions.andExpect(status().isOk());

        // DOCS
        actions.andDo(restDocs.document(
                requestHeaders(
                        headerWithName(ACCESS_TOKEN)
                                .description("인증 토큰")
                )
        ));
    }

    @Test
    @DisplayName("회원탈퇴")
    void withdraw() throws Exception {
        // GIVEN
        User user = userRepository.save(User.createEmailUser(MY_EMAIL, PASSWORD, MY_NAME, DEVICE_TOKEN));
        String accessToken = userService.login(MY_EMAIL, PASSWORD, DEVICE_TOKEN);

        // WHEN
        ResultActions actions = mvc.perform(delete("/users")
                .header(ACCESS_TOKEN, accessToken)
                .contentType(JSON_CONTENT_TYPE));

        // THEN
        actions.andExpect(status().isOk());

        // DOCS
        actions.andDo(restDocs.document(
                requestHeaders(
                        headerWithName(ACCESS_TOKEN)
                                .description("인증 토큰")
                )
        ));
    }

    @Test
    @DisplayName("알림 허용 여부 수정")
    void setAllowNotification() throws Exception {
        // GIVEN
        User user = userRepository.save(User.createEmailUser(MY_EMAIL, PASSWORD, MY_NAME, DEVICE_TOKEN));
        String accessToken = userService.login(MY_EMAIL, PASSWORD, DEVICE_TOKEN);
        String content = objectMapper.writeValueAsString(new UserSetNotificationRequest(true));

        // WHEN
        ResultActions actions = mvc.perform(patch("/users/notification")
                .header(ACCESS_TOKEN, accessToken)
                .content(content)
                .contentType(JSON_CONTENT_TYPE));

        // THEN
        actions.andExpect(status().isOk());

        // DOCS
        actions.andDo(restDocs.document(
                requestHeaders(
                        headerWithName(ACCESS_TOKEN)
                                .description("인증 토큰")
                ),
                requestFields(
                        fieldWithPath("allow")
                                .type(BOOLEAN)
                                .description("알림 허용 여부")
                )
        ));
    }

    @Test
    @DisplayName("닉네임 수정")
    void updateName() throws Exception {
        // GIVEN
        User user = userRepository.save(User.createEmailUser(MY_EMAIL, PASSWORD, MY_NAME, DEVICE_TOKEN));
        String accessToken = userService.login(MY_EMAIL, PASSWORD, DEVICE_TOKEN);
        String content = objectMapper.writeValueAsString(new UserUpdateNameRequest("UPDATED"));

        // WHEN
        ResultActions actions = mvc.perform(put("/users/name")
                .header(ACCESS_TOKEN, accessToken)
                .content(content)
                .contentType(JSON_CONTENT_TYPE));

        // THEN
        actions.andExpect(status().isOk());

        // DOCS
        actions.andDo(restDocs.document(
                requestHeaders(
                        headerWithName(ACCESS_TOKEN)
                                .description("인증 토큰")
                ),
                requestFields(
                        fieldWithPath("name")
                                .type(STRING)
                                .description("닉네임")
                                .attributes(constraints("2~8자리 한글/영어/숫자"))
                )
        ));
    }

    @Test
    @DisplayName("비밀번호 수정")
    void updatePassword() throws Exception {
        // GIVEN
        User user = userRepository.save(User.createEmailUser(MY_EMAIL, PASSWORD, MY_NAME, DEVICE_TOKEN));
        String accessToken = userService.login(MY_EMAIL, PASSWORD, DEVICE_TOKEN);
        String content = objectMapper.writeValueAsString(new UserUpdatePasswordRequest(PASSWORD, "!Updated1234"));

        // WHEN
        ResultActions actions = mvc.perform(put("/users/password")
                .header(ACCESS_TOKEN, accessToken)
                .content(content)
                .contentType(JSON_CONTENT_TYPE));

        // THEN
        actions.andExpect(status().isOk());

        // DOCS
        actions.andDo(restDocs.document(
                requestHeaders(
                        headerWithName(ACCESS_TOKEN)
                                .description("인증 토큰")
                ),
                requestFields(
                        fieldWithPath("previousPassword")
                                .type(STRING)
                                .description("변경 전 비밀번호"),
                        fieldWithPath("updatePassword")
                                .type(STRING)
                                .description("변경 후 비밀번호")
                                .attributes(constraints("8~16자리 영어/숫자/문자(!@#$%^&*)"))
                )
        ));
    }

    @Test
    @DisplayName("비밀번호 초기화")
    void resetPassword() throws Exception {
        // GIVEN
        User user = userRepository.save(User.createEmailUser(MY_EMAIL, PASSWORD, MY_NAME, DEVICE_TOKEN));
        String content = objectMapper.writeValueAsString(new UserResetPasswordRequest(MY_EMAIL));

        // WHEN
        ResultActions actions = mvc.perform(patch("/users/password")
                .content(content)
                .contentType(JSON_CONTENT_TYPE));

        // THEN
        actions.andExpect(status().isOk());

        // DOCS
        actions.andDo(restDocs.document(
                requestFields(
                        fieldWithPath("email")
                                .type(STRING)
                                .description("이메일")
                                .attributes(constraints("이메일 형식"))
                )
        ));
    }

    @Test
    @DisplayName("내 정보 조회(행성, 메이트 없음)")
    void findMe() throws Exception {
        // GIVEN
        User user = userRepository.save(User.createEmailUser(MY_EMAIL, PASSWORD, MY_NAME, DEVICE_TOKEN));

        String accessToken = userService.login(MY_EMAIL, PASSWORD, DEVICE_TOKEN);

        // WHEN
        ResultActions actions = mvc.perform(get("/users")
                .header(ACCESS_TOKEN, accessToken)
                .contentType(JSON_CONTENT_TYPE));

        // THEN
        actions.andExpect(status().isOk());

        // DOCS
        actions.andDo(restDocs.document(
                requestHeaders(
                        headerWithName(ACCESS_TOKEN)
                                .description("인증 토큰")
                ),
                responseFields(
                        fieldWithPath("me.name")
                                .type(STRING)
                                .description("사용자 닉네임"),
                        fieldWithPath("me.email")
                                .type(STRING)
                                .description("사용자 이메일"),
                        fieldWithPath("hasNotification")
                                .type(BOOLEAN)
                                .description("읽지 않은 알림이 있는지 여부"),
                        fieldWithPath("socialAccount")
                                .type(BOOLEAN)
                                .description("소셜 계정 여부"),
                        fieldWithPath("allowNotification")
                                .type(BOOLEAN)
                                .description("알림 허용 여부")
                )
        ));
    }

    @Test
    @DisplayName("내 정보 조회(메이트 없음)")
    void findMePlanet() throws Exception {
        // GIVEN
        User user = userRepository.save(User.createEmailUser(MY_EMAIL, PASSWORD, MY_NAME, DEVICE_TOKEN));
        Planet planet = planetRepository.save(new Planet(user, PLANET_NAME, IMAGE_NAME, INVITE_CODE));

        String accessToken = userService.login(MY_EMAIL, PASSWORD, DEVICE_TOKEN);

        // WHEN
        ResultActions actions = mvc.perform(get("/users")
                .header(ACCESS_TOKEN, accessToken)
                .contentType(JSON_CONTENT_TYPE));

        // THEN
        actions.andExpect(status().isOk());

        // DOCS
        actions.andDo(restDocs.document(
                requestHeaders(
                        headerWithName(ACCESS_TOKEN)
                                .description("인증 토큰")
                ),
                responseFields(
                        fieldWithPath("me.name")
                                .type(STRING)
                                .description("사용자 닉네임"),
                        fieldWithPath("me.email")
                                .type(STRING)
                                .description("사용자 이메일"),
                        fieldWithPath("planet.name")
                                .type(STRING)
                                .description("행성 이름"),
                        fieldWithPath("planet.meetDate")
                                .type(STRING)
                                .description("만난 날짜"),
                        fieldWithPath("planet.dday")
                                .type(NUMBER)
                                .description("D+Day(초기값은 행성 생성일 기준)")
                                .attributes(constraints("양수(오늘 이전 날짜 불가)")),
                        fieldWithPath("planet.image")
                                .type(STRING)
                                .description("행성 이미지 타입"),
                        fieldWithPath("planet.code")
                                .type(STRING)
                                .description("행성 초대 코드"),
                        fieldWithPath("socialAccount")
                                .type(BOOLEAN)
                                .description("소셜 계정 여부"),
                        fieldWithPath("hasNotification")
                                .type(BOOLEAN)
                                .description("읽지 않은 알림이 있는지 여부"),
                        fieldWithPath("allowNotification")
                                .type(BOOLEAN)
                                .description("알림 허용 여부")
                )
        ));
    }

    @Test
    @DisplayName("내 정보 조회(메이트 존재)")
    void findMeMate() throws Exception {
        // GIVEN
        User user = userRepository.save(User.createEmailUser(MY_EMAIL, PASSWORD, MY_NAME, DEVICE_TOKEN));
        User mate = userRepository.save(User.createEmailUser(MATE_EMAIL, PASSWORD, MATE_NAME, DEVICE_TOKEN));
        Planet planet = planetRepository.save(new Planet(user, PLANET_NAME, IMAGE_NAME, INVITE_CODE));
        planet.join(mate);

        String accessToken = userService.login(MY_EMAIL, PASSWORD, DEVICE_TOKEN);

        // WHEN
        ResultActions actions = mvc.perform(get("/users")
                .header(ACCESS_TOKEN, accessToken)
                .contentType(JSON_CONTENT_TYPE));

        // THEN
        actions.andExpect(status().isOk());

        // DOCS
        actions.andDo(restDocs.document(
                requestHeaders(
                        headerWithName(ACCESS_TOKEN)
                                .description("인증 토큰")
                ),
                responseFields(
                        fieldWithPath("me.name")
                                .type(STRING)
                                .description("사용자 닉네임"),
                        fieldWithPath("me.email")
                                .type(STRING)
                                .description("사용자 이메일"),
                        fieldWithPath("mate.name")
                                .type(STRING)
                                .description("메이트 닉네임"),
                        fieldWithPath("planet.name")
                                .type(STRING)
                                .description("행성 이름"),
                        fieldWithPath("planet.meetDate")
                                .type(STRING)
                                .description("만난 날짜"),
                        fieldWithPath("planet.dday")
                                .type(NUMBER)
                                .description("D+Day(초기값은 행성 생성일 기준)")
                                .attributes(constraints("양수(오늘 이전 날짜 불가)")),
                        fieldWithPath("planet.image")
                                .type(STRING)
                                .description("행성 이미지 타입"),
                        fieldWithPath("socialAccount")
                                .type(BOOLEAN)
                                .description("소셜 계정 여부"),
                        fieldWithPath("hasNotification")
                                .type(BOOLEAN)
                                .description("읽지 않은 알림이 있는지 여부"),
                        fieldWithPath("allowNotification")
                                .type(BOOLEAN)
                                .description("알림 허용 여부"),
                        fieldWithPath("activities.courseCount")
                                .type(NUMBER)
                                .description("사용자가 작성한 코스 수"),
                        fieldWithPath("activities.likedCount")
                                .type(NUMBER)
                                .description("사용자가 좋아요를 누른 코스 수")
                )
        ));
    }
}
