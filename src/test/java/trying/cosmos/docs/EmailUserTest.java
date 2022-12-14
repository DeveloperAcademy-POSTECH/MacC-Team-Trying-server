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
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static trying.cosmos.docs.utils.RestDocsConfiguration.constraints;
import static trying.cosmos.test.TestVariables.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@DisplayName("[DOCS] ????????? ??????")
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
    @DisplayName("???????????? ??????")
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
                                .description("??????????????? ?????? ?????????")
                                .attributes(constraints("????????? ??????"))
                )
        ));
    }

    @Test
    @DisplayName("???????????? ??????")
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
                                .description("???????????? ?????????")
                                .attributes(constraints("????????? ??????")),
                        fieldWithPath("code")
                                .type(STRING)
                                .description("????????????")
                )
        ));
    }

    @Test
    @DisplayName("???????????? ????????????")
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
                                .description("?????????")
                                .attributes(constraints("????????? ??????")),
                        fieldWithPath("password")
                                .type(STRING)
                                .description("????????????")
                                .attributes(constraints("8~16?????? ??????/??????/??????(!@#$%^&*)")),
                        fieldWithPath("name")
                                .type(STRING)
                                .description("?????????")
                                .attributes(constraints("2~8?????? ??????/??????/??????")),
                        fieldWithPath("deviceToken")
                                .type(STRING)
                                .description("???????????? ??????")
                ),
                responseFields(
                        fieldWithPath("accessToken")
                                .type(STRING)
                                .description("?????? ??????")
                )
        ));
    }

    @Test
    @DisplayName("???????????? ?????????")
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
                                .description("?????????")
                                .attributes(constraints("????????? ??????")),
                        fieldWithPath("password")
                                .type(STRING)
                                .description("????????????")
                                .attributes(constraints("8~16?????? ??????/??????/??????(!@#$%^&*)")),
                        fieldWithPath("deviceToken")
                                .type(STRING)
                                .description("???????????? ??????")
                ),
                responseFields(
                        fieldWithPath("accessToken")
                                .type(STRING)
                                .description("?????? ??????")
                )
        ));
    }
}
