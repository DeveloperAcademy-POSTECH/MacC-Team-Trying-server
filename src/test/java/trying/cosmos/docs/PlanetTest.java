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
@DisplayName("[DOCS] 행성")
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
    @DisplayName("행성 생성")
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
                                .description("인증 토큰")
                ),
                requestFields(
                        fieldWithPath("name")
                                .type(STRING)
                                .description("행성 이름")
                                .attributes(constraints("2~8자리 한글/영어/숫자")),
                        fieldWithPath("image")
                                .type(STRING)
                                .description("행성 이미지 타입")
                ),
                responseFields(
                        fieldWithPath("code")
                                .type(STRING)
                                .description("행성 초대코드")
                )
        ));
    }

    @Test
    @DisplayName("행성 조회")
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
                                .description("인증 토큰")
                ),
                requestParameters(
                        parameterWithName("code")
                                .attributes(key("type").value("String"))
                                .description("행성 초대코드")
                ),
                responseFields(
                        fieldWithPath("name")
                                .type(STRING)
                                .description("행성 이름"),
                        fieldWithPath("image")
                                .type(STRING)
                                .description("행성 이미지 타입")
                )
        ));
    }

    @Test
    @DisplayName("행성 참가")
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
                                .description("인증 토큰")
                ),
                requestFields(
                        fieldWithPath("code")
                                .type(STRING)
                                .description("행성 초대코드")
                )
        ));
    }

    @Test
    @DisplayName("행성 수정")
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
                                .description("인증 토큰")
                ),
                requestFields(
                        fieldWithPath("name")
                                .type(STRING)
                                .description("변경할 행성 이름"),
                        fieldWithPath("date")
                                .type(STRING)
                                .description("처음 만난 날짜")
                                .attributes(constraints("형식: yyyy-MM-ss, 오늘 날짜 이후는 지정 불가")),
                        fieldWithPath("image")
                                .type(STRING)
                                .description("변경할 행성 이미지")
                ),
                responseFields(
                        fieldWithPath("name")
                                .type(STRING)
                                .description("변경할 행성 이름"),
                        fieldWithPath("dday")
                                .type(NUMBER)
                                .description("D+Day"),
                        fieldWithPath("image")
                                .type(STRING)
                                .description("변경할 행성 이미지")
                )
        ));
    }

    @Test
    @DisplayName("행성 나가기")
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
                                .description("인증 토큰")
                )
        ));
    }
}
