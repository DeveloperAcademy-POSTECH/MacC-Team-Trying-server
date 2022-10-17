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
import trying.cosmos.domain.course.Access;
import trying.cosmos.domain.course.CourseService;
import trying.cosmos.domain.course.request.TagCreateRequest;
import trying.cosmos.domain.place.Place;
import trying.cosmos.domain.planet.Planet;
import trying.cosmos.domain.planet.PlanetImageType;
import trying.cosmos.domain.planet.PlanetService;
import trying.cosmos.domain.planet.response.PlanetCreateRequest;
import trying.cosmos.domain.planet.response.PlanetJoinRequest;
import trying.cosmos.domain.user.User;
import trying.cosmos.domain.user.UserRepository;
import trying.cosmos.domain.user.UserService;
import trying.cosmos.domain.user.UserStatus;
import trying.cosmos.global.auth.Authority;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static trying.cosmos.docs.utils.ApiDocumentUtils.getDocumentRequest;
import static trying.cosmos.docs.utils.ApiDocumentUtils.getDocumentResponse;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Transactional
@ActiveProfiles("test")
public class PlanetDocsTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserService userService;
    @Autowired PlanetService planetService;
    @Autowired CourseService courseService;

    private static final String JSON_CONTENT_TYPE = "application/json";

    private static final String EMAIL = "email@gmail.com";
    private static final String PASSWORD = "password";
    private static final String NAME = "name";
    private static final String DEVICE_TOKEN = "device_token";
    private static final String PLANET_NAME = "포딩행성";
    private static final String TITLE = "효자시장 맛집코스";
    private static final String BODY = "굿";

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
                getDocumentRequest(),
                getDocumentResponse(),
                requestHeaders(
                        headerWithName("accessToken").description("인증 토큰")
                ),
                requestFields(
                        fieldWithPath("name").description("행성 이름"),
                        fieldWithPath("image").description("행성 이미지 타입")
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
                getDocumentRequest(),
                getDocumentResponse(),
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
                getDocumentRequest(),
                getDocumentResponse(),
                requestHeaders(
                        headerWithName("accessToken").description("인증 토큰")
                ),
                requestParameters(
                        parameterWithName("code").description("초대 코드")
                ),
                responseFields(
                        fieldWithPath("id").description("행성 id"),
                        fieldWithPath("name").description("행성 이름"),
                        fieldWithPath("image").description("행성 이미지 타입")
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

        actions.andDo(document("planet/join",
                getDocumentRequest(),
                getDocumentResponse(),
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
        userService.login(EMAIL, PASSWORD, DEVICE_TOKEN);

        Planet planet = planetService.create(user.getId(), PLANET_NAME, PlanetImageType.EARTH);

        ResultActions actions = mvc.perform(get("/planets/{id}", planet.getId())
                .contentType(JSON_CONTENT_TYPE))
                .andExpect(status().isOk());

        actions.andDo(document("planet/find",
                getDocumentRequest(),
                getDocumentResponse(),
                pathParameters(
                        parameterWithName("id").description("행성 id")
                ),
                responseFields(
                        fieldWithPath("id").description("행성 id"),
                        fieldWithPath("name").description("행성 이름"),
                        fieldWithPath("image").description("행성 이미지 타입")
                )
        ));
    }

    @Test
    @DisplayName("행성 목록 조회")
    void find_list() throws Exception {
        User user = userRepository.save(new User(EMAIL, PASSWORD, NAME, UserStatus.LOGOUT, Authority.USER));
        userService.login(EMAIL, PASSWORD, DEVICE_TOKEN);

        Planet planet = planetService.create(user.getId(), PLANET_NAME, PlanetImageType.EARTH);

        ResultActions actions = mvc.perform(get("/planets")
                .param("query", "행성")
                .param("page", "0")
                .param("size", "5")
                .contentType(JSON_CONTENT_TYPE))
                .andExpect(status().isOk());

        actions.andDo(document("planet/find-list",
                getDocumentRequest(),
                getDocumentResponse(),
                requestParameters(
                        parameterWithName("query").description("검색하려는 행성 이름").optional(),
                        parameterWithName("page").description("페이지 번호").optional(),
                        parameterWithName("size").description("한 페이지 크기").optional()
                ),
                responseFields(
                        fieldWithPath("planets[].id").description("행성 id"),
                        fieldWithPath("planets[].name").description("행성 이름"),
                        fieldWithPath("planets[].image").description("행성 이미지 타입"),
                        fieldWithPath("size").description("검색결과 불러온 행성 수"),
                        fieldWithPath("hasNext").description("다음 페이지가 있는지")
                )
        ));
    }

    @Test
    @DisplayName("행성 코스 목록 조회")
    void findCourses() throws Exception {
        User user = userRepository.save(new User(EMAIL, PASSWORD, NAME, UserStatus.LOGOUT, Authority.USER));
        String accessToken = userService.login(EMAIL, PASSWORD, DEVICE_TOKEN);

        Planet planet = planetService.create(user.getId(), PLANET_NAME, PlanetImageType.EARTH);

        List<TagCreateRequest> tagDto1 = new ArrayList<>();
        tagDto1.add(new TagCreateRequest(new Place(1L, "참뼈 효자시장점", 36.4, 124.0), "참뼈"));
        tagDto1.add(new TagCreateRequest(new Place(2L, "맥도날드 포항점", 37.0, 125.3), "맥도날드"));
        tagDto1.add(new TagCreateRequest(new Place(3L, "명륜진사갈비", 35.1, 122.1), "명륜진사갈비"));

        List<TagCreateRequest> tagDto2 = new ArrayList<>();
        tagDto2.add(new TagCreateRequest(new Place(4L, "그여든", 36.7, 128.5), "삐갈레 브래드"));
        tagDto2.add(new TagCreateRequest(new Place(5L, "버거킹 포항공대점", 35.5, 126.4), "버거킹"));

        courseService.create(user.getId(), planet.getId(), "효자동 맛집 리스트", BODY, Access.PUBLIC, tagDto1);
        courseService.create(user.getId(), planet.getId(), "한번쯤 가볼만한 식당 리스트", BODY, Access.PRIVATE, tagDto2);

        ResultActions actions = mvc.perform(get("/planets/{id}/courses", planet.getId())
                .param("page", "0")
                .param("size", "5")
                .header("accessToken", accessToken)
                .contentType(JSON_CONTENT_TYPE))
                .andExpect(status().isOk());

        actions.andDo(document("planet/courses",
                getDocumentRequest(),
                getDocumentResponse(),
                requestHeaders(
                        headerWithName("accessToken").description("인증 코드")
                ),
                requestParameters(
                        parameterWithName("page").description("페이지 번호").optional(),
                        parameterWithName("size").description("한 페이지 크기").optional()
                ),
                pathParameters(
                        parameterWithName("id").description("행성 id")
                ),
                responseFields(
                        fieldWithPath("courses[].title").description("코스 제목"),
                        fieldWithPath("size").description("불러온 코스 수"),
                        fieldWithPath("hasNext").description("다음 페이지 존재 여부")
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

        ResultActions actions = mvc.perform(post("/planets/{id}/follow", planet.getId())
                .header("accessToken", accessToken)
                .contentType(JSON_CONTENT_TYPE))
                .andExpect(status().isOk());

        actions.andDo(document("planet/follow",
                getDocumentRequest(),
                getDocumentResponse(),
                requestHeaders(
                        headerWithName("accessToken").description("인증 토큰")
                ),
                pathParameters(
                        parameterWithName("id").description("팔로우할 행성 id")
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

        ResultActions actions = mvc.perform(delete("/planets/{id}/follow", planet.getId())
                .header("accessToken", accessToken)
                .contentType(JSON_CONTENT_TYPE))
                .andExpect(status().isOk());

        actions.andDo(document("planet/unfollow",
                getDocumentRequest(),
                getDocumentResponse(),
                requestHeaders(
                        headerWithName("accessToken").description("인증 토큰")
                ),
                pathParameters(
                        parameterWithName("id").description("언팔로우할 행성 id")
                )
        ));
    }
}
