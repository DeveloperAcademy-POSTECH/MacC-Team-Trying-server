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
import trying.cosmos.controller.request.planet.PlanetCreateRequest;
import trying.cosmos.controller.request.planet.PlanetJoinRequest;
import trying.cosmos.docs.utils.ApiDocumentUtils;
import trying.cosmos.entity.Planet;
import trying.cosmos.entity.User;
import trying.cosmos.entity.component.Authority;
import trying.cosmos.entity.component.PlanetImageType;
import trying.cosmos.entity.component.UserStatus;
import trying.cosmos.repository.UserRepository;
import trying.cosmos.service.PlanetService;
import trying.cosmos.service.UserService;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Transactional
@ActiveProfiles("test")
public class PlanetDocsTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired UserRepository userRepository;
    @Autowired UserService userService;
    @Autowired PlanetService planetService;

    private static final String JSON_CONTENT_TYPE = "application/json";

    private static final String EMAIL = "email@gmail.com";
    private static final String PASSWORD = "password";
    private static final String NAME = "name";
    private static final String DEVICE_TOKEN = "device_token";
    private static final String PLANET_NAME = "포딩행성";

    @Test
    @DisplayName("행성 생성")
    void create() throws Exception {
        userRepository.save(new User(EMAIL, PASSWORD, NAME, UserStatus.LOGOUT, Authority.USER));
        String accessToken = userService.login(EMAIL, PASSWORD, DEVICE_TOKEN);

        String content = objectMapper.writeValueAsString(new PlanetCreateRequest(PLANET_NAME, PlanetImageType.EARTH));

        ResultActions actions = mvc.perform(post("/planets")
                .header("accessToken", accessToken)
                .contentType(JSON_CONTENT_TYPE)
                .content(content))
                .andExpect(status().isOk());

        actions.andDo(document("planet/create",
                ApiDocumentUtils.getDocumentRequest(),
                ApiDocumentUtils.getDocumentResponse(),
                requestHeaders(
                        headerWithName("accessToken").description("인증 토큰")
                ),
                requestFields(
                        fieldWithPath("name").description("행성 이름"),
                        fieldWithPath("planetImageType").description("행성 이미지 타입")
                ),
                responseFields(
                        fieldWithPath("id").description("생성된 행성 id"),
                        fieldWithPath("code").description("초대 코드")
                )
        ));
    }

    @Test
    @DisplayName("초대코드 조회")
    void get_invite_code() throws Exception {
        User user = userRepository.save(new User(EMAIL, PASSWORD, NAME, UserStatus.LOGOUT, Authority.USER));
        String accessToken = userService.login(EMAIL, PASSWORD, DEVICE_TOKEN);

        Planet planet = planetService.create(user.getId(), PLANET_NAME, PlanetImageType.EARTH);
        Long id = planet.getId();

        ResultActions actions = mvc.perform(get("/planets/{id}/code", id)
                .header("accessToken", accessToken)
                .contentType(JSON_CONTENT_TYPE))
                .andExpect(status().isOk());

        actions.andDo(document("planet/invite-code",
                ApiDocumentUtils.getDocumentRequest(),
                ApiDocumentUtils.getDocumentResponse(),
                requestHeaders(
                        headerWithName("accessToken").description("인증 토큰")
                ),
                pathParameters(
                        parameterWithName("id").description("행성 id")
                ),
                responseFields(
                        fieldWithPath("code").description("초대 코드")
                )
        ));
    }

    @Test
    @DisplayName("초대코드로 행성 조회")
    void find_by_invite_code() throws Exception {
        User user = userRepository.save(new User(EMAIL, PASSWORD, NAME, UserStatus.LOGOUT, Authority.USER));
        String accessToken = userService.login(EMAIL, PASSWORD, DEVICE_TOKEN);

        Planet planet = planetService.create(user.getId(), PLANET_NAME, PlanetImageType.EARTH);

        ResultActions actions = mvc.perform(get("/planets/join")
                .header("accessToken", accessToken)
                .param("code", planet.getInviteCode())
                .contentType(JSON_CONTENT_TYPE))
                .andExpect(status().isOk());

        actions.andDo(document("planet/find-by-code",
                ApiDocumentUtils.getDocumentRequest(),
                ApiDocumentUtils.getDocumentResponse(),
                requestHeaders(
                        headerWithName("accessToken").description("인증 토큰")
                ),
                requestParameters(
                        parameterWithName("code").description("초대 코드")
                ),
                responseFields(
                        fieldWithPath("id").description("행성 id"),
                        fieldWithPath("name").description("행성 이름"),
                        fieldWithPath("planetImageType").description("행성 이미지 타입")
                )
        ));
    }

    @Test
    @DisplayName("행성 참여")
    void join() throws Exception {
        User host = userRepository.save(new User("host@gmail.com", "hostpassword", "host", UserStatus.LOGOUT, Authority.USER));

        User user = userRepository.save(new User(EMAIL, PASSWORD, NAME, UserStatus.LOGOUT, Authority.USER));
        String accessToken = userService.login(EMAIL, PASSWORD, DEVICE_TOKEN);

        Planet planet = planetService.create(host.getId(), PLANET_NAME, PlanetImageType.EARTH);
        String content = objectMapper.writeValueAsString(new PlanetJoinRequest(planet.getInviteCode()));

        ResultActions actions = mvc.perform(post("/planets/join")
                .header("accessToken", accessToken)
                .content(content)
                .contentType(JSON_CONTENT_TYPE))
                .andExpect(status().isOk());

        actions.andDo(document("planet/join",
                ApiDocumentUtils.getDocumentRequest(),
                ApiDocumentUtils.getDocumentResponse(),
                requestHeaders(
                        headerWithName("accessToken").description("인증 토큰")
                ),
                requestFields(
                        fieldWithPath("code").description("초대 코드")
                )
        ));
    }

    @Test
    @DisplayName("행성 조회")
    void find() throws Exception {
        User user = userRepository.save(new User(EMAIL, PASSWORD, NAME, UserStatus.LOGOUT, Authority.USER));
        String accessToken = userService.login(EMAIL, PASSWORD, DEVICE_TOKEN);

        Planet planet = planetService.create(user.getId(), PLANET_NAME, PlanetImageType.EARTH);

        ResultActions actions = mvc.perform(get("/planets/{id}", planet.getId())
                .contentType(JSON_CONTENT_TYPE))
                .andExpect(status().isOk());

        actions.andDo(document("planet/find",
                ApiDocumentUtils.getDocumentRequest(),
                ApiDocumentUtils.getDocumentResponse(),
                pathParameters(
                        parameterWithName("id").description("행성 id")
                ),
                responseFields(
                        fieldWithPath("id").description("행성 id"),
                        fieldWithPath("name").description("행성 이름"),
                        fieldWithPath("planetImageType").description("행성 이미지 타입")
                )
        ));
    }

    @Test
    @DisplayName("행성 목록 조회")
    void find_list() throws Exception {
        User user = userRepository.save(new User(EMAIL, PASSWORD, NAME, UserStatus.LOGOUT, Authority.USER));
        String accessToken = userService.login(EMAIL, PASSWORD, DEVICE_TOKEN);

        Planet planet = planetService.create(user.getId(), PLANET_NAME, PlanetImageType.EARTH);

        ResultActions actions = mvc.perform(get("/planets")
                .param("query", "행성")
                .param("page", "0")
                .param("size", "5")
                .contentType(JSON_CONTENT_TYPE))
                .andExpect(status().isOk());

        actions.andDo(document("planet/find-list",
                ApiDocumentUtils.getDocumentRequest(),
                ApiDocumentUtils.getDocumentResponse(),
                requestParameters(
                        parameterWithName("query").description("검색하려는 행성 이름").optional(),
                        parameterWithName("page").description("무한스크롤 페이지 번호").optional(),
                        parameterWithName("size").description("무한스크롤 한 페이지 크기").optional()
                ),
                responseFields(
                        fieldWithPath("planets[].id").description("행성 id"),
                        fieldWithPath("planets[].name").description("행성 이름"),
                        fieldWithPath("planets[].planetImageType").description("행성 이미지 타입"),
                        fieldWithPath("size").description("검색결과 불러온 행성 수"),
                        fieldWithPath("hasNext").description("다음 페이지가 있는지")
                )
        ));
    }
}
