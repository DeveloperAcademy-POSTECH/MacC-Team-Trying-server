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
import trying.cosmos.domain.course.dto.request.CourseCreateRequest;
import trying.cosmos.domain.course.dto.request.CoursePlaceRequest;
import trying.cosmos.domain.course.dto.request.CourseUpdateRequest;
import trying.cosmos.domain.course.entity.Course;
import trying.cosmos.domain.course.entity.CourseLike;
import trying.cosmos.domain.course.entity.CoursePlace;
import trying.cosmos.domain.course.repository.CourseRepository;
import trying.cosmos.domain.place.dto.request.PlaceCreateRequest;
import trying.cosmos.domain.place.entity.Place;
import trying.cosmos.domain.place.service.PlaceService;
import trying.cosmos.domain.planet.entity.Planet;
import trying.cosmos.domain.planet.repository.PlanetRepository;
import trying.cosmos.domain.review.entity.Review;
import trying.cosmos.domain.user.entity.User;
import trying.cosmos.domain.user.repository.UserRepository;
import trying.cosmos.domain.user.service.UserService;
import trying.cosmos.global.auth.SessionService;
import trying.cosmos.global.utils.DateUtils;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static trying.cosmos.test.TestVariables.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@DisplayName("[DOCS] 코스")
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
@ExtendWith({RestDocumentationExtension.class})
public class CourseTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    @Autowired
    PlanetRepository planetRepository;

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    EntityManager em;

    @Autowired
    PlaceService placeService;

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
    @DisplayName("코스 생성")
    void create() throws Exception {
        // GIVEN
        User user = userRepository.save(User.createEmailUser(EMAIL1, PASSWORD, NAME1, DEVICE_TOKEN));
        User mate = userRepository.save(User.createEmailUser(EMAIL2, PASSWORD, NAME2, DEVICE_TOKEN));
        Planet planet = planetRepository.save(new Planet(user, NAME1, IMAGE, INVITE_CODE));
        planet.join(mate);
        String accessToken = userService.login(EMAIL1, PASSWORD, DEVICE_TOKEN);

        List<CoursePlaceRequest> requests = List.of(
                new CoursePlaceRequest(new PlaceCreateRequest(1L, "효자동국밥", "음식점", "주소", 0.1, 0.3), MEMO),
                new CoursePlaceRequest(new PlaceCreateRequest(2L, "효자동쌀국수", "음식점", "주소", 2.1, 1.3), MEMO)
        );
        String content = objectMapper.writeValueAsString(new CourseCreateRequest(NAME1, DateUtils.getFormattedDate(LocalDate.now()), requests));

        // WHEN
        ResultActions actions = mvc.perform(post("/courses")
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
                        fieldWithPath("title")
                                .type(STRING)
                                .description("코스 이름"),
                        fieldWithPath("date")
                                .type(STRING)
                                .description("코스 날짜"),
                        fieldWithPath("places[].place.identifier")
                                .type(NUMBER)
                                .description("장소 API에서 제공받은 id"),
                        fieldWithPath("places[].place.name")
                                .type(STRING)
                                .description("장소 이름"),
                        fieldWithPath("places[].place.category")
                                .type(STRING)
                                .description("장소 카테고리"),
                        fieldWithPath("places[].place.address")
                                .type(STRING)
                                .description("장소 주소"),
                        fieldWithPath("places[].place.latitude")
                                .type(NUMBER)
                                .description("장소 위도"),
                        fieldWithPath("places[].place.longitude")
                                .type(NUMBER)
                                .description("장소 경도"),
                        fieldWithPath("places[].memo")
                                .type(STRING)
                                .description("장소 계획에 남길 수 있는 메모")
                                .optional()
                ),
                responseFields(
                        fieldWithPath("courseId")
                                .type(NUMBER)
                                .description("생성된 코스 id")
                )
        ));
    }

    @Test
    @DisplayName("코스 수정")
    void update() throws Exception {
        // GIVEN
        User user = userRepository.save(User.createEmailUser(EMAIL1, PASSWORD, NAME1, DEVICE_TOKEN));
        User mate = userRepository.save(User.createEmailUser(EMAIL2, PASSWORD, NAME2, DEVICE_TOKEN));
        Planet planet = planetRepository.save(new Planet(user, NAME1, IMAGE, INVITE_CODE));
        planet.join(mate);
        String accessToken = userService.login(EMAIL1, PASSWORD, DEVICE_TOKEN);

        Course course = courseRepository.save(new Course(planet, NAME1, LocalDate.now()));
        Place place1 = placeService.create(PLACE_IDENTIFIER1, NAME1, CATEGORY1, ADDRESS, 0.0, 0.1);
        Place place2 = placeService.create(PLACE_IDENTIFIER2, NAME2, CATEGORY2, ADDRESS, 0.2, 0.3);

        em.persist(new CoursePlace(course, place1, MEMO));
        em.persist(new CoursePlace(course, place2, MEMO));

        List<CoursePlaceRequest> requests = List.of(
                new CoursePlaceRequest(new PlaceCreateRequest(1L, "효자동국밥", "음식점", "주소", 0.1, 0.3), MEMO),
                new CoursePlaceRequest(new PlaceCreateRequest(2L, "효자동쌀국수", "음식점", "주소", 2.1, 1.3), MEMO)
        );
        String content = objectMapper.writeValueAsString(new CourseUpdateRequest("UPDATED", DateUtils.getFormattedDate(LocalDate.now().plusDays(3)), requests));

        // WHEN
        ResultActions actions = mvc.perform(put("/courses/{courseId}", course.getId())
                .header(ACCESS_TOKEN, accessToken)
                .content(content)
                .contentType(JSON_CONTENT_TYPE));

        // THEN
        actions.andExpect(status().isOk());

        // DOCS
        actions.andDo(restDocs.document(
                pathParameters(
                        parameterWithName("courseId")
                                .description("수정하려는 코스 id")
                                .attributes(key("type").value("Number"))
                ),
                requestHeaders(
                        headerWithName(ACCESS_TOKEN)
                                .description("인증 토큰")
                ),
                requestFields(
                        fieldWithPath("title")
                                .type(STRING)
                                .description("코스 이름"),
                        fieldWithPath("date")
                                .type(STRING)
                                .description("코스 날짜"),
                        fieldWithPath("places[].place.identifier")
                                .type(NUMBER)
                                .description("장소 API에서 제공받은 id"),
                        fieldWithPath("places[].place.name")
                                .type(STRING)
                                .description("장소 이름"),
                        fieldWithPath("places[].place.category")
                                .type(STRING)
                                .description("장소 카테고리"),
                        fieldWithPath("places[].place.address")
                                .type(STRING)
                                .description("장소 주소"),
                        fieldWithPath("places[].place.latitude")
                                .type(NUMBER)
                                .description("장소 위도"),
                        fieldWithPath("places[].place.longitude")
                                .type(NUMBER)
                                .description("장소 경도"),
                        fieldWithPath("places[].memo")
                                .type(STRING)
                                .description("장소 계획에 남길 수 있는 메모")
                                .optional()
                ),
                responseFields(
                        fieldWithPath("courseId")
                                .type(NUMBER)
                                .description("수정된 코스 id")
                )
        ));
    }

    @Test
    @DisplayName("코스 삭제")
    void remove() throws Exception {
        // GIVEN
        User user = userRepository.save(User.createEmailUser(EMAIL1, PASSWORD, NAME1, DEVICE_TOKEN));
        User mate = userRepository.save(User.createEmailUser(EMAIL2, PASSWORD, NAME2, DEVICE_TOKEN));
        Planet planet = planetRepository.save(new Planet(user, NAME1, IMAGE, INVITE_CODE));
        planet.join(mate);
        String accessToken = userService.login(EMAIL1, PASSWORD, DEVICE_TOKEN);

        Course course = courseRepository.save(new Course(planet, NAME1, LocalDate.now()));
        Place place1 = placeService.create(PLACE_IDENTIFIER1, NAME1, CATEGORY1, ADDRESS, 0.0, 0.1);
        Place place2 = placeService.create(PLACE_IDENTIFIER2, NAME2, CATEGORY2, ADDRESS, 0.2, 0.3);

        em.persist(new CoursePlace(course, place1, MEMO));
        em.persist(new CoursePlace(course, place2, MEMO));

        // WHEN
        ResultActions actions = mvc.perform(delete("/courses/{courseId}", course.getId())
                .header(ACCESS_TOKEN, accessToken)
                .contentType(JSON_CONTENT_TYPE));

        // THEN
        actions.andExpect(status().isOk());

        // DOCS
        actions.andDo(restDocs.document(
                pathParameters(
                        parameterWithName("courseId")
                                .description("수정하려는 코스 id")
                                .attributes(key("type").value("Number"))
                ),
                requestHeaders(
                        headerWithName(ACCESS_TOKEN)
                                .description("인증 토큰")
                )
        ));
    }

    @Test
    @DisplayName("아이디로 코스 조회")
    void findById() throws Exception {
        // GIVEN
        User user = userRepository.save(User.createEmailUser(EMAIL1, PASSWORD, NAME1, DEVICE_TOKEN));
        User mate = userRepository.save(User.createEmailUser(EMAIL2, PASSWORD, NAME2, DEVICE_TOKEN));
        Planet planet = planetRepository.save(new Planet(user, NAME1, IMAGE, INVITE_CODE));
        planet.join(mate);
        String accessToken = userService.login(EMAIL1, PASSWORD, DEVICE_TOKEN);

        Course course = courseRepository.save(new Course(planet, NAME1, LocalDate.now()));
        Place place1 = placeService.create(PLACE_IDENTIFIER1, NAME1, CATEGORY1, ADDRESS, 0.0, 0.1);
        Place place2 = placeService.create(PLACE_IDENTIFIER2, NAME2, CATEGORY2, ADDRESS, 0.2, 0.3);

        em.persist(new CoursePlace(course, place1, MEMO));
        em.persist(new CoursePlace(course, place2, MEMO));

        // WHEN
        ResultActions actions = mvc.perform(get("/courses/{courseId}", course.getId())
                .header(ACCESS_TOKEN, accessToken)
                .contentType(JSON_CONTENT_TYPE));

        // THEN
        actions.andExpect(status().isOk());

        // DOCS
        actions.andDo(restDocs.document(
                pathParameters(
                        parameterWithName("courseId")
                                .description("코스 id")
                                .attributes(key("type").value("Number"))
                ),
                requestHeaders(
                        headerWithName(ACCESS_TOKEN)
                                .description("인증 토큰")
                ),
                responseFields(
                        fieldWithPath("courseId")
                                .type(NUMBER)
                                .description("코스 id"),
                        fieldWithPath("title")
                                .type(STRING)
                                .description("코스 제목"),
                        fieldWithPath("date")
                                .type(STRING)
                                .description("코스 날짜"),
                        fieldWithPath("liked")
                                .type(BOOLEAN)
                                .description("코스 좋아요 여부"),
                        fieldWithPath("places[].memo")
                                .type(STRING)
                                .description("장소 메모"),
                        fieldWithPath("places[].place.placeId")
                                .type(NUMBER)
                                .description("장소 id"),
                        fieldWithPath("places[].place.identifier")
                                .type(NUMBER)
                                .description("장소 API에서 제공받은 id"),
                        fieldWithPath("places[].place.name")
                                .type(STRING)
                                .description("장소 이름"),
                        fieldWithPath("places[].place.category")
                                .type(STRING)
                                .description("장소 카테고리"),
                        fieldWithPath("places[].place.address")
                                .type(STRING)
                                .description("장소 주소"),
                        fieldWithPath("places[].place.coordinate.latitude")
                                .type(NUMBER)
                                .description("장소 위도"),
                        fieldWithPath("places[].place.coordinate.longitude")
                                .type(NUMBER)
                                .description("장소 경도"),
                        fieldWithPath("places[].distanceFromNext")
                                .type(NUMBER)
                                .description("다음 코스까지의 거리(KM), 마지막 코스에는 없음")
                                .optional()
                )
        ));
    }

    @Test
    @DisplayName("날짜로 코스 조회")
    void findByDate() throws Exception {
        // GIVEN
        User user = userRepository.save(User.createEmailUser(EMAIL1, PASSWORD, NAME1, DEVICE_TOKEN));
        User mate = userRepository.save(User.createEmailUser(EMAIL2, PASSWORD, NAME2, DEVICE_TOKEN));
        Planet planet = planetRepository.save(new Planet(user, NAME1, IMAGE, INVITE_CODE));
        planet.join(mate);
        String accessToken = userService.login(EMAIL1, PASSWORD, DEVICE_TOKEN);

        Course course = courseRepository.save(new Course(planet, NAME1, LocalDate.now()));
        Place place1 = placeService.create(PLACE_IDENTIFIER1, NAME1, CATEGORY1, ADDRESS, 0.0, 0.1);
        Place place2 = placeService.create(PLACE_IDENTIFIER2, NAME2, CATEGORY2, ADDRESS, 0.2, 0.3);

        em.persist(new CoursePlace(course, place1, MEMO));
        em.persist(new CoursePlace(course, place2, MEMO));

        // WHEN
        ResultActions actions = mvc.perform(get("/courses/dates/{date}", course.getDate())
                .header(ACCESS_TOKEN, accessToken)
                .contentType(JSON_CONTENT_TYPE));

        // THEN
        actions.andExpect(status().isOk());

        // DOCS
        actions.andDo(restDocs.document(
                pathParameters(
                        parameterWithName("date")
                                .description("찾으려는 날짜")
                                .attributes(key("type").value("String"))
                ),
                requestHeaders(
                        headerWithName(ACCESS_TOKEN)
                                .description("인증 토큰")
                ),
                responseFields(
                        fieldWithPath("courseId")
                                .type(NUMBER)
                                .description("코스 id"),
                        fieldWithPath("title")
                                .type(STRING)
                                .description("코스 제목"),
                        fieldWithPath("date")
                                .type(STRING)
                                .description("코스 날짜"),
                        fieldWithPath("liked")
                                .type(BOOLEAN)
                                .description("코스 좋아요 여부"),
                        fieldWithPath("places[].memo")
                                .type(STRING)
                                .description("장소 메모"),
                        fieldWithPath("places[].place.placeId")
                                .type(NUMBER)
                                .description("장소 id"),
                        fieldWithPath("places[].place.identifier")
                                .type(NUMBER)
                                .description("장소 API에서 제공받은 id"),
                        fieldWithPath("places[].place.name")
                                .type(STRING)
                                .description("장소 이름"),
                        fieldWithPath("places[].place.category")
                                .type(STRING)
                                .description("장소 카테고리"),
                        fieldWithPath("places[].place.address")
                                .type(STRING)
                                .description("장소 주소"),
                        fieldWithPath("places[].place.coordinate.latitude")
                                .type(NUMBER)
                                .description("장소 위도"),
                        fieldWithPath("places[].place.coordinate.longitude")
                                .type(NUMBER)
                                .description("장소 경도"),
                        fieldWithPath("places[].distanceFromNext")
                                .type(NUMBER)
                                .description("다음 코스까지의 거리(KM), 마지막 코스에는 없음")
                                .optional()
                )
        ));
    }

    @Test
    @DisplayName("코스 목록 조회")
    void findList() throws Exception {
        // GIVEN
        User user = userRepository.save(User.createEmailUser(EMAIL1, PASSWORD, NAME1, DEVICE_TOKEN));
        User mate = userRepository.save(User.createEmailUser(EMAIL2, PASSWORD, NAME2, DEVICE_TOKEN));
        Planet planet = planetRepository.save(new Planet(user, NAME1, IMAGE, INVITE_CODE));
        planet.join(mate);
        String accessToken = userService.login(EMAIL1, PASSWORD, DEVICE_TOKEN);

        Course course = courseRepository.save(new Course(planet, NAME1, LocalDate.now()));
        Place place1 = placeService.create(PLACE_IDENTIFIER1, NAME1, CATEGORY1, ADDRESS, 0.0, 0.1);
        Place place2 = placeService.create(PLACE_IDENTIFIER2, NAME2, CATEGORY2, ADDRESS, 0.2, 0.3);

        em.persist(new CoursePlace(course, place1, MEMO));
        em.persist(new CoursePlace(course, place2, MEMO));

        // WHEN
        ResultActions actions = mvc.perform(get("/courses")
                .header(ACCESS_TOKEN, accessToken)
                .param("query", "")
                .param("likeonly", "false")
                .param("page", "0")
                .param("size", "10")
                .contentType(JSON_CONTENT_TYPE));

        // THEN
        actions.andExpect(status().isOk());

        // DOCS
        actions.andDo(restDocs.document(
                requestParameters(
                        parameterWithName("query")
                                .description("찾으려는 코스 제목")
                                .attributes(key("type").value("String"))
                                .optional(),
                        parameterWithName("likeonly")
                                .description("좋아요한 코스만 검색")
                                .attributes(key("type").value("Boolean"))
                                .optional(),
                        parameterWithName("page")
                                .description("페이지 번호")
                                .attributes(key("type").value("Number"))
                                .optional(),
                        parameterWithName("size")
                                .description("페이지 크기")
                                .attributes(key("type").value("Number"))
                                .optional()
                ),
                requestHeaders(
                        headerWithName(ACCESS_TOKEN)
                                .description("인증 토큰")
                ),
                responseFields(
                        fieldWithPath("courses[].courseId")
                                .type(NUMBER)
                                .description("코스 id"),
                        fieldWithPath("courses[].title")
                                .type(STRING)
                                .description("코스 제목"),
                        fieldWithPath("courses[].date")
                                .type(STRING)
                                .description("코스 날짜"),
                        fieldWithPath("courses[].liked")
                                .type(BOOLEAN)
                                .description("코스 좋아요 여부"),
                        fieldWithPath("size")
                                .type(NUMBER)
                                .description("불러온 데이터 크기"),
                        fieldWithPath("hasNext")
                                .type(BOOLEAN)
                                .description("다음 페이지 존재 여부"),
                        fieldWithPath("courses[].places[].memo")
                                .type(STRING)
                                .description("장소 메모"),
                        fieldWithPath("courses[].places[].place.placeId")
                                .type(NUMBER)
                                .description("장소 id"),
                        fieldWithPath("courses[].places[].place.identifier")
                                .type(NUMBER)
                                .description("장소 API에서 제공받은 id"),
                        fieldWithPath("courses[].places[].place.name")
                                .type(STRING)
                                .description("장소 이름"),
                        fieldWithPath("courses[].places[].place.category")
                                .type(STRING)
                                .description("장소 카테고리"),
                        fieldWithPath("courses[].places[].place.address")
                                .type(STRING)
                                .description("장소 주소"),
                        fieldWithPath("courses[].places[].place.coordinate.latitude")
                                .type(NUMBER)
                                .description("장소 위도"),
                        fieldWithPath("courses[].places[].place.coordinate.longitude")
                                .type(NUMBER)
                                .description("장소 경도"),
                        fieldWithPath("courses[].places[].distanceFromNext")
                                .type(NUMBER)
                                .description("다음 코스까지의 거리(KM), 마지막 코스에는 없음")
                                .optional()
                )
        ));
    }

    @Test
    @DisplayName("로그 조회")
    void log() throws Exception {
        // GIVEN
        User user = userRepository.save(User.createEmailUser(EMAIL1, PASSWORD, NAME1, DEVICE_TOKEN));
        User mate = userRepository.save(User.createEmailUser(EMAIL2, PASSWORD, NAME2, DEVICE_TOKEN));
        Planet planet = planetRepository.save(new Planet(user, NAME1, IMAGE, INVITE_CODE));
        planet.join(mate);
        String accessToken = userService.login(EMAIL1, PASSWORD, DEVICE_TOKEN);

        Course course = courseRepository.save(new Course(planet, NAME1, LocalDate.now()));
        Place place1 = placeService.create(PLACE_IDENTIFIER1, NAME1, CATEGORY1, ADDRESS, 0.0, 0.1);
        Place place2 = placeService.create(PLACE_IDENTIFIER2, NAME2, CATEGORY2, ADDRESS, 0.2, 0.3);

        em.persist(new CoursePlace(course, place1, MEMO));
        em.persist(new CoursePlace(course, place2, MEMO));
        em.persist(new Review(user, course, BODY));

        // WHEN
        ResultActions actions = mvc.perform(get("/courses/log")
                .header(ACCESS_TOKEN, accessToken)
                .param("page", "0")
                .param("size", "10")
                .contentType(JSON_CONTENT_TYPE));

        // THEN
        actions.andExpect(status().isOk());

        // DOCS
        actions.andDo(restDocs.document(
                requestParameters(
                        parameterWithName("page")
                                .description("페이지 번호")
                                .attributes(key("type").value("Number"))
                                .optional(),
                        parameterWithName("size")
                                .description("페이지 크기")
                                .attributes(key("type").value("Number"))
                                .optional()
                ),
                requestHeaders(
                        headerWithName(ACCESS_TOKEN)
                                .description("인증 토큰")
                ),
                responseFields(
                        fieldWithPath("courses[].courseId")
                                .type(NUMBER)
                                .description("코스 id"),
                        fieldWithPath("courses[].title")
                                .type(STRING)
                                .description("코스 제목"),
                        fieldWithPath("courses[].date")
                                .type(STRING)
                                .description("코스 날짜"),
                        fieldWithPath("courses[].liked")
                                .type(BOOLEAN)
                                .description("코스 좋아요 여부"),
                        fieldWithPath("size")
                                .type(NUMBER)
                                .description("불러온 데이터 크기"),
                        fieldWithPath("hasNext")
                                .type(BOOLEAN)
                                .description("다음 페이지 존재 여부"),
                        fieldWithPath("courses[].places[].memo")
                                .type(STRING)
                                .description("장소 메모"),
                        fieldWithPath("courses[].places[].place.placeId")
                                .type(NUMBER)
                                .description("장소 id"),
                        fieldWithPath("courses[].places[].place.identifier")
                                .type(NUMBER)
                                .description("장소 API에서 제공받은 id"),
                        fieldWithPath("courses[].places[].place.name")
                                .type(STRING)
                                .description("장소 이름"),
                        fieldWithPath("courses[].places[].place.category")
                                .type(STRING)
                                .description("장소 카테고리"),
                        fieldWithPath("courses[].places[].place.address")
                                .type(STRING)
                                .description("장소 주소"),
                        fieldWithPath("courses[].places[].place.coordinate.latitude")
                                .type(NUMBER)
                                .description("장소 위도"),
                        fieldWithPath("courses[].places[].place.coordinate.longitude")
                                .type(NUMBER)
                                .description("장소 경도"),
                        fieldWithPath("courses[].places[].distanceFromNext")
                                .type(NUMBER)
                                .description("다음 코스까지의 거리(KM), 마지막 코스에는 없음")
                                .optional()
                )
        ));
    }

    @Test
    @DisplayName("코스가 존재하는 날짜 조회")
    void courseExistDate() throws Exception {
        // GIVEN
        User user = userRepository.save(User.createEmailUser(EMAIL1, PASSWORD, NAME1, DEVICE_TOKEN));
        User mate = userRepository.save(User.createEmailUser(EMAIL2, PASSWORD, NAME2, DEVICE_TOKEN));
        Planet planet = planetRepository.save(new Planet(user, NAME1, IMAGE, INVITE_CODE));
        planet.join(mate);
        String accessToken = userService.login(EMAIL1, PASSWORD, DEVICE_TOKEN);

        Course course = courseRepository.save(new Course(planet, NAME1, LocalDate.now()));
        Place place1 = placeService.create(PLACE_IDENTIFIER1, NAME1, CATEGORY1, ADDRESS, 0.0, 0.1);
        Place place2 = placeService.create(PLACE_IDENTIFIER2, NAME2, CATEGORY2, ADDRESS, 0.2, 0.3);

        em.persist(new CoursePlace(course, place1, MEMO));
        em.persist(new CoursePlace(course, place2, MEMO));
        em.persist(new Review(user, course, BODY));

        // WHEN
        ResultActions actions = mvc.perform(get("/courses/dates")
                .header(ACCESS_TOKEN, accessToken)
                .param("start", DateUtils.getFormattedDate(LocalDate.now()))
                .param("end", DateUtils.getFormattedDate(LocalDate.now().plusDays(1)))
                .contentType(JSON_CONTENT_TYPE));

        // THEN
        actions.andExpect(status().isOk());

        // DOCS
        actions.andDo(restDocs.document(
                requestParameters(
                        parameterWithName("start")
                                .description("검색 시작 날짜")
                                .attributes(key("type").value("String")),
                        parameterWithName("end")
                                .description("검색 끝 날짜(해당 날짜 제외)")
                                .attributes(key("type").value("String"))
                ),
                requestHeaders(
                        headerWithName(ACCESS_TOKEN)
                                .description("인증 토큰")
                ),
                responseFields(
                        fieldWithPath("dates[]")
                                .type(ARRAY)
                                .description("코스가 존재하는 날짜")
                )
        ));
    }

    @Test
    @DisplayName("코스 좋아요")
    void like() throws Exception {
        // GIVEN
        User user = userRepository.save(User.createEmailUser(EMAIL1, PASSWORD, NAME1, DEVICE_TOKEN));
        User mate = userRepository.save(User.createEmailUser(EMAIL2, PASSWORD, NAME2, DEVICE_TOKEN));
        Planet planet = planetRepository.save(new Planet(user, NAME1, IMAGE, INVITE_CODE));
        planet.join(mate);
        String accessToken = userService.login(EMAIL1, PASSWORD, DEVICE_TOKEN);

        Course course = courseRepository.save(new Course(planet, NAME1, LocalDate.now()));
        Place place1 = placeService.create(PLACE_IDENTIFIER1, NAME1, CATEGORY1, ADDRESS, 0.0, 0.1);
        Place place2 = placeService.create(PLACE_IDENTIFIER2, NAME2, CATEGORY2, ADDRESS, 0.2, 0.3);

        em.persist(new CoursePlace(course, place1, MEMO));
        em.persist(new CoursePlace(course, place2, MEMO));

        // WHEN
        ResultActions actions = mvc.perform(post("/courses/{courseId}/like", course.getId())
                .header(ACCESS_TOKEN, accessToken)
                .contentType(JSON_CONTENT_TYPE));

        // THEN
        actions.andExpect(status().isOk());

        // DOCS
        actions.andDo(restDocs.document(
                pathParameters(
                        parameterWithName("courseId")
                                .attributes(key("type").value("Number"))
                                .description("코스 id")
                ),
                requestHeaders(
                        headerWithName(ACCESS_TOKEN)
                                .description("인증 토큰")
                )
        ));
    }

    @Test
    @DisplayName("코스 좋아요 취소")
    void unlike() throws Exception {
        // GIVEN
        User user = userRepository.save(User.createEmailUser(EMAIL1, PASSWORD, NAME1, DEVICE_TOKEN));
        User mate = userRepository.save(User.createEmailUser(EMAIL2, PASSWORD, NAME2, DEVICE_TOKEN));
        Planet planet = planetRepository.save(new Planet(user, NAME1, IMAGE, INVITE_CODE));
        planet.join(mate);
        String accessToken = userService.login(EMAIL1, PASSWORD, DEVICE_TOKEN);

        Course course = courseRepository.save(new Course(planet, NAME1, LocalDate.now()));
        Place place1 = placeService.create(PLACE_IDENTIFIER1, NAME1, CATEGORY1, ADDRESS, 0.0, 0.1);
        Place place2 = placeService.create(PLACE_IDENTIFIER2, NAME2, CATEGORY2, ADDRESS, 0.2, 0.3);

        em.persist(new CoursePlace(course, place1, MEMO));
        em.persist(new CoursePlace(course, place2, MEMO));
        em.persist(new CourseLike(user, course));

        // WHEN
        ResultActions actions = mvc.perform(delete("/courses/{courseId}/like", course.getId())
                .header(ACCESS_TOKEN, accessToken)
                .contentType(JSON_CONTENT_TYPE));

        // THEN
        actions.andExpect(status().isOk());

        // DOCS
        actions.andDo(restDocs.document(
                pathParameters(
                        parameterWithName("courseId")
                                .attributes(key("type").value("Number"))
                                .description("코스 id")
                ),
                requestHeaders(
                        headerWithName(ACCESS_TOKEN)
                                .description("인증 토큰")
                )
        ));
    }
}
