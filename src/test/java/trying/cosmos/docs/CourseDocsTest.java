package trying.cosmos.docs;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockPart;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;
import trying.cosmos.domain.course.Access;
import trying.cosmos.domain.course.Course;
import trying.cosmos.domain.course.CourseService;
import trying.cosmos.domain.course.request.CourseCreateRequest;
import trying.cosmos.domain.course.request.CourseUpdateRequest;
import trying.cosmos.domain.course.request.TagCreateRequest;
import trying.cosmos.domain.place.PlaceCreateRequest;
import trying.cosmos.domain.planet.Planet;
import trying.cosmos.domain.planet.PlanetImageType;
import trying.cosmos.domain.planet.PlanetService;
import trying.cosmos.domain.user.User;
import trying.cosmos.domain.user.UserRepository;
import trying.cosmos.domain.user.UserService;
import trying.cosmos.domain.user.UserStatus;
import trying.cosmos.global.auth.Authority;

import java.nio.charset.StandardCharsets;
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
public class CourseDocsTest {

    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserService userService;
    @Autowired
    PlanetService planetService;
    @Autowired
    CourseService courseService;

    private static final String JSON_CONTENT_TYPE = "application/json";

    private static final String EMAIL = "email@gmail.com";
    private static final String PASSWORD = "password";
    private static final String NAME = "name";
    private static final String DEVICE_TOKEN = "device_token";
    private static final String PLANET_NAME = "포딩행성";
    private static final String TITLE = "효자시장 맛집코스";
    private static final String BODY = "굿";

    private static final String IMAGE_PATH = "src/test/resources/image.png";

    @Test
    @DisplayName("코스 생성")
    void create() throws Exception {
        User user = userRepository.save(new User(EMAIL, PASSWORD, NAME, UserStatus.LOGOUT, Authority.USER));
        String accessToken = userService.login(EMAIL, PASSWORD, DEVICE_TOKEN);

        Planet planet = planetService.create(user.getId(), PLANET_NAME, PlanetImageType.EARTH);

        List<TagCreateRequest> tagDto = new ArrayList<>();
        tagDto.add(new TagCreateRequest(new PlaceCreateRequest(1L, "참뼈 효자시장점", 36.4, 124.0), "참뼈"));
        tagDto.add(new TagCreateRequest(new PlaceCreateRequest(2L, "맥도날드 포항점", 37.0, 125.3), "맥도날드"));
        tagDto.add(new TagCreateRequest(new PlaceCreateRequest(3L, "명륜진사갈비", 35.1, 122.1), "명륜진사갈비"));

        String json = objectMapper.writeValueAsString(new CourseCreateRequest(planet.getId(), TITLE, BODY, Access.PUBLIC, tagDto));
        MockPart data = new MockPart("data", json.getBytes(StandardCharsets.UTF_8));
        data.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        MockPart image = new MockPart("images", "image", null);
        image.getHeaders().setContentType(MediaType.IMAGE_PNG);

        ResultActions actions = mvc.perform(multipart("/courses")
                .part(data)
                .part(image)
                .header("accessToken", accessToken))
                .andExpect(status().isOk());

        actions.andDo(document("course/create",
                getDocumentRequest(),
                getDocumentResponse(),
                requestHeaders(
                        headerWithName("accessToken").description("인증 토큰")
                ),
                requestPartFields("data",
                        fieldWithPath("planetId").description("코스를 만들 행성 id"),
                        fieldWithPath("title").description("코스 제목"),
                        fieldWithPath("body").description("코스 본문"),
                        fieldWithPath("access").description("코스 공개여부"),
                        fieldWithPath("tags[].place.placeId").description("태그할 장소 번호(지도 API에서 제공하는 ID)"),
                        fieldWithPath("tags[].place.name").description("태그할 장소 이름"),
                        fieldWithPath("tags[].place.latitude").description("태그할 장소 위도"),
                        fieldWithPath("tags[].place.longitude").description("태그할 장소 경도"),
                        fieldWithPath("tags[].name").description("태그 이름")
                ),
                requestParts(
                        partWithName("data").description("코스 생성을 위한 데이터"),
                        partWithName("images").description("업로드할 이미지 목록")
                ),
                responseFields(
                        fieldWithPath("courseId").description("만들어진 코스 id"),
                        fieldWithPath("stars").description("별자리 이미지에서 별 좌표"),
                        fieldWithPath("stars[].x").description("x좌표(경도)"),
                        fieldWithPath("stars[].y").description("y좌표(위도)")
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
        tagDto.add(new TagCreateRequest(new PlaceCreateRequest(1L, "참뼈 효자시장점", 36.4, 124.0), "참뼈"));
        tagDto.add(new TagCreateRequest(new PlaceCreateRequest(2L, "맥도날드 포항점", 37.0, 125.3), "맥도날드"));
        tagDto.add(new TagCreateRequest(new PlaceCreateRequest(3L, "명륜진사갈비", 35.1, 122.1), "명륜진사갈비"));

        Course course = courseService.create(user.getId(), planet.getId(), TITLE, BODY, Access.PUBLIC, tagDto, null);

        ResultActions actions = mvc.perform(get("/courses/{courseId}", course.getId())
                .header("accessToken", accessToken)
                .contentType(JSON_CONTENT_TYPE))
                .andExpect(status().isOk());

        actions.andDo(document("course/find",
                getDocumentRequest(),
                getDocumentResponse(),
                requestHeaders(
                        headerWithName("accessToken").description("인증 토큰")
                ),
                pathParameters(
                        parameterWithName("courseId").description("코스 id")
                ),
                responseFields(
                        fieldWithPath("title").description("코스 제목"),
                        fieldWithPath("body").description("코스 본문"),
                        fieldWithPath("createdDate").description("코스 생성일"),
                        fieldWithPath("liked").description("코스 좋아요 여부"),
                        fieldWithPath("planet.planetId").description("코스가 포함된 행성 id"),
                        fieldWithPath("planet.name").description("코스가 포함된 행성 이름"),
                        fieldWithPath("planet.image").description("코스가 포함된 행성 이미지 타입"),
                        fieldWithPath("planet.dday").description("코스가 포함된 행성 dday"),
                        fieldWithPath("tags[].place.placeNumber").description("태그할 장소 번호(지도 API에서 제공하는 ID)"),
                        fieldWithPath("tags[].place.name").description("태그할 장소 이름"),
                        fieldWithPath("tags[].place.coordinate.latitude").description("태그할 장소 위도"),
                        fieldWithPath("tags[].place.coordinate.longitude").description("태그할 장소 경도"),
                        fieldWithPath("tags[].name").description("태그 이름"),
                        fieldWithPath("images[]").description("이미지 이름")
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
        tagDto1.add(new TagCreateRequest(new PlaceCreateRequest(1L, "참뼈 효자시장점", 36.4, 124.0), "참뼈"));
        tagDto1.add(new TagCreateRequest(new PlaceCreateRequest(2L, "맥도날드 포항점", 37.0, 125.3), "맥도날드"));
        tagDto1.add(new TagCreateRequest(new PlaceCreateRequest(3L, "명륜진사갈비", 35.1, 122.1), "명륜진사갈비"));

        List<TagCreateRequest> tagDto2 = new ArrayList<>();
        tagDto2.add(new TagCreateRequest(new PlaceCreateRequest(4L, "그여든", 36.7, 128.5), "삐갈레 브래드"));
        tagDto2.add(new TagCreateRequest(new PlaceCreateRequest(5L, "버거킹 포항공대점", 35.5, 126.4), "버거킹"));

        courseService.create(user.getId(), planet.getId(), TITLE, BODY, Access.PUBLIC, tagDto1, null);
        courseService.create(user.getId(), planet.getId(), "한번쯤 가볼만한 식당 리스트", BODY, Access.PUBLIC, tagDto2, null);

        ResultActions actions = mvc.perform(get("/courses")
                .header("accessToken", accessToken)
                .param("page", "0")
                .param("size", "5")
                .contentType(JSON_CONTENT_TYPE))
                .andExpect(status().isOk());

        actions.andDo(document("course/find-list",
                getDocumentRequest(),
                getDocumentResponse(),
                requestHeaders(
                        headerWithName("accessToken").description("인증 토큰")
                ),
                requestParameters(
                        parameterWithName("page").description("페이지 번호").optional(),
                        parameterWithName("size").description("한 페이지 크기").optional()
                ),
                responseFields(
                        fieldWithPath("courses[].courseId").description("코스 id"),
                        fieldWithPath("courses[].planet.planetId").description("코스가 포함된 행성 id"),
                        fieldWithPath("courses[].planet.name").description("코스가 포함된 행성 이름"),
                        fieldWithPath("courses[].planet.image").description("코스가 포함된 행성 이미지 타입"),
                        fieldWithPath("courses[].planet.dday").description("코스가 포함된 행성 dday"),
                        fieldWithPath("courses[].title").description("코스 제목"),
                        fieldWithPath("courses[].createdDate").description("코스 생성일"),
                        fieldWithPath("courses[].liked").description("코스 좋아요 여부"),
                        fieldWithPath("courses[].images[]").description("이미지 이름"),
                        fieldWithPath("size").description("불러온 코스 수"),
                        fieldWithPath("hasNext").description("다음 페이지 존재 여부")
                )
        ));
    }

    @Test
    @DisplayName("코스 수정")
    void update() throws Exception {
        User user = userRepository.save(new User(EMAIL, PASSWORD, NAME, UserStatus.LOGOUT, Authority.USER));
        String accessToken = userService.login(EMAIL, PASSWORD, DEVICE_TOKEN);

        Planet planet = planetService.create(user.getId(), PLANET_NAME, PlanetImageType.EARTH);

        List<TagCreateRequest> tagDto = new ArrayList<>();
        tagDto.add(new TagCreateRequest(new PlaceCreateRequest(1L, "참뼈 효자시장점", 36.4, 124.0), "참뼈"));
        tagDto.add(new TagCreateRequest(new PlaceCreateRequest(2L, "맥도날드 포항점", 37.0, 125.3), "맥도날드"));
        tagDto.add(new TagCreateRequest(new PlaceCreateRequest(3L, "명륜진사갈비", 35.1, 122.1), "명륜진사갈비"));

        Course course = courseService.create(user.getId(), planet.getId(), "updated", "updated", Access.PUBLIC, tagDto, null);

        String json = objectMapper.writeValueAsString(new CourseUpdateRequest(TITLE, BODY, Access.PUBLIC, tagDto));
        MockPart data = new MockPart("data", json.getBytes(StandardCharsets.UTF_8));
        data.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        MockPart image = new MockPart("images", "image", null);
        image.getHeaders().setContentType(MediaType.IMAGE_PNG);

        MockMultipartHttpServletRequestBuilder builder = RestDocumentationRequestBuilders.multipart("/courses/{courseId}", course.getId());
        builder.with(request -> {
            request.setMethod("PUT");
            return request;
        });

        ResultActions actions = mvc.perform(builder
                .part(data)
                .part(image)
                .header("accessToken", accessToken)
        ).andExpect(status().isOk());

        actions.andDo(document("course/update",
                getDocumentRequest(),
                getDocumentResponse(),
                requestHeaders(
                        headerWithName("accessToken").description("인증 토큰")
                ),
                pathParameters(
                        parameterWithName("courseId").description("코스 id")
                ),
                requestPartFields("data",
                        fieldWithPath("title").description("코스 제목"),
                        fieldWithPath("body").description("코스 본문"),
                        fieldWithPath("access").description("코스 공개여부"),
                        fieldWithPath("tags[].place.placeId").description("태그할 장소 번호(지도 API에서 제공하는 ID)"),
                        fieldWithPath("tags[].place.name").description("태그할 장소 이름"),
                        fieldWithPath("tags[].place.latitude").description("태그할 장소 위도"),
                        fieldWithPath("tags[].place.longitude").description("태그할 장소 경도"),
                        fieldWithPath("tags[].name").description("태그 이름")
                ),
                requestParts(
                        partWithName("data").description("코스 생성을 위한 데이터"),
                        partWithName("images").description("업로드할 이미지 목록")
                ),
                responseFields(
                        fieldWithPath("courseId").description("만들어진 코스 id"),
                        fieldWithPath("stars").description("별자리 이미지에서 별 좌표"),
                        fieldWithPath("stars[].x").description("x좌표(경도)"),
                        fieldWithPath("stars[].y").description("y좌표(위도)")
                )
        ));
    }

    @Test
    @DisplayName("코스 삭제")
    void remove() throws Exception {
        User user = userRepository.save(new User(EMAIL, PASSWORD, NAME, UserStatus.LOGOUT, Authority.USER));
        String accessToken = userService.login(EMAIL, PASSWORD, DEVICE_TOKEN);

        Planet planet = planetService.create(user.getId(), PLANET_NAME, PlanetImageType.EARTH);

        List<TagCreateRequest> tagDto = new ArrayList<>();
        tagDto.add(new TagCreateRequest(new PlaceCreateRequest(1L, "참뼈 효자시장점", 36.4, 124.0), "참뼈"));
        tagDto.add(new TagCreateRequest(new PlaceCreateRequest(2L, "맥도날드 포항점", 37.0, 125.3), "맥도날드"));
        tagDto.add(new TagCreateRequest(new PlaceCreateRequest(3L, "명륜진사갈비", 35.1, 122.1), "명륜진사갈비"));

        Course course = courseService.create(user.getId(), planet.getId(), "updated", "updated", Access.PUBLIC, tagDto, null);

        ResultActions actions = mvc.perform(delete("/courses/{courseId}", course.getId())
                .header("accessToken", accessToken)
                .contentType(JSON_CONTENT_TYPE)
        ).andExpect(status().isOk());

        actions.andDo(document("course/delete",
                getDocumentRequest(),
                getDocumentResponse(),
                requestHeaders(
                        headerWithName("accessToken").description("인증 토큰")
                ),
                pathParameters(
                        parameterWithName("courseId").description("코스 id")
                ),
                pathParameters(
                        parameterWithName("courseId").description("코스 id")
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
        tagDto.add(new TagCreateRequest(new PlaceCreateRequest(1L, "참뼈 효자시장점", 36.4, 124.0), "참뼈"));
        tagDto.add(new TagCreateRequest(new PlaceCreateRequest(2L, "맥도날드 포항점", 37.0, 125.3), "맥도날드"));
        tagDto.add(new TagCreateRequest(new PlaceCreateRequest(3L, "명륜진사갈비", 35.1, 122.1), "명륜진사갈비"));

        Course course = courseService.create(user.getId(), planet.getId(), TITLE, BODY, Access.PUBLIC, tagDto, null);

        ResultActions actions = mvc.perform(post("/courses/{courseId}/like", course.getId())
                .header("accessToken", accessToken)
                .contentType(JSON_CONTENT_TYPE))
                .andExpect(status().isOk());

        actions.andDo(document("course/like",
                getDocumentRequest(),
                getDocumentResponse(),
                requestHeaders(
                        headerWithName("accessToken").description("인증 토큰")
                ),
                pathParameters(
                        parameterWithName("courseId").description("코스 id")
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
        tagDto.add(new TagCreateRequest(new PlaceCreateRequest(1L, "참뼈 효자시장점", 36.4, 124.0), "참뼈"));
        tagDto.add(new TagCreateRequest(new PlaceCreateRequest(2L, "맥도날드 포항점", 37.0, 125.3), "맥도날드"));
        tagDto.add(new TagCreateRequest(new PlaceCreateRequest(3L, "명륜진사갈비", 35.1, 122.1), "명륜진사갈비"));

        Course course = courseService.create(user.getId(), planet.getId(), TITLE, BODY, Access.PUBLIC, tagDto, null);

        courseService.like(user.getId(), course.getId());

        ResultActions actions = mvc.perform(delete("/courses/{courseId}/like", course.getId())
                .header("accessToken", accessToken)
                .contentType(JSON_CONTENT_TYPE))
                .andExpect(status().isOk());

        actions.andDo(document("course/unlike",
                getDocumentRequest(),
                getDocumentResponse(),
                requestHeaders(
                        headerWithName("accessToken").description("인증 토큰")
                ),
                pathParameters(
                        parameterWithName("courseId").description("코스 id")
                )
        ));
    }
}
