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
import trying.cosmos.controller.request.course.CourseCreateRequest;
import trying.cosmos.controller.request.course.TagCreateRequest;
import trying.cosmos.docs.utils.ApiDocumentUtils;
import trying.cosmos.entity.Course;
import trying.cosmos.entity.Place;
import trying.cosmos.entity.Planet;
import trying.cosmos.entity.User;
import trying.cosmos.entity.component.Access;
import trying.cosmos.entity.component.Authority;
import trying.cosmos.entity.component.PlanetImageType;
import trying.cosmos.entity.component.UserStatus;
import trying.cosmos.repository.UserRepository;
import trying.cosmos.service.CourseService;
import trying.cosmos.service.PlanetService;
import trying.cosmos.service.UserService;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Transactional
@ActiveProfiles("test")
public class CourseDocsTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired UserRepository userRepository;
    @Autowired UserService userService;
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
    @DisplayName("코스 생성")
    void create() throws Exception {
        User user = userRepository.save(new User(EMAIL, PASSWORD, NAME, UserStatus.LOGOUT, Authority.USER));
        String accessToken = userService.login(EMAIL, PASSWORD, DEVICE_TOKEN);

        Planet planet = planetService.create(user.getId(), PLANET_NAME, PlanetImageType.EARTH);

        List<TagCreateRequest> tagDto = new ArrayList<>();
        tagDto.add(new TagCreateRequest(new Place(1L, "참뼈 효자시장점", 36.4, 124.0), "참뼈"));
        tagDto.add(new TagCreateRequest(new Place(2L, "맥도날드 포항점", 37.0, 125.3), "맥도날드"));
        tagDto.add(new TagCreateRequest(new Place(3L, "명륜진사갈비", 35.1, 122.1), "명륜진사갈비"));;

        String content = objectMapper.writeValueAsString(new CourseCreateRequest(planet.getId(), TITLE, BODY, Access.PUBLIC, tagDto));

        ResultActions actions = mvc.perform(post("/courses")
                .header("accessToken", accessToken)
                .contentType(JSON_CONTENT_TYPE)
                .content(content))
                .andExpect(status().isOk());

        actions.andDo(document("course/create",
                ApiDocumentUtils.getDocumentRequest(),
                ApiDocumentUtils.getDocumentResponse(),
                requestHeaders(
                        headerWithName("accessToken").description("인증 토큰")
                ),
                requestFields(
                        fieldWithPath("planetId").description("코스를 만들 행성 id"),
                        fieldWithPath("title").description("코스 제목"),
                        fieldWithPath("body").description("코스 본문"),
                        fieldWithPath("access").description("코스 공개여부"),
                        fieldWithPath("tags[].place.id").description("태그할 장소 id"),
                        fieldWithPath("tags[].place.name").description("태그할 장소 이름"),
                        fieldWithPath("tags[].place.latitude").description("태그할 장소 위도"),
                        fieldWithPath("tags[].place.longitude").description("태그할 장소 경도"),
                        fieldWithPath("tags[].name").description("태그 이름")
                ),
                responseFields(
                        fieldWithPath("id").description("만들어진 코스 id")
                )
        ));
    }

    @Test
    @DisplayName("코스 조회")
    void find() throws Exception {
        User user = userRepository.save(new User(EMAIL, PASSWORD, NAME, UserStatus.LOGOUT, Authority.USER));
        String accessToken = userService.login(EMAIL, PASSWORD, DEVICE_TOKEN);

        Planet planet = planetService.create(user.getId(), PLANET_NAME, PlanetImageType.EARTH);

        List<TagCreateRequest> tagDto = new ArrayList<>();
        tagDto.add(new TagCreateRequest(new Place(1L, "참뼈 효자시장점", 36.4, 124.0), "참뼈"));
        tagDto.add(new TagCreateRequest(new Place(2L, "맥도날드 포항점", 37.0, 125.3), "맥도날드"));
        tagDto.add(new TagCreateRequest(new Place(3L, "명륜진사갈비", 35.1, 122.1), "명륜진사갈비"));

        Course course = courseService.create(user.getId(), planet.getId(), TITLE, BODY, Access.PUBLIC, tagDto);

        ResultActions actions = mvc.perform(get("/courses/{id}", course.getId())
                .header("accessToken", accessToken)
                .contentType(JSON_CONTENT_TYPE))
                .andExpect(status().isOk());

        actions.andDo(document("course/find",
                ApiDocumentUtils.getDocumentRequest(),
                ApiDocumentUtils.getDocumentResponse(),
                requestHeaders(
                        headerWithName("accessToken").description("인증 토큰")
                ),
                pathParameters(
                        parameterWithName("id").description("코스 id")
                ),
                responseFields(
                        fieldWithPath("title").description("코스 제목"),
                        fieldWithPath("body").description("코스 본문"),
                        fieldWithPath("createdDate").description("코스 생성일"),
                        fieldWithPath("liked").description("코스 좋아요 여부"),
                        fieldWithPath("planet.id").description("코스가 포함된 행성 id"),
                        fieldWithPath("planet.name").description("코스가 포함된 행성 이름"),
                        fieldWithPath("planet.image").description("코스가 포함된 행성 이미지 타입"),
                        fieldWithPath("tags[].place.id").description("태그할 장소 id"),
                        fieldWithPath("tags[].place.name").description("태그할 장소 이름"),
                        fieldWithPath("tags[].place.latitude").description("태그할 장소 위도"),
                        fieldWithPath("tags[].place.longitude").description("태그할 장소 경도"),
                        fieldWithPath("tags[].name").description("태그 이름")
                )
        ));
    }

    @Test
    @DisplayName("코스 목록 조회")
    void find_list() throws Exception {
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

        courseService.create(user.getId(), planet.getId(), TITLE, BODY, Access.PUBLIC, tagDto1);
        courseService.create(user.getId(), planet.getId(), "한번쯤 가볼만한 식당 리스트", BODY, Access.PUBLIC, tagDto2);

        ResultActions actions = mvc.perform(get("/courses")
                .header("accessToken", accessToken)
                .param("page", "0")
                .param("size", "5")
                .contentType(JSON_CONTENT_TYPE))
                .andExpect(status().isOk());

        actions.andDo(document("course/find-list",
                ApiDocumentUtils.getDocumentRequest(),
                ApiDocumentUtils.getDocumentResponse(),
                requestHeaders(
                        headerWithName("accessToken").description("인증 토큰")
                ),
                requestParameters(
                        parameterWithName("page").description("페이지 번호").optional(),
                        parameterWithName("size").description("한 페이지 크기").optional()
                ),
                responseFields(
                        fieldWithPath("courses[].id").description("코스 id"),
                        fieldWithPath("courses[].planet.id").description("코스가 포함된 행성 id"),
                        fieldWithPath("courses[].planet.name").description("코스가 포함된 행성 이름"),
                        fieldWithPath("courses[].planet.image").description("코스가 포함된 행성 이미지 타입"),
                        fieldWithPath("courses[].title").description("코스 제목"),
                        fieldWithPath("courses[].createdDate").description("코스 생성일"),
                        fieldWithPath("size").description("불러온 코스 수"),
                        fieldWithPath("hasNext").description("다음 페이지 존재 여부")
                )
        ));
    }

    @Test
    @DisplayName("코스 좋아요")
    void like() throws Exception {
        User user = userRepository.save(new User(EMAIL, PASSWORD, NAME, UserStatus.LOGOUT, Authority.USER));
        String accessToken = userService.login(EMAIL, PASSWORD, DEVICE_TOKEN);

        Planet planet = planetService.create(user.getId(), PLANET_NAME, PlanetImageType.EARTH);

        List<TagCreateRequest> tagDto = new ArrayList<>();
        tagDto.add(new TagCreateRequest(new Place(1L, "참뼈 효자시장점", 36.4, 124.0), "참뼈"));
        tagDto.add(new TagCreateRequest(new Place(2L, "맥도날드 포항점", 37.0, 125.3), "맥도날드"));
        tagDto.add(new TagCreateRequest(new Place(3L, "명륜진사갈비", 35.1, 122.1), "명륜진사갈비"));

        Course course = courseService.create(user.getId(), planet.getId(), TITLE, BODY, Access.PUBLIC, tagDto);

        ResultActions actions = mvc.perform(post("/courses/{id}/like", course.getId())
                .header("accessToken", accessToken)
                .contentType(JSON_CONTENT_TYPE))
                .andExpect(status().isOk());

        actions.andDo(document("course/like",
                ApiDocumentUtils.getDocumentRequest(),
                ApiDocumentUtils.getDocumentResponse(),
                requestHeaders(
                        headerWithName("accessToken").description("인증 토큰")
                ),
                pathParameters(
                        parameterWithName("id").description("코스 id")
                )
        ));
    }

    @Test
    @DisplayName("코스 좋아요 취소")
    void unlike() throws Exception {
        User user = userRepository.save(new User(EMAIL, PASSWORD, NAME, UserStatus.LOGOUT, Authority.USER));
        String accessToken = userService.login(EMAIL, PASSWORD, DEVICE_TOKEN);

        Planet planet = planetService.create(user.getId(), PLANET_NAME, PlanetImageType.EARTH);

        List<TagCreateRequest> tagDto = new ArrayList<>();
        tagDto.add(new TagCreateRequest(new Place(1L, "참뼈 효자시장점", 36.4, 124.0), "참뼈"));
        tagDto.add(new TagCreateRequest(new Place(2L, "맥도날드 포항점", 37.0, 125.3), "맥도날드"));
        tagDto.add(new TagCreateRequest(new Place(3L, "명륜진사갈비", 35.1, 122.1), "명륜진사갈비"));

        Course course = courseService.create(user.getId(), planet.getId(), TITLE, BODY, Access.PUBLIC, tagDto);

        courseService.like(user.getId(), course.getId());

        ResultActions actions = mvc.perform(delete("/courses/{id}/like", course.getId())
                .header("accessToken", accessToken)
                .contentType(JSON_CONTENT_TYPE))
                .andExpect(status().isOk());

        actions.andDo(document("course/unlike",
                ApiDocumentUtils.getDocumentRequest(),
                ApiDocumentUtils.getDocumentResponse(),
                requestHeaders(
                        headerWithName("accessToken").description("인증 토큰")
                ),
                pathParameters(
                        parameterWithName("id").description("코스 id")
                )
        ));
    }
}
