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
import trying.cosmos.domain.place.entity.Place;
import trying.cosmos.domain.place.service.PlaceService;
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
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static trying.cosmos.docs.utils.DocsVariable.*;
import static trying.cosmos.test.TestVariables.NAME1;

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
    @DisplayName("아이디로 장소 조회")
    void findById() throws Exception {
        // GIVEN
        User user = userRepository.save(User.createEmailUser(MY_EMAIL, PASSWORD, MY_NAME, DEVICE_TOKEN));
        User mate = userRepository.save(User.createEmailUser(MATE_EMAIL, PASSWORD, MATE_NAME, DEVICE_TOKEN));
        Planet planet = planetRepository.save(new Planet(user, PLANET_NAME, IMAGE_NAME, INVITE_CODE));
        planet.join(mate);
        String accessToken = userService.login(MY_EMAIL, PASSWORD, DEVICE_TOKEN);
        Place place1 = placeService.create(PLACE_IDENTIFIER1, NAME1, CATEGORY1, 0.0, 0.1);
        placeService.create(place1.getIdentifier(), place1.getName(), place1.getCategory(), place1.getLongitude(), place1.getLatitude());

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
                        fieldWithPath("identifier")
                                .type(NUMBER)
                                .description("장소 API에서 제공받은 id"),
                        fieldWithPath("name")
                                .type(STRING)
                                .description("장소 이름"),
                        fieldWithPath("category")
                                .type(STRING)
                                .description("장소 카테고리"),
                        fieldWithPath("coordinate.latitude")
                                .type(NUMBER)
                                .description("장소 위도"),
                        fieldWithPath("coordinate.longitude")
                                .type(NUMBER)
                                .description("장소 경도")
                )
        ));
    }
}
