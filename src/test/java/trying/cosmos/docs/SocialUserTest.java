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
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static trying.cosmos.docs.utils.RestDocsConfiguration.constraints;
import static trying.cosmos.test.TestVariables.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@DisplayName("[DOCS] ?????? ??????")
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
    @DisplayName("?????? ???????????? ????????????")
    void joinWithApple() throws Exception {
        // GIVEN
        String content = objectMapper.writeValueAsString(new SocialJoinRequest(IDENTIFIER1, EMAIL1, NAME1, DEVICE_TOKEN));

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
                                .description("?????? ???????????? ???????????? identifier(appleIDCredential.user)"),
                        fieldWithPath("email")
                                .type(STRING)
                                .description("?????????")
                                .optional()
                                .attributes(constraints("????????? ??????")),
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
    @DisplayName("?????? ???????????? ?????????")
    void loginWithApple() throws Exception {
        // GIVEN
        User user = userRepository.save(User.createSocialUser("APPLE " + IDENTIFIER1, EMAIL1, NAME1, DEVICE_TOKEN));
        String content = objectMapper.writeValueAsString(new SocialLoginRequest(IDENTIFIER1, DEVICE_TOKEN));

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
                                .description("?????? ???????????? ???????????? identifier(appleIDCredential.user)"),
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
    @DisplayName("????????? ???????????? ????????????")
    void joinWithKakao() throws Exception {
        // GIVEN
        String content = objectMapper.writeValueAsString(new SocialJoinRequest(IDENTIFIER1, EMAIL1, NAME1, DEVICE_TOKEN));

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
                                .description("????????? ???????????? ???????????? id(UserApi.shared.me().id)"),
                        fieldWithPath("email")
                                .type(STRING)
                                .description("?????????")
                                .optional()
                                .attributes(constraints("????????? ??????")),
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
    @DisplayName("????????? ???????????? ?????????")
    void loginWithKakao() throws Exception {
        // GIVEN
        User user = userRepository.save(User.createSocialUser("KAKAO " + IDENTIFIER1, EMAIL1, NAME1, DEVICE_TOKEN));
        String content = objectMapper.writeValueAsString(new SocialLoginRequest(IDENTIFIER1, DEVICE_TOKEN));

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
                                .description("????????? ???????????? ???????????? id(UserApi.shared.me().id)"),
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
