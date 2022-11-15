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
import trying.cosmos.domain.user.dto.request.SocialJoinRequest;
import trying.cosmos.domain.user.dto.request.SocialLoginRequest;
import trying.cosmos.domain.user.entity.User;
import trying.cosmos.domain.user.repository.UserRepository;
import trying.cosmos.global.auth.SessionService;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.JsonFieldType.BOOLEAN;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static trying.cosmos.docs.utils.DocsVariable.*;
import static trying.cosmos.docs.utils.RestDocsConfiguration.constraints;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@DisplayName("[DOCS] 소셜 회원")
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
@ExtendWith({RestDocumentationExtension.class})
public class SocialUserTest {

    @Autowired
    UserRepository userRepository;

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
    @DisplayName("애플 계정으로 회원가입")
    void joinWithApple() throws Exception {
        // GIVEN
        String content = objectMapper.writeValueAsString(new SocialJoinRequest(IDENTIFIER, MY_EMAIL, MY_NAME, DEVICE_TOKEN, true));

        // WHEN
        ResultActions actions = mvc.perform(post("/oauth/apple")
                .content(content)
                .contentType(JSON_CONTENT_TYPE));

        // THEN
        actions.andExpect(status().isOk());

        // DOCS
        actions.andDo(restDocs.document(
                requestFields(
                        fieldWithPath("identifier")
                                .type(STRING)
                                .description("애플 로그인시 반환되는 Identifier(appleIDCredential.user)"),
                        fieldWithPath("email")
                                .type(STRING)
                                .description("이메일")
                                .optional()
                                .attributes(constraints("이메일 형식")),
                        fieldWithPath("name")
                                .type(STRING)
                                .description("닉네임")
                                .attributes(constraints("2~8자리 한글/영어/숫자")),
                        fieldWithPath("deviceToken")
                                .type(STRING)
                                .description("디바이스 토큰"),
                        fieldWithPath("allowNotification")
                                .type(BOOLEAN)
                                .description("알림 허용 여부")
                ),
                responseFields(
                        fieldWithPath("accessToken")
                                .type(STRING)
                                .description("인증 토큰")
                )
        ));
    }

    @Test
    @DisplayName("애플 계정으로 로그인")
    void loginWithApple() throws Exception {
        // GIVEN
        User user = userRepository.save(User.createSocialUser("APPLE " + IDENTIFIER, MY_EMAIL, MY_NAME, DEVICE_TOKEN, true));
        String content = objectMapper.writeValueAsString(new SocialLoginRequest(IDENTIFIER, DEVICE_TOKEN));

        // WHEN
        ResultActions actions = mvc.perform(post("/oauth/apple/login")
                .content(content)
                .contentType(JSON_CONTENT_TYPE));

        // THEN
        actions.andExpect(status().isOk());

        // DOCS
        actions.andDo(restDocs.document(
                requestFields(
                        fieldWithPath("identifier")
                                .type(STRING)
                                .description("애플 로그인시 반환되는 Identifier(appleIDCredential.user)"),
                        fieldWithPath("deviceToken")
                                .type(STRING)
                                .description("디바이스 토큰")
                ),
                responseFields(
                        fieldWithPath("accessToken")
                                .type(STRING)
                                .description("인증 토큰")
                )
        ));
    }

    @Test
    @DisplayName("카카오 계정으로 회원가입")
    void joinWithKakao() throws Exception {
        // GIVEN
        String content = objectMapper.writeValueAsString(new SocialJoinRequest(IDENTIFIER, MY_EMAIL, MY_NAME, DEVICE_TOKEN, true));

        // WHEN
        ResultActions actions = mvc.perform(post("/oauth/kakao")
                .content(content)
                .contentType(JSON_CONTENT_TYPE));

        // THEN
        actions.andExpect(status().isOk());

        // DOCS
        actions.andDo(restDocs.document(
                requestFields(
                        fieldWithPath("identifier")
                                .type(STRING)
                                .description("카카오 로그인시 반환되는 id(UserApi.shared.me().id)"),
                        fieldWithPath("email")
                                .type(STRING)
                                .description("이메일")
                                .optional()
                                .attributes(constraints("이메일 형식")),
                        fieldWithPath("name")
                                .type(STRING)
                                .description("닉네임")
                                .attributes(constraints("2~8자리 한글/영어/숫자")),
                        fieldWithPath("deviceToken")
                                .type(STRING)
                                .description("디바이스 토큰"),
                        fieldWithPath("allowNotification")
                                .type(BOOLEAN)
                                .description("알림 허용 여부")
                ),
                responseFields(
                        fieldWithPath("accessToken")
                                .type(STRING)
                                .description("인증 토큰")
                )
        ));
    }

    @Test
    @DisplayName("카카오 계정으로 로그인")
    void loginWithKakao() throws Exception {
        // GIVEN
        User user = userRepository.save(User.createSocialUser("KAKAO " + IDENTIFIER, MY_EMAIL, MY_NAME, DEVICE_TOKEN, true));
        String content = objectMapper.writeValueAsString(new SocialLoginRequest(IDENTIFIER, DEVICE_TOKEN));

        // WHEN
        ResultActions actions = mvc.perform(post("/oauth/kakao/login")
                .content(content)
                .contentType(JSON_CONTENT_TYPE));

        // THEN
        actions.andExpect(status().isOk());

        // DOCS
        actions.andDo(restDocs.document(
                requestFields(
                        fieldWithPath("identifier")
                                .type(STRING)
                                .description("카카오 로그인시 반환되는 id(UserApi.shared.me().id)"),
                        fieldWithPath("deviceToken")
                                .type(STRING)
                                .description("디바이스 토큰")
                ),
                responseFields(
                        fieldWithPath("accessToken")
                                .type(STRING)
                                .description("인증 토큰")
                )
        ));
    }
}
