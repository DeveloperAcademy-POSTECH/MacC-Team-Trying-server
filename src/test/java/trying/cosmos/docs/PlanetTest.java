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
import trying.cosmos.domain.planet.dto.request.PlanetCreateRequest;
import trying.cosmos.domain.planet.dto.request.PlanetJoinRequest;
import trying.cosmos.domain.planet.dto.request.PlanetUpdateRequest;
import trying.cosmos.domain.planet.entity.Planet;
import trying.cosmos.domain.planet.repository.PlanetRepository;
import trying.cosmos.domain.user.entity.User;
import trying.cosmos.domain.user.repository.UserRepository;
import trying.cosmos.domain.user.service.UserService;
import trying.cosmos.global.auth.SessionService;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
public class PlanetTest {

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
    @DisplayName("?????? ??????")
    void create() throws Exception {
        // GIVEN
        User user = userRepository.save(User.createEmailUser(EMAIL1, PASSWORD, NAME1, DEVICE_TOKEN));
        String accessToken = userService.login(EMAIL1, PASSWORD, DEVICE_TOKEN);

        String content = objectMapper.writeValueAsString(new PlanetCreateRequest(NAME1, IMAGE));

        // WHEN
        ResultActions actions = mvc.perform(post("/planets")
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
                                .description("?????? ??????")
                                .attributes(constraints("2~8?????? ??????/??????/??????")),
                        fieldWithPath("image")
                                .type(STRING)
                                .description("?????? ????????? ??????")
                ),
                responseFields(
                        fieldWithPath("code")
                                .type(STRING)
                                .description("?????? ????????????")
                )
        ));
    }

    @Test
    @DisplayName("?????? ??????")
    void find() throws Exception {
        // GIVEN
        User user = userRepository.save(User.createEmailUser(EMAIL1, PASSWORD, NAME1, DEVICE_TOKEN));
        Planet planet = planetRepository.save(new Planet(user, NAME1, IMAGE, INVITE_CODE));
        String accessToken = userService.login(EMAIL1, PASSWORD, DEVICE_TOKEN);

        // WHEN
        ResultActions actions = mvc.perform(get("/planets")
                .header(ACCESS_TOKEN, accessToken)
                .param("code", planet.getInviteCode())
                .contentType(JSON_CONTENT_TYPE));

        // THEN
        actions.andExpect(status().isOk());

        // DOCS
        actions.andDo(restDocs.document(
                requestHeaders(
                        headerWithName(ACCESS_TOKEN)
                                .description("?????? ??????")
                ),
                requestParameters(
                        parameterWithName("code")
                                .attributes(key("type").value("String"))
                                .description("?????? ????????????")
                ),
                responseFields(
                        fieldWithPath("name")
                                .type(STRING)
                                .description("?????? ??????"),
                        fieldWithPath("image")
                                .type(STRING)
                                .description("?????? ????????? ??????")
                )
        ));
    }

    @Test
    @DisplayName("?????? ??????")
    void join() throws Exception {
        // GIVEN
        User user = userRepository.save(User.createEmailUser(EMAIL1, PASSWORD, NAME1, DEVICE_TOKEN));
        User mate = userRepository.save(User.createEmailUser(EMAIL2, PASSWORD, NAME2, DEVICE_TOKEN));
        Planet planet = planetRepository.save(new Planet(mate, NAME1, IMAGE, INVITE_CODE));
        String accessToken = userService.login(EMAIL1, PASSWORD, DEVICE_TOKEN);

        String content = objectMapper.writeValueAsString(new PlanetJoinRequest(INVITE_CODE));

        // WHEN
        ResultActions actions = mvc.perform(post("/planets/join")
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
                        fieldWithPath("code")
                                .type(STRING)
                                .description("?????? ????????????")
                )
        ));
    }

    @Test
    @DisplayName("?????? ??????")
    void update() throws Exception {
        // GIVEN
        User user = userRepository.save(User.createEmailUser(EMAIL1, PASSWORD, NAME1, DEVICE_TOKEN));
        User mate = userRepository.save(User.createEmailUser(EMAIL2, PASSWORD, NAME2, DEVICE_TOKEN));
        Planet planet = planetRepository.save(new Planet(user, NAME1, IMAGE, INVITE_CODE));
        planet.join(mate);
        String accessToken = userService.login(EMAIL1, PASSWORD, DEVICE_TOKEN);

        String content = objectMapper.writeValueAsString(new PlanetUpdateRequest("UPDATED", "2020-01-10", IMAGE));

        // WHEN
        ResultActions actions = mvc.perform(put("/planets")
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
                                .description("????????? ?????? ??????"),
                        fieldWithPath("date")
                                .type(STRING)
                                .description("?????? ?????? ??????")
                                .attributes(constraints("??????: yyyy-MM-ss, ?????? ?????? ????????? ?????? ??????")),
                        fieldWithPath("image")
                                .type(STRING)
                                .description("????????? ?????? ?????????")
                ),
                responseFields(
                        fieldWithPath("name")
                                .type(STRING)
                                .description("????????? ?????? ??????"),
                        fieldWithPath("dday")
                                .type(NUMBER)
                                .description("D+Day"),
                        fieldWithPath("image")
                                .type(STRING)
                                .description("????????? ?????? ?????????")
                )
        ));
    }

    @Test
    @DisplayName("?????? ?????????")
    void leave() throws Exception {
        // GIVEN
        User user = userRepository.save(User.createEmailUser(EMAIL1, PASSWORD, NAME1, DEVICE_TOKEN));
        User mate = userRepository.save(User.createEmailUser(EMAIL2, PASSWORD, NAME2, DEVICE_TOKEN));
        Planet planet = planetRepository.save(new Planet(user, NAME1, IMAGE, INVITE_CODE));
        planet.join(mate);
        String accessToken = userService.login(EMAIL1, PASSWORD, DEVICE_TOKEN);

        // WHEN
        ResultActions actions = mvc.perform(delete("/planets")
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
}
