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
import trying.cosmos.domain.certification.dto.request.CertificateRequest;
import trying.cosmos.domain.certification.dto.request.GenerateCertificationRequest;
import trying.cosmos.domain.certification.entity.Certification;
import trying.cosmos.domain.certification.repository.CertificationRepository;
import trying.cosmos.domain.user.dto.request.UserJoinRequest;
import trying.cosmos.domain.user.dto.request.UserLoginRequest;
import trying.cosmos.domain.user.entity.User;
import trying.cosmos.domain.user.repository.UserRepository;
import trying.cosmos.global.auth.SessionService;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.JsonFieldType.BOOLEAN;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static trying.cosmos.docs.utils.RestDocsConfiguration.constraints;
import static trying.cosmos.test.TestVariables.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@DisplayName("[DOCS] 이메일 회원")
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
@ExtendWith({RestDocumentationExtension.class})
public class EmailUserTest {

    @Autowired
    CertificationRepository certificationRepository;

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
    @DisplayName("인증코드 생성")
    void generate() throws Exception {
        // GIVEN
        String content = objectMapper.writeValueAsString(new GenerateCertificationRequest(EMAIL1));

        // WHEN
        ResultActions actions = mvc.perform(post("/certification")
                .content(content)
                .contentType(JSON_CONTENT_TYPE));

        // THEN
        actions.andExpect(status().isOk());

        // DOCS
        actions.andDo(restDocs.document(
                requestFields(
                        fieldWithPath("email")
                                .type(STRING)
                                .description("인증코드를 받을 이메일")
                                .attributes(constraints("이메일 형식"))
                )
        ));
    }

    @Test
    @DisplayName("인증코드 확인")
    void certificate() throws Exception {
        // GIVEN
        Certification certification = certificationRepository.save(new Certification(EMAIL1));
        String content = objectMapper.writeValueAsString(new CertificateRequest(EMAIL1, certification.getCode()));

        // WHEN
        ResultActions actions = mvc.perform(patch("/certification")
                .content(content)
                .contentType(JSON_CONTENT_TYPE));

        // THEN
        actions.andExpect(status().isOk());

        // DOCS
        actions.andDo(restDocs.document(
                requestFields(
                        fieldWithPath("email")
                                .type(STRING)
                                .description("인증받을 이메일")
                                .attributes(constraints("이메일 형식")),
                        fieldWithPath("code")
                                .type(STRING)
                                .description("인증코드")
                )
        ));
    }

    @Test
    @DisplayName("이메일로 회원가입")
    void join() throws Exception {
        // GIVEN
        Certification certification = certificationRepository.save(new Certification(EMAIL1));
        certification.certificate(certification.getCode());
        String content = objectMapper.writeValueAsString(new UserJoinRequest(EMAIL1, PASSWORD, NAME1, DEVICE_TOKEN));

        // WHEN
        ResultActions actions = mvc.perform(post("/users")
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
                                .attributes(constraints("이메일 형식")),
                        fieldWithPath("password")
                                .type(STRING)
                                .description("비밀번호")
                                .attributes(constraints("8~16자리 영어/숫자/문자(!@#$%^&*)")),
                        fieldWithPath("name")
                                .type(STRING)
                                .description("닉네임")
                                .attributes(constraints("2~8자리 한글/영어/숫자")),
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
    @DisplayName("이메일로 로그인")
    void login() throws Exception {
        // GIVEN
        User user = userRepository.save(User.createEmailUser(EMAIL1, PASSWORD, NAME1, DEVICE_TOKEN));
        String content = objectMapper.writeValueAsString(new UserLoginRequest(EMAIL1, PASSWORD, DEVICE_TOKEN));

        // WHEN
        ResultActions actions = mvc.perform(post("/users/login")
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
                                .attributes(constraints("이메일 형식")),
                        fieldWithPath("password")
                                .type(STRING)
                                .description("비밀번호")
                                .attributes(constraints("8~16자리 영어/숫자/문자(!@#$%^&*)")),
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
