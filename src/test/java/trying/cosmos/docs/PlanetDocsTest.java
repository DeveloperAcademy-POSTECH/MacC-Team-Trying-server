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
import trying.cosmos.domain.course.dto.request.TagCreateRequest;
import trying.cosmos.domain.course.entity.Access;
import trying.cosmos.domain.course.service.CourseService;
import trying.cosmos.domain.place.dto.request.PlaceCreateRequest;
import trying.cosmos.domain.planet.dto.request.PlanetUpdateRequest;
import trying.cosmos.domain.planet.dto.response.PlanetCreateRequest;
import trying.cosmos.domain.planet.dto.response.PlanetJoinRequest;
import trying.cosmos.domain.planet.entity.Planet;
import trying.cosmos.domain.planet.entity.PlanetImageType;
import trying.cosmos.domain.planet.service.PlanetService;
import trying.cosmos.domain.user.entity.User;
import trying.cosmos.domain.user.entity.UserStatus;
import trying.cosmos.domain.user.repository.UserRepository;
import trying.cosmos.domain.user.service.UserService;
import trying.cosmos.global.auth.entity.Authority;
import trying.cosmos.global.auth.repository.SessionRepository;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
@ExtendWith({RestDocumentationExtension.class})
@Transactional
@ActiveProfiles("test")
public class PlanetDocsTest {

    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    UserRepository userRepository;
    @Autowired
    SessionRepository sessionRepository;
    @Autowired
    UserService userService;
    @Autowired
    PlanetService planetService;
    @Autowired
    CourseService courseService;

    @Autowired
    RestDocumentationResultHandler restDocs;

    private static final String JSON_CONTENT_TYPE = "application/json";

    private static final String EMAIL = "email@gmail.com";
    private static final String PASSWORD = "password";
    private static final String NAME = "name";
    private static final String DEVICE_TOKEN = "device_token";
    private static final String PLANET_NAME = "포딩행성";
    private static final String BODY = "굿";

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
        sessionRepository.deleteAll();
    }

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

        actions.andDo(restDocs.document(
                requestHeaders(
                        headerWithName("accessToken")
                                .description("인증 토큰")
                ),
                requestFields(
                        fieldWithPath("name")
                                .type(STRING)
                                .description("행성 이름")
                                .attributes(key("constraint").value("2~8자리 한글/영어/숫자")),
                        fieldWithPath("image")
                                .type(VARIES)
                                .description("행성 이미지 타입")
                ),
                responseFields(
                        fieldWithPath("planetId")
                                .description("생성된 행성 id"),
                        fieldWithPath("code")
                                .description("초대 코드")
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

        ResultActions actions = mvc.perform(get("/planets/{planetId}/code", id)
                .header("accessToken", accessToken)
                .contentType(JSON_CONTENT_TYPE))
                .andExpect(status().isOk());

        actions.andDo(restDocs.document(
                requestHeaders(
                        headerWithName("accessToken")
                                .description("인증 토큰")
                ),
                pathParameters(
                        parameterWithName("planetId")
                                .description("행성 id")
                                .attributes(key("type").value("Number"))
                ),
                responseFields(
                        fieldWithPath("code")
                                .description("초대 코드")
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

        actions.andDo(restDocs.document(
                requestHeaders(
                        headerWithName("accessToken")
                                .description("인증 토큰")
                ),
                requestParameters(
                        parameterWithName("code")
                                .description("초대 코드")
                                .attributes(key("type").value("String"))
                ),
                responseFields(
                        fieldWithPath("planetId")
                                .description("행성 id"),
                        fieldWithPath("name")
                                .description("행성 이름"),
                        fieldWithPath("image")
                                .description("행성 이미지 타입")
                )
        ));
    }

    @Test
    @DisplayName("행성 참여")
    void join() throws Exception {
        User host = userRepository.save(new User("host@gmail.com", "hostpassword", "host", UserStatus.LOGOUT, Authority.USER));

        userRepository.save(new User(EMAIL, PASSWORD, NAME, UserStatus.LOGOUT, Authority.USER));
        String accessToken = userService.login(EMAIL, PASSWORD, DEVICE_TOKEN);

        Planet planet = planetService.create(host.getId(), PLANET_NAME, PlanetImageType.EARTH);
        String content = objectMapper.writeValueAsString(new PlanetJoinRequest(planet.getInviteCode()));

        ResultActions actions = mvc.perform(post("/planets/join")
                .header("accessToken", accessToken)
                .content(content)
                .contentType(JSON_CONTENT_TYPE))
                .andExpect(status().isOk());

        actions.andDo(restDocs.document(
                requestHeaders(
                        headerWithName("accessToken")
                                .description("인증 토큰")
                ),
                requestFields(
                        fieldWithPath("code")
                                .type(STRING)
                                .description("초대 코드")
                )
        ));
    }

    @Test
    @DisplayName("행성 조회")
    void find_by_id() throws Exception {
        User user = userRepository.save(new User(EMAIL, PASSWORD, NAME, UserStatus.LOGOUT, Authority.USER));
        userService.login(EMAIL, PASSWORD, DEVICE_TOKEN);

        Planet planet = planetService.create(user.getId(), PLANET_NAME, PlanetImageType.EARTH);

        ResultActions actions = mvc.perform(get("/planets/{planetId}", planet.getId())
                .contentType(JSON_CONTENT_TYPE))
                .andExpect(status().isOk());

        actions.andDo(restDocs.document(
                pathParameters(
                        parameterWithName("planetId")
                                .description("행성 id")
                                .attributes(key("type").value("Number"))
                ),
                responseFields(
                        fieldWithPath("planetId")
                                .description("행성 id"),
                        fieldWithPath("name")
                                .description("행성 이름"),
                        fieldWithPath("image")
                                .description("행성 이미지 타입"),
                        fieldWithPath("dday")
                                .description("행성 dday")
                )
        ));
    }

    @Test
    @DisplayName("이름으로 행성 조회")
    void find_by_name() throws Exception {
        User user = userRepository.save(new User(EMAIL, PASSWORD, NAME, UserStatus.LOGOUT, Authority.USER));
        userService.login(EMAIL, PASSWORD, DEVICE_TOKEN);

        Planet planet = planetService.create(user.getId(), PLANET_NAME, PlanetImageType.EARTH);

        ResultActions actions = mvc.perform(get("/planets")
                .param("query", "행성")
                .param("page", "0")
                .param("size", "5")
                .contentType(JSON_CONTENT_TYPE))
                .andExpect(status().isOk());

        actions.andDo(restDocs.document(
                requestParameters(
                        parameterWithName("query")
                                .description("검색하려는 행성 이름")
                                .attributes(key("type").value("String"))
                                .optional(),
                        parameterWithName("page")
                                .description("페이지 번호")
                                .attributes(key("type").value("Number"))
                                .optional(),
                        parameterWithName("size")
                                .description("한 페이지 크기")
                                .attributes(key("type").value("Number"))
                                .optional()
                ),
                responseFields(
                        fieldWithPath("planets[].planetId")
                                .description("행성 id"),
                        fieldWithPath("planets[].name")
                                .description("행성 이름"),
                        fieldWithPath("planets[].image")
                                .description("행성 이미지 타입"),
                        fieldWithPath("size")
                                .description("검색결과 불러온 행성 수"),
                        fieldWithPath("hasNext")
                                .description("다음 페이지가 있는지")
                )
        ));
    }

    @Test
    @DisplayName("팔로우한 행성 조회")
    void get_follow_planet() throws Exception {
        User user = userRepository.save(new User(EMAIL, PASSWORD, NAME, UserStatus.LOGOUT, Authority.USER));
        User follow1 = userRepository.save(new User("follow1@gmail.com", PASSWORD, "follow1", UserStatus.LOGOUT, Authority.USER));
        User follow2 = userRepository.save(new User("follow2@gmail.com", PASSWORD, "follow2", UserStatus.LOGOUT, Authority.USER));
        String accessToken = userService.login(EMAIL, PASSWORD, DEVICE_TOKEN);

        planetService.create(user.getId(), PLANET_NAME, PlanetImageType.EARTH);
        Planet followPlanet1 = planetService.create(follow1.getId(), PLANET_NAME, PlanetImageType.EARTH);
        Planet followPlanet2 = planetService.create(follow2.getId(), PLANET_NAME, PlanetImageType.EARTH);
        planetService.follow(user.getId(), followPlanet1.getId());
        planetService.follow(user.getId(), followPlanet2.getId());

        ResultActions actions = mvc.perform(get("/planets/follow")
                .header("accessToken", accessToken)
                .param("page", "0")
                .param("size", "5")
                .contentType(JSON_CONTENT_TYPE))
                .andExpect(status().isOk());

        actions.andDo(restDocs.document(
                requestHeaders(
                        headerWithName("accessToken")
                                .description("인증 토큰")
                ),
                requestParameters(
                        parameterWithName("page")
                                .description("페이지 번호")
                                .attributes(key("type").value("Number"))
                                .optional(),
                        parameterWithName("size")
                                .description("한 페이지 크기")
                                .attributes(key("type").value("Number"))
                                .optional()
                ),
                responseFields(
                        fieldWithPath("planets[].planetId")
                                .description("행성 id"),
                        fieldWithPath("planets[].name")
                                .description("행성 이름"),
                        fieldWithPath("planets[].image")
                                .description("행성 이미지 타입"),
                        fieldWithPath("size")
                                .description("검색결과 불러온 행성 수"),
                        fieldWithPath("hasNext")
                                .description("다음 페이지가 있는지")
                )
        ));
    }

    @Test
    @DisplayName("행성 코스 목록 조회")
    void find_planet_course() throws Exception {
        User user = userRepository.save(new User(EMAIL, PASSWORD, NAME, UserStatus.LOGOUT, Authority.USER));
        String accessToken = userService.login(EMAIL, PASSWORD, DEVICE_TOKEN);

        Planet planet = planetService.create(user.getId(), PLANET_NAME, PlanetImageType.EARTH);

        List<TagCreateRequest> tagDto1 = new ArrayList<>();
        tagDto1.add(new TagCreateRequest(new PlaceCreateRequest(1L, "참뼈 효자시장점", 36.4, 124.0), "참뼈"));
        tagDto1.add(new TagCreateRequest(new PlaceCreateRequest(2L, "맥도날드 포항점", 37.0, 125.3), "맥도날드"));
        tagDto1.add(new TagCreateRequest(new PlaceCreateRequest(3L, "명륜진사갈비", 35.1, 122.1), "명륜진사갈비"));

        List<TagCreateRequest> tagDto2 = new ArrayList<>();
        tagDto2.add(new TagCreateRequest(new PlaceCreateRequest(4L, "그여든", 36.7, 128.5), "삐갈레 브래드"));
        tagDto2.add(new TagCreateRequest(new PlaceCreateRequest(5L, "버거킹 포항공대점", 35.5, 126.4), "버거킹"));

        courseService.create(user.getId(), planet.getId(), "효자동 맛집 리스트", BODY, Access.PUBLIC, tagDto1, null);
        courseService.create(user.getId(), planet.getId(), "한번쯤 가볼만한 식당 리스트", BODY, Access.PRIVATE, tagDto2, null);

        ResultActions actions = mvc.perform(get("/planets/{planetId}/courses", planet.getId())
                .param("page", "0")
                .param("size", "5")
                .header("accessToken", accessToken)
                .contentType(JSON_CONTENT_TYPE))
                .andExpect(status().isOk());

        actions.andDo(restDocs.document(
                requestHeaders(
                        headerWithName("accessToken")
                                .description("인증 코드")
                ),
                requestParameters(
                        parameterWithName("page")
                                .description("페이지 번호")
                                .attributes(key("type").value("Number"))
                                .optional(),
                        parameterWithName("size")
                                .description("한 페이지 크기")
                                .attributes(key("type").value("Number"))
                                .optional()
                ),
                pathParameters(
                        parameterWithName("planetId")
                                .description("행성 id")
                                .attributes(key("type").value("Number"))
                ),
                responseFields(
                        fieldWithPath("courses[].title")
                                .description("코스 제목"),
                        fieldWithPath("courses[].stars")
                                .description("별자리 이미지에서 별 좌표"),
                        fieldWithPath("courses[].stars[].x")
                                .description("x좌표(경도)"),
                        fieldWithPath("courses[].stars[].y")
                                .description("y좌표(위도)"),
                        fieldWithPath("size")
                                .description("불러온 코스 수"),
                        fieldWithPath("hasNext")
                                .description("다음 페이지 존재 여부")
                )
        ));
    }

    @Test
    @DisplayName("행성 수정")
    void update() throws Exception {
        User user = userRepository.save(new User(EMAIL, PASSWORD, NAME, UserStatus.LOGOUT, Authority.USER));
        String accessToken = userService.login(EMAIL, PASSWORD, DEVICE_TOKEN);
        Planet planet = planetService.create(user.getId(), PLANET_NAME, PlanetImageType.EARTH);

        String content = objectMapper.writeValueAsString(new PlanetUpdateRequest("updated", 365));

        ResultActions actions = mvc.perform(put("/planets/{planetId}", planet.getId())
                .header("accessToken", accessToken)
                .content(content)
                .contentType(JSON_CONTENT_TYPE))
                .andExpect(status().isOk());

        actions.andDo(restDocs.document(
                requestHeaders(
                        headerWithName("accessToken")
                                .description("인증 토큰")
                ),
                pathParameters(
                        parameterWithName("planetId")
                                .description("행성 id")
                                .attributes(key("type").value("Number"))
                ),
                requestFields(
                        fieldWithPath("name")
                                .type(STRING)
                                .description("행성 이름")
                                .attributes(key("constraint").value("2~8자리 한글/영어/숫자")),
                        fieldWithPath("dday")
                                .type(NUMBER)
                                .description("변경할 dday")
                )
        ));
    }

    @Test
    @DisplayName("행성 삭제")
    void remove() throws Exception {
        User user = userRepository.save(new User(EMAIL, PASSWORD, NAME, UserStatus.LOGOUT, Authority.USER));
        String accessToken = userService.login(EMAIL, PASSWORD, DEVICE_TOKEN);
        Planet planet = planetService.create(user.getId(), PLANET_NAME, PlanetImageType.EARTH);

        ResultActions actions = mvc.perform(delete("/planets/{planetId}", planet.getId())
                .header("accessToken", accessToken)
                .contentType(JSON_CONTENT_TYPE))
                .andExpect(status().isOk());

        actions.andDo(restDocs.document(
                requestHeaders(
                        headerWithName("accessToken")
                                .description("인증 토큰")
                ),
                pathParameters(
                        parameterWithName("planetId")
                                .description("행성 id")
                                .attributes(key("type").value("Number"))
                )
        ));
    }

    @Test
    @DisplayName("행성 팔로우")
    void follow() throws Exception {
        User user = userRepository.save(new User("host@gmail", PASSWORD, "host", UserStatus.LOGOUT, Authority.USER));
        Planet planet = planetService.create(user.getId(), PLANET_NAME, PlanetImageType.EARTH);

        userRepository.save(new User(EMAIL, PASSWORD, NAME, UserStatus.LOGOUT, Authority.USER));
        String accessToken = userService.login(EMAIL, PASSWORD, DEVICE_TOKEN);

        ResultActions actions = mvc.perform(post("/planets/{planetId}/follow", planet.getId())
                .header("accessToken", accessToken)
                .contentType(JSON_CONTENT_TYPE))
                .andExpect(status().isOk());

        actions.andDo(restDocs.document(
                requestHeaders(
                        headerWithName("accessToken")
                                .description("인증 토큰")
                ),
                pathParameters(
                        parameterWithName("planetId")
                                .description("행성 id")
                                .attributes(key("type").value("Number"))
                )
        ));
    }

    @Test
    @DisplayName("행성 언팔로우")
    void unfollow() throws Exception {
        User user = userRepository.save(new User("host@gmail", PASSWORD, "host", UserStatus.LOGOUT, Authority.USER));
        Planet planet = planetService.create(user.getId(), PLANET_NAME, PlanetImageType.EARTH);

        User follower = userRepository.save(new User(EMAIL, PASSWORD, NAME, UserStatus.LOGOUT, Authority.USER));
        String accessToken = userService.login(EMAIL, PASSWORD, DEVICE_TOKEN);

        planetService.follow(follower.getId(), planet.getId());

        ResultActions actions = mvc.perform(delete("/planets/{planetId}/follow", planet.getId())
                .header("accessToken", accessToken)
                .contentType(JSON_CONTENT_TYPE))
                .andExpect(status().isOk());

        actions.andDo(restDocs.document(
                requestHeaders(
                        headerWithName("accessToken").description("인증 토큰")
                ),
                pathParameters(
                        parameterWithName("planetId")
                                .description("행성 id")
                                .attributes(key("type").value("Number"))
                )
        ));
    }
}
