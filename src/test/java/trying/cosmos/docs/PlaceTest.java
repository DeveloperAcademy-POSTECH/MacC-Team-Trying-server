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
import trying.cosmos.domain.course.repository.CourseRepository;
import trying.cosmos.domain.planet.entity.Planet;
import trying.cosmos.domain.planet.repository.PlanetRepository;
import trying.cosmos.domain.user.entity.User;
import trying.cosmos.domain.user.repository.UserRepository;
import trying.cosmos.domain.user.service.UserService;
import trying.cosmos.global.auth.SessionService;

import javax.persistence.EntityManager;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static trying.cosmos.docs.utils.DocsVariable.*;
import static trying.cosmos.test.TestVariables.place1;
import static trying.cosmos.test.TestVariables.place2;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@DisplayName("[DOCS] 코스 리뷰")
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
@ExtendWith({RestDocumentationExtension.class})
public class PlaceTest {

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

        em.persist(place1);
        em.persist(place2);
    }

    @AfterEach
    void clearSession() {
        sessionService.clear();
    }

    @Test
    @DisplayName("아이디로 장소 조회")
    void findById() throws Exception {
        // GIVEN
        User user = userRepository.save(User.createEmailUser(MY_EMAIL, PASSWORD, MY_NAME, DEVICE_TOKEN, true));
        User mate = userRepository.save(User.createEmailUser(MATE_EMAIL, PASSWORD, MATE_NAME, DEVICE_TOKEN, true));
        Planet planet = planetRepository.save(new Planet(user, PLANET_NAME, IMAGE_NAME, INVITE_CODE));
        planet.join(mate);
        String accessToken = userService.login(MY_EMAIL, PASSWORD, DEVICE_TOKEN);

        // WHEN
        ResultActions actions = mvc.perform(get("/places/{placeId}", place1.getId())
                .header(ACCESS_TOKEN, accessToken)
                .contentType(JSON_CONTENT_TYPE));

        // THEN
        actions.andExpect(status().isOk());

        // DOCS
        actions.andDo(restDocs.document(
                requestHeaders(
                        headerWithName(ACCESS_TOKEN)
                                .description("인증 토큰")
                ),
                pathParameters(
                        parameterWithName("placeId")
                                .attributes(key("type").value("Number"))
                                .description("장소 id")
                ),
                responseFields(
                        fieldWithPath("placeId")
                                .type(NUMBER)
                                .description("장소 id"),
                        fieldWithPath("name")
                                .type(STRING)
                                .description("장소 이름"),
                        fieldWithPath("address")
                                .type(STRING)
                                .description("장소 주소"),
                        fieldWithPath("roadAddress")
                                .type(STRING)
                                .description("장소 도로명 주소"),
                        fieldWithPath("coordinate.latitude")
                                .type(NUMBER)
                                .description("장소 위도"),
                        fieldWithPath("coordinate.longitude")
                                .type(NUMBER)
                                .description("장소 경도")
                )
        ));
    }

    @Test
    @DisplayName("이름으로 장소 조회")
    void findByName() throws Exception {
        // GIVEN
        User user = userRepository.save(User.createEmailUser(MY_EMAIL, PASSWORD, MY_NAME, DEVICE_TOKEN, true));
        User mate = userRepository.save(User.createEmailUser(MATE_EMAIL, PASSWORD, MATE_NAME, DEVICE_TOKEN, true));
        Planet planet = planetRepository.save(new Planet(user, PLANET_NAME, IMAGE_NAME, INVITE_CODE));
        planet.join(mate);
        String accessToken = userService.login(MY_EMAIL, PASSWORD, DEVICE_TOKEN);

        // WHEN
        ResultActions actions = mvc.perform(get("/places")
                .header(ACCESS_TOKEN, accessToken)
                .param("name", "name")
                .param("latitude", "0.0")
                .param("longitude", "0.0")
                .param("page", "0")
                .param("size", "10")
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
                        parameterWithName("name")
                                .attributes(key("type").value("String"))
                                .description("장소 이름"),
                        parameterWithName("latitude")
                                .attributes(key("type").value("Number"))
                                .description("장소 위도"),
                        parameterWithName("longitude")
                                .attributes(key("type").value("Number"))
                                .description("장소 경도"),
                        parameterWithName("page")
                                .attributes(key("type").value("Number"))
                                .description("페이지 번호")
                                .optional(),
                        parameterWithName("size")
                                .attributes(key("type").value("Number"))
                                .description("페이지 크기")
                                .optional()
                ),
                responseFields(
                        fieldWithPath("contents[].place.placeId")
                                .type(NUMBER)
                                .description("장소 id"),
                        fieldWithPath("contents[].place.name")
                                .type(STRING)
                                .description("장소 이름"),
                        fieldWithPath("contents[].place.coordinate.latitude")
                                .type(NUMBER)
                                .description("장소 위도"),
                        fieldWithPath("contents[].place.coordinate.longitude")
                                .type(NUMBER)
                                .description("장소 경도"),
                        fieldWithPath("contents[].distance")
                                .type(NUMBER)
                                .description("현재 위치로부터 장소까지의 거리(km)"),
                        fieldWithPath("size")
                                .type(NUMBER)
                                .description("불러온 장소 수"),
                        fieldWithPath("hasNext")
                                .type(BOOLEAN)
                                .description("다음 페이지 존재 여부")
                )
        ));
    }

    @Test
    @DisplayName("위치로 장소 조회")
    void findByLocation() throws Exception {
        // GIVEN
        User user = userRepository.save(User.createEmailUser(MY_EMAIL, PASSWORD, MY_NAME, DEVICE_TOKEN, true));
        User mate = userRepository.save(User.createEmailUser(MATE_EMAIL, PASSWORD, MATE_NAME, DEVICE_TOKEN, true));
        Planet planet = planetRepository.save(new Planet(user, PLANET_NAME, IMAGE_NAME, INVITE_CODE));
        planet.join(mate);
        String accessToken = userService.login(MY_EMAIL, PASSWORD, DEVICE_TOKEN);

        // WHEN
        ResultActions actions = mvc.perform(get("/places/position")
                .header(ACCESS_TOKEN, accessToken)
                .param("distance", "2")
                .param("latitude", "0.0")
                .param("longitude", "0.0")
                .param("page", "0")
                .param("size", "10")
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
                        parameterWithName("distance")
                                .attributes(key("type").value("Number"))
                                .description("범위(km)"),
                        parameterWithName("latitude")
                                .attributes(key("type").value("Number"))
                                .description("장소 위도"),
                        parameterWithName("longitude")
                                .attributes(key("type").value("Number"))
                                .description("장소 경도"),
                        parameterWithName("page")
                                .attributes(key("type").value("Number"))
                                .description("페이지 번호")
                                .optional(),
                        parameterWithName("size")
                                .attributes(key("type").value("Number"))
                                .description("페이지 크기")
                                .optional()
                ),
                responseFields(
                        fieldWithPath("contents[].place.placeId")
                                .type(NUMBER)
                                .description("장소 id"),
                        fieldWithPath("contents[].place.name")
                                .type(STRING)
                                .description("장소 이름"),
                        fieldWithPath("contents[].place.coordinate.latitude")
                                .type(NUMBER)
                                .description("장소 위도"),
                        fieldWithPath("contents[].place.coordinate.longitude")
                                .type(NUMBER)
                                .description("장소 경도"),
                        fieldWithPath("contents[].distance")
                                .type(NUMBER)
                                .description("현재 위치로부터 장소까지의 거리(km)"),
                        fieldWithPath("size")
                                .type(NUMBER)
                                .description("불러온 장소 수"),
                        fieldWithPath("hasNext")
                                .type(BOOLEAN)
                                .description("다음 페이지 존재 여부")
                )
        ));
    }
}
