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
import org.springframework.mock.web.MockPart;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import trying.cosmos.docs.utils.RestDocsConfiguration;
import trying.cosmos.domain.course.entity.Course;
import trying.cosmos.domain.course.entity.CoursePlace;
import trying.cosmos.domain.course.repository.CourseRepository;
import trying.cosmos.domain.course.service.CourseService;
import trying.cosmos.domain.place.entity.Place;
import trying.cosmos.domain.place.service.PlaceService;
import trying.cosmos.domain.planet.entity.Planet;
import trying.cosmos.domain.planet.repository.PlanetRepository;
import trying.cosmos.domain.review.entity.Review;
import trying.cosmos.domain.review.repository.ReviewRepository;
import trying.cosmos.domain.user.entity.User;
import trying.cosmos.domain.user.repository.UserRepository;
import trying.cosmos.domain.user.service.UserService;
import trying.cosmos.global.auth.SessionService;

import javax.persistence.EntityManager;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static trying.cosmos.docs.utils.DocsVariable.*;
import static trying.cosmos.test.TestVariables.NAME1;
import static trying.cosmos.test.TestVariables.NAME2;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@DisplayName("[DOCS] 코스 리뷰")
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
@ExtendWith({RestDocumentationExtension.class})
public class ReviewTest {

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
    ReviewRepository reviewRepository;

    @Autowired
    PlaceService placeService;

    @Autowired
    CourseService courseService;

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
    @DisplayName("코스 리뷰 생성")
    void create() throws Exception {
        // GIVEN
        User user = userRepository.save(User.createEmailUser(MY_EMAIL, PASSWORD, MY_NAME, DEVICE_TOKEN));
        User mate = userRepository.save(User.createEmailUser(MATE_EMAIL, PASSWORD, MATE_NAME, DEVICE_TOKEN));
        Planet planet = planetRepository.save(new Planet(user, PLANET_NAME, IMAGE_NAME, INVITE_CODE));
        planet.join(mate);
        String accessToken = userService.login(MY_EMAIL, PASSWORD, DEVICE_TOKEN);

        Course course = courseRepository.save(new Course(planet, COURSE_NAME, LocalDate.now()));
        Place place1 = placeService.create(PLACE_IDENTIFIER1, NAME1, CATEGORY1, 0.0, 0.1);
        Place place2 = placeService.create(PLACE_IDENTIFIER2, NAME2, CATEGORY2, 0.2, 0.3);

        placeService.create(place1.getIdentifier(), place1.getName(), place1.getCategory(), place1.getLongitude(), place1.getLatitude());
        placeService.create(place2.getIdentifier(), place2.getName(), place2.getCategory(), place2.getLongitude(), place2.getLatitude());
        em.persist(new CoursePlace(course, place1, MEMO));
        em.persist(new CoursePlace(course, place2, MEMO));

        MockPart contentPart = new MockPart("content", CONTENT.getBytes(StandardCharsets.UTF_8));
        MockPart courseIdPart = new MockPart("courseId", String.valueOf(course.getId()).getBytes(StandardCharsets.UTF_8));
        MockPart imagePart = new MockPart("images", null);

        // WHEN
        ResultActions actions = mvc.perform(multipart("/reviews")
                .part(courseIdPart)
                .part(contentPart)
                .part(imagePart)
                .header(ACCESS_TOKEN, accessToken));

        // THEN
        actions.andExpect(status().isOk());

        // DOCS
        actions.andDo(restDocs.document(
                requestHeaders(
                        headerWithName(ACCESS_TOKEN)
                                .description("인증 토큰")
                ),
                requestParts(
                        partWithName("courseId")
                                .description("코스 id"),
                        partWithName("content")
                                .description("코스 리뷰 내용"),
                        partWithName("images")
                                .description("리뷰 이미지")
                ),
                responseFields(
                        fieldWithPath("reviewId")
                                .type(NUMBER)
                                .description("코스 리뷰 id")
                )
        ));
    }

    @Test
    @DisplayName("코스 리뷰 수정")
    void update() throws Exception {
        // GIVEN
        User user = userRepository.save(User.createEmailUser(MY_EMAIL, PASSWORD, MY_NAME, DEVICE_TOKEN));
        User mate = userRepository.save(User.createEmailUser(MATE_EMAIL, PASSWORD, MATE_NAME, DEVICE_TOKEN));
        Planet planet = planetRepository.save(new Planet(user, PLANET_NAME, IMAGE_NAME, INVITE_CODE));
        planet.join(mate);
        String accessToken = userService.login(MY_EMAIL, PASSWORD, DEVICE_TOKEN);

        Course course = courseRepository.save(new Course(planet, COURSE_NAME, LocalDate.now()));
        Place place1 = placeService.create(PLACE_IDENTIFIER1, NAME1, CATEGORY1, 0.0, 0.1);
        Place place2 = placeService.create(PLACE_IDENTIFIER2, NAME2, CATEGORY2, 0.2, 0.3);

        em.persist(new CoursePlace(course, place1, MEMO));
        em.persist(new CoursePlace(course, place2, MEMO));

        Review review = reviewRepository.save(new Review(user, course, CONTENT));

        MockPart contentPart = new MockPart("content", "UPDATED".getBytes(StandardCharsets.UTF_8));
        MockPart imagePart = new MockPart("images", null);

        // WHEN
        MockMultipartHttpServletRequestBuilder builder = RestDocumentationRequestBuilders.multipart("/reviews/{reviewId}", review.getId());
        builder.with(request -> {
            request.setMethod("PUT");
            return request;
        });

        ResultActions actions = mvc.perform(builder
                .part(contentPart)
                .part(imagePart)
                .header(ACCESS_TOKEN, accessToken));

        // THEN
        actions.andExpect(status().isOk());

        // DOCS
        actions.andDo(restDocs.document(
                pathParameters(
                        parameterWithName("reviewId")
                                .attributes(key("type").value("Number"))
                                .description("코스 리뷰 id")
                ),
                requestHeaders(
                        headerWithName(ACCESS_TOKEN)
                                .description("인증 토큰")
                ),
                requestParts(
                        partWithName("content")
                                .description("코스 리뷰 내용"),
                        partWithName("images")
                                .description("리뷰 이미지")
                )
        ));
    }

    @Test
    @DisplayName("코스 리뷰 삭제")
    void remove() throws Exception {
        // GIVEN
        User user = userRepository.save(User.createEmailUser(MY_EMAIL, PASSWORD, MY_NAME, DEVICE_TOKEN));
        User mate = userRepository.save(User.createEmailUser(MATE_EMAIL, PASSWORD, MATE_NAME, DEVICE_TOKEN));
        Planet planet = planetRepository.save(new Planet(user, PLANET_NAME, IMAGE_NAME, INVITE_CODE));
        planet.join(mate);
        String accessToken = userService.login(MY_EMAIL, PASSWORD, DEVICE_TOKEN);

        Course course = courseRepository.save(new Course(planet, COURSE_NAME, LocalDate.now()));
        Place place1 = placeService.create(PLACE_IDENTIFIER1, NAME1, CATEGORY1, 0.0, 0.1);
        Place place2 = placeService.create(PLACE_IDENTIFIER2, NAME2, CATEGORY2, 0.2, 0.3);

        em.persist(new CoursePlace(course, place1, MEMO));
        em.persist(new CoursePlace(course, place2, MEMO));

        Review review = reviewRepository.save(new Review(user, course, CONTENT));

        // WHEN
        ResultActions actions = mvc.perform(delete("/reviews/{reviewId}", review.getId())
                .header(ACCESS_TOKEN, accessToken));

        // THEN
        actions.andExpect(status().isOk());

        // DOCS
        actions.andDo(restDocs.document(
                pathParameters(
                        parameterWithName("reviewId")
                                .attributes(key("type").value("Number"))
                                .description("코스 리뷰 id")
                ),
                requestHeaders(
                        headerWithName(ACCESS_TOKEN)
                                .description("인증 토큰")
                )
        ));
    }

    @Test
    @DisplayName("코스로 리뷰 조회")
    void findByCourse() throws Exception {
        // GIVEN
        User user = userRepository.save(User.createEmailUser(MY_EMAIL, PASSWORD, MY_NAME, DEVICE_TOKEN));
        User mate = userRepository.save(User.createEmailUser(MATE_EMAIL, PASSWORD, MATE_NAME, DEVICE_TOKEN));
        Planet planet = planetRepository.save(new Planet(user, PLANET_NAME, IMAGE_NAME, INVITE_CODE));
        planet.join(mate);
        String accessToken = userService.login(MY_EMAIL, PASSWORD, DEVICE_TOKEN);

        Course course = courseRepository.save(new Course(planet, COURSE_NAME, LocalDate.now()));
        Place place1 = placeService.create(PLACE_IDENTIFIER1, NAME1, CATEGORY1, 0.0, 0.1);
        Place place2 = placeService.create(PLACE_IDENTIFIER2, NAME2, CATEGORY2, 0.2, 0.3);

        placeService.create(place1.getIdentifier(), place1.getName(), place1.getCategory(), place1.getLongitude(), place1.getLatitude());
        placeService.create(place2.getIdentifier(), place2.getName(), place2.getCategory(), place2.getLongitude(), place2.getLatitude());

        em.persist(new CoursePlace(course, place1, MEMO));
        em.persist(new CoursePlace(course, place2, MEMO));

        em.persist(new Review(user, course, CONTENT));
        em.persist(new Review(mate, course, CONTENT));

        // WHEN
        ResultActions actions = mvc.perform(get("/courses/{courseId}/review", course.getId())
                .header(ACCESS_TOKEN, accessToken));

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
                ),
                responseFields(
                        fieldWithPath("myReview.writerName")
                                .type(STRING)
                                .description("리뷰 작성자 닉네임"),
                        fieldWithPath("myReview.content")
                                .type(STRING)
                                .description("리뷰 내용"),
                        fieldWithPath("myReview.images[]")
                                .type(ARRAY)
                                .description("리뷰 이미지 경로"),
                        fieldWithPath("mateReview.writerName")
                                .type(STRING)
                                .description("리뷰 작성자 닉네임"),
                        fieldWithPath("mateReview.content")
                                .type(STRING)
                                .description("리뷰 내용"),
                        fieldWithPath("mateReview.images[]")
                                .type(ARRAY)
                                .description("리뷰 이미지 경로")
                )
        ));
    }

    @Test
    @DisplayName("아이디로 코스 리뷰 조회")
    void findById() throws Exception {
        // GIVEN
        User user = userRepository.save(User.createEmailUser(MY_EMAIL, PASSWORD, MY_NAME, DEVICE_TOKEN));
        User mate = userRepository.save(User.createEmailUser(MATE_EMAIL, PASSWORD, MATE_NAME, DEVICE_TOKEN));
        Planet planet = planetRepository.save(new Planet(user, PLANET_NAME, IMAGE_NAME, INVITE_CODE));
        planet.join(mate);
        String accessToken = userService.login(MY_EMAIL, PASSWORD, DEVICE_TOKEN);

        Course course = courseRepository.save(new Course(planet, COURSE_NAME, LocalDate.now()));
        Place place1 = placeService.create(PLACE_IDENTIFIER1, NAME1, CATEGORY1, 0.0, 0.1);
        Place place2 = placeService.create(PLACE_IDENTIFIER2, NAME2, CATEGORY2, 0.2, 0.3);

        em.persist(new CoursePlace(course, place1, MEMO));

        Review review = reviewRepository.save(new Review(user, course, CONTENT));

        // WHEN
        ResultActions actions = mvc.perform(get("/reviews/{reviewId}", review.getId())
                .header(ACCESS_TOKEN, accessToken));

        // THEN
        actions.andExpect(status().isOk());

        // DOCS
        actions.andDo(restDocs.document(
                pathParameters(
                        parameterWithName("reviewId")
                                .attributes(key("type").value("Number"))
                                .description("리뷰 id")
                ),
                requestHeaders(
                        headerWithName(ACCESS_TOKEN)
                                .description("인증 토큰")
                ),
                responseFields(
                        fieldWithPath("writerName")
                                .type(STRING)
                                .description("리뷰 작성자 닉네임"),
                        fieldWithPath("content")
                                .type(STRING)
                                .description("리뷰 내용"),
                        fieldWithPath("images[]")
                                .type(ARRAY)
                                .description("리뷰 이미지 경로")
                )
        ));
    }
}
