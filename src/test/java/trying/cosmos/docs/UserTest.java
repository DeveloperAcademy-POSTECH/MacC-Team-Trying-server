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
import static trying.cosmos.docs.utils.RestDocsConfiguration.constraints;
import static trying.cosmos.test.TestVariables.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@DisplayName("[DOCS] ??????")
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
    @DisplayName("????????????")
    void logout() throws Exception {
        // GIVEN
        User user = userRepository.save(User.createEmailUser(EMAIL1, PASSWORD, NAME1, DEVICE_TOKEN));
        String accessToken = userService.login(EMAIL1, PASSWORD, DEVICE_TOKEN);

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
                                .description("?????? ??????")
                )
        ));
    }

    @Test
    @DisplayName("????????????")
    void withdraw() throws Exception {
        // GIVEN
        User user = userRepository.save(User.createEmailUser(EMAIL1, PASSWORD, NAME1, DEVICE_TOKEN));
        String accessToken = userService.login(EMAIL1, PASSWORD, DEVICE_TOKEN);

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
                                .description("?????? ??????")
                )
        ));
    }

    @Test
    @DisplayName("?????? ?????? ?????? ??????")
    void setAllowNotification() throws Exception {
        // GIVEN
        User user = userRepository.save(User.createEmailUser(EMAIL1, PASSWORD, NAME1, DEVICE_TOKEN));
        String accessToken = userService.login(EMAIL1, PASSWORD, DEVICE_TOKEN);
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
                                .description("?????? ??????")
                ),
                requestFields(
                        fieldWithPath("allow")
                                .type(BOOLEAN)
                                .description("?????? ?????? ??????")
                )
        ));
    }

    @Test
    @DisplayName("????????? ??????")
    void updateName() throws Exception {
        // GIVEN
        User user = userRepository.save(User.createEmailUser(EMAIL1, PASSWORD, NAME1, DEVICE_TOKEN));
        String accessToken = userService.login(EMAIL1, PASSWORD, DEVICE_TOKEN);
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
                                .description("?????? ??????")
                ),
                requestFields(
                        fieldWithPath("name")
                                .type(STRING)
                                .description("?????????")
                                .attributes(constraints("2~8?????? ??????/??????/??????"))
                )
        ));
    }

    @Test
    @DisplayName("???????????? ??????")
    void updatePassword() throws Exception {
        // GIVEN
        User user = userRepository.save(User.createEmailUser(EMAIL1, PASSWORD, NAME1, DEVICE_TOKEN));
        String accessToken = userService.login(EMAIL1, PASSWORD, DEVICE_TOKEN);
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
                                .description("?????? ??????")
                ),
                requestFields(
                        fieldWithPath("previousPassword")
                                .type(STRING)
                                .description("?????? ??? ????????????"),
                        fieldWithPath("updatePassword")
                                .type(STRING)
                                .description("?????? ??? ????????????")
                                .attributes(constraints("8~16?????? ??????/??????/??????(!@#$%^&*)"))
                )
        ));
    }

    @Test
    @DisplayName("???????????? ?????????")
    void resetPassword() throws Exception {
        // GIVEN
        User user = userRepository.save(User.createEmailUser(EMAIL1, PASSWORD, NAME1, DEVICE_TOKEN));
        String content = objectMapper.writeValueAsString(new UserResetPasswordRequest(EMAIL1));

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
                                .description("?????????")
                                .attributes(constraints("????????? ??????"))
                )
        ));
    }

    @Test
    @DisplayName("??? ?????? ??????(??????, ????????? ??????)")
    void findMe() throws Exception {
        // GIVEN
        User user = userRepository.save(User.createEmailUser(EMAIL1, PASSWORD, NAME1, DEVICE_TOKEN));

        String accessToken = userService.login(EMAIL1, PASSWORD, DEVICE_TOKEN);

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
                                .description("?????? ??????")
                ),
                responseFields(
                        fieldWithPath("me.name")
                                .type(STRING)
                                .description("????????? ?????????"),
                        fieldWithPath("me.email")
                                .type(STRING)
                                .description("????????? ?????????"),
                        fieldWithPath("hasNotification")
                                .type(BOOLEAN)
                                .description("?????? ?????? ????????? ????????? ??????"),
                        fieldWithPath("socialAccount")
                                .type(BOOLEAN)
                                .description("?????? ?????? ??????"),
                        fieldWithPath("allowNotification")
                                .type(BOOLEAN)
                                .description("?????? ?????? ??????")
                )
        ));
    }

    @Test
    @DisplayName("??? ?????? ??????(????????? ??????)")
    void findMePlanet() throws Exception {
        // GIVEN
        User user = userRepository.save(User.createEmailUser(EMAIL1, PASSWORD, NAME1, DEVICE_TOKEN));
        Planet planet = planetRepository.save(new Planet(user, NAME1, IMAGE, INVITE_CODE));

        String accessToken = userService.login(EMAIL1, PASSWORD, DEVICE_TOKEN);

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
                                .description("?????? ??????")
                ),
                responseFields(
                        fieldWithPath("me.name")
                                .type(STRING)
                                .description("????????? ?????????"),
                        fieldWithPath("me.email")
                                .type(STRING)
                                .description("????????? ?????????"),
                        fieldWithPath("planet.name")
                                .type(STRING)
                                .description("?????? ??????"),
                        fieldWithPath("planet.meetDate")
                                .type(STRING)
                                .description("?????? ??????"),
                        fieldWithPath("planet.dday")
                                .type(NUMBER)
                                .description("D+Day(???????????? ?????? ????????? ??????)")
                                .attributes(constraints("??????(?????? ?????? ?????? ??????)")),
                        fieldWithPath("planet.image")
                                .type(STRING)
                                .description("?????? ????????? ??????"),
                        fieldWithPath("planet.code")
                                .type(STRING)
                                .description("?????? ?????? ??????"),
                        fieldWithPath("planet.hasBeenMateEntered")
                                .type(BOOLEAN)
                                .description("true??? ???????????? ????????? ?????? ??????, false??? ??????"),
                        fieldWithPath("socialAccount")
                                .type(BOOLEAN)
                                .description("?????? ?????? ??????"),
                        fieldWithPath("hasNotification")
                                .type(BOOLEAN)
                                .description("?????? ?????? ????????? ????????? ??????"),
                        fieldWithPath("allowNotification")
                                .type(BOOLEAN)
                                .description("?????? ?????? ??????")
                )
        ));
    }

    @Test
    @DisplayName("??? ?????? ??????(????????? ??????)")
    void findMeMate() throws Exception {
        // GIVEN
        User user = userRepository.save(User.createEmailUser(EMAIL1, PASSWORD, NAME1, DEVICE_TOKEN));
        User mate = userRepository.save(User.createEmailUser(EMAIL2, PASSWORD, NAME2, DEVICE_TOKEN));
        Planet planet = planetRepository.save(new Planet(user, NAME1, IMAGE, INVITE_CODE));
        planet.join(mate);

        String accessToken = userService.login(EMAIL1, PASSWORD, DEVICE_TOKEN);

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
                                .description("?????? ??????")
                ),
                responseFields(
                        fieldWithPath("me.name")
                                .type(STRING)
                                .description("????????? ?????????"),
                        fieldWithPath("me.email")
                                .type(STRING)
                                .description("????????? ?????????"),
                        fieldWithPath("mate.name")
                                .type(STRING)
                                .description("????????? ?????????"),
                        fieldWithPath("planet.name")
                                .type(STRING)
                                .description("?????? ??????"),
                        fieldWithPath("planet.meetDate")
                                .type(STRING)
                                .description("?????? ??????"),
                        fieldWithPath("planet.dday")
                                .type(NUMBER)
                                .description("D+Day(???????????? ?????? ????????? ??????)")
                                .attributes(constraints("??????(?????? ?????? ?????? ??????)")),
                        fieldWithPath("planet.image")
                                .type(STRING)
                                .description("?????? ????????? ??????"),
                        fieldWithPath("planet.hasBeenMateEntered")
                                .type(BOOLEAN)
                                .description("true??? ???????????? ????????? ?????? ??????, false??? ??????"),
                        fieldWithPath("socialAccount")
                                .type(BOOLEAN)
                                .description("?????? ?????? ??????"),
                        fieldWithPath("hasNotification")
                                .type(BOOLEAN)
                                .description("?????? ?????? ????????? ????????? ??????"),
                        fieldWithPath("allowNotification")
                                .type(BOOLEAN)
                                .description("?????? ?????? ??????"),
                        fieldWithPath("activities.courseCount")
                                .type(NUMBER)
                                .description("???????????? ????????? ?????? ???"),
                        fieldWithPath("activities.likedCount")
                                .type(NUMBER)
                                .description("???????????? ???????????? ?????? ?????? ???")
                )
        ));
    }
}
