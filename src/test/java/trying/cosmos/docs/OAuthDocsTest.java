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
import trying.cosmos.domain.certification.entity.Certification;
import trying.cosmos.domain.certification.repository.CertificationRepository;
import trying.cosmos.domain.certification.service.CertificationService;
import trying.cosmos.domain.user.controller.UserController;
import trying.cosmos.domain.user.dto.request.SocialJoinRequest;
import trying.cosmos.domain.user.dto.request.SocialLoginRequest;
import trying.cosmos.domain.user.entity.User;
import trying.cosmos.domain.user.repository.UserRepository;
import trying.cosmos.domain.user.service.SocialAccountService;
import trying.cosmos.global.auth.SessionService;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
@ExtendWith({RestDocumentationExtension.class})
@Transactional
@ActiveProfiles("test")
public class OAuthDocsTest {

    @Autowired
    MockMvc mvc;
    @Autowired
    UserController userController;
    @Autowired
    SessionService sessionService;
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UserRepository userRepository;
    @Autowired
    CertificationService certificationService;
    @Autowired
    CertificationRepository certificationRepository;
    @Autowired
    SocialAccountService socialAccountService;

    @Autowired
    RestDocumentationResultHandler restDocs;

    private static final String JSON_CONTENT_TYPE = "application/json";

    private static final String IDENTIFIER = "identifier";
    private static final String EMAIL = "email@gmail.com";
    private static final String NAME = "name";
    private static final String DEVICE_TOKEN = "device_token";

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
    void clear() {
        sessionService.clear();
    }

    @Test
    @DisplayName("애플 회원가입")
    void join_apple() throws Exception {
        certificationService.generate(EMAIL);
        Certification certification = certificationRepository.findByEmail(EMAIL).orElseThrow();
        certificationService.certificate(certification.getEmail(), certification.getCode());

        String content = objectMapper.writeValueAsString(new SocialJoinRequest(IDENTIFIER, EMAIL, NAME, DEVICE_TOKEN));

        ResultActions actions = mvc.perform(post("/oauth/apple")
                        .content(content)
                        .contentType(JSON_CONTENT_TYPE))
                .andExpect(status().isOk());

        actions.andDo(restDocs.document(
                requestFields(
                        fieldWithPath("identifier")
                                .type(STRING)
                                .description("애플 로그인 시 반환되는 user identifier"),
                        fieldWithPath("email")
                                .type(STRING)
                                .description("애플 회원가입 시 반환되는 user email")
                                .attributes(key("constraint").value("이메일 형식")),
                        fieldWithPath("name")
                                .type(STRING)
                                .description("닉네임")
                                .attributes(key("constraint").value("2~8자리 한글/영어/숫자")),
                        fieldWithPath("deviceToken")
                                .type(STRING)
                                .description("푸시 알림을 위한 기기 고유 토큰")
                ),
                responseFields(
                        fieldWithPath("accessToken")
                                .description("인증 토큰")
                )
        ));
    }

    @Test
    @DisplayName("애플 로그인")
    void login_apple() throws Exception {
        userRepository.save(User.createSocialUser("APPLE " + IDENTIFIER, EMAIL, NAME, DEVICE_TOKEN));

        String content = objectMapper.writeValueAsString(new SocialLoginRequest(IDENTIFIER, DEVICE_TOKEN));

        ResultActions actions = mvc.perform(post("/oauth/apple/login")
                        .content(content)
                        .contentType(JSON_CONTENT_TYPE))
                .andExpect(status().isOk());

        actions.andDo(restDocs.document(
                requestFields(
                        fieldWithPath("identifier")
                                .type(STRING)
                                .description("애플 로그인 시 반환되는 user identifier"),
                        fieldWithPath("deviceToken")
                                .type(STRING)
                                .description("푸시 알림을 위한 기기 고유 토큰")
                ),
                responseFields(
                        fieldWithPath("accessToken")
                                .description("인증 토큰")
                )
        ));
    }

    @Test
    @DisplayName("카카오 회원가입")
    void join_kakao() throws Exception {
        certificationService.generate(EMAIL);
        Certification certification = certificationRepository.findByEmail(EMAIL).orElseThrow();
        certificationService.certificate(certification.getEmail(), certification.getCode());

        String content = objectMapper.writeValueAsString(new SocialJoinRequest(IDENTIFIER, EMAIL, NAME, DEVICE_TOKEN));

        ResultActions actions = mvc.perform(post("/oauth/kakao")
                        .content(content)
                        .contentType(JSON_CONTENT_TYPE))
                .andExpect(status().isOk());

        actions.andDo(restDocs.document(
                requestFields(
                        fieldWithPath("identifier")
                                .type(STRING)
                                .description("카카오 로그인 후 사용자 정보 조회시 반환되는 id"),
                        fieldWithPath("email")
                                .type(STRING)
                                .description("카카오 로그인 후 사용자 정보 조회시 반환되는 이메일")
                                .attributes(key("constraint").value("이메일 형식")),
                        fieldWithPath("name")
                                .type(STRING)
                                .description("닉네임")
                                .attributes(key("constraint").value("2~8자리 한글/영어/숫자")),
                        fieldWithPath("deviceToken")
                                .type(STRING)
                                .description("푸시 알림을 위한 기기 고유 토큰")
                ),
                responseFields(
                        fieldWithPath("accessToken")
                                .description("인증 토큰")
                )
        ));
    }

    @Test
    @DisplayName("카카오 로그인")
    void login_kakao() throws Exception {
        userRepository.save(User.createSocialUser("KAKAO " + IDENTIFIER, EMAIL, NAME, DEVICE_TOKEN));

        String content = objectMapper.writeValueAsString(new SocialLoginRequest(IDENTIFIER, DEVICE_TOKEN));

        ResultActions actions = mvc.perform(post("/oauth/kakao/login")
                        .content(content)
                        .contentType(JSON_CONTENT_TYPE))
                .andExpect(status().isOk());

        actions.andDo(restDocs.document(
                requestFields(
                        fieldWithPath("identifier")
                                .type(STRING)
                                .description("카카오 로그인 후 사용자 정보 조회시 반환되는 id"),
                        fieldWithPath("deviceToken")
                                .type(STRING)
                                .description("푸시 알림을 위한 기기 고유 토큰")
                ),
                responseFields(
                        fieldWithPath("accessToken")
                                .description("인증 토큰")
                )
        ));
    }
}
