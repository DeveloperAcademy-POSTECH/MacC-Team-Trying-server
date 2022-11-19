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
import trying.cosmos.domain.notification.entity.Notification;
import trying.cosmos.domain.notification.entity.NotificationTarget;
import trying.cosmos.domain.notification.repository.NotificationRepository;
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
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static trying.cosmos.docs.utils.RestDocsConfiguration.constraints;
import static trying.cosmos.test.TestVariables.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@DisplayName("[DOCS] 알림")
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
@ExtendWith({RestDocumentationExtension.class})
public class NotificationTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    @Autowired
    PlanetRepository planetRepository;

    @Autowired
    NotificationRepository notificationRepository;

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
    @DisplayName("알림 조회")
    void find() throws Exception {
        // GIVEN
        User user = userRepository.save(User.createEmailUser(EMAIL1, PASSWORD, NAME1, DEVICE_TOKEN));
        String accessToken = userService.login(EMAIL1, PASSWORD, DEVICE_TOKEN);
        User mate = userRepository.save(User.createEmailUser(EMAIL2, PASSWORD, NAME2, DEVICE_TOKEN));
        Planet planet = planetRepository.save(new Planet(user, NAME1, IMAGE, INVITE_CODE));
        planet.join(mate);
        Notification notification = notificationRepository.save(new Notification(user, TITLE, BODY, NotificationTarget.COURSE, 1L));

        // WHEN
        ResultActions actions = mvc.perform(get("/notifications")
                .header(ACCESS_TOKEN, accessToken));

        actions.andExpect(status().isOk());

        // THEN
        actions.andDo(restDocs.document(
                requestHeaders(
                        headerWithName(ACCESS_TOKEN)
                                .description("인증 토큰")
                ),
                responseFields(
                        fieldWithPath("notifications[].notificationId")
                                .type(NUMBER)
                                .description("알림 id"),
                        fieldWithPath("notifications[].title")
                                .type(STRING)
                                .description("알림 제목"),
                        fieldWithPath("notifications[].body")
                                .type(STRING)
                                .description("알림 본문"),
                        fieldWithPath("notifications[].target")
                                .type(STRING)
                                .description("알림을 눌렀을 때 연결시켜야 할 데이터 타입")
                                .attributes(constraints("PLANET, COURSE, REVIEW 중 하나")),
                        fieldWithPath("notifications[].targetId")
                                .type(NUMBER)
                                .description("알림을 눌렀을 때 연결시켜야 할 데이터 타입"),
                        fieldWithPath("notifications[].createdDate")
                                .type(STRING)
                                .description("알림 생성일"),
                        fieldWithPath("notifications[].checked")
                                .type(BOOLEAN)
                                .description("알림 확인 여부")

                )
        ));
    }

    @Test
    @DisplayName("알림 읽음 표시")
    void mark() throws Exception {
        // GIVEN
        User user = userRepository.save(User.createEmailUser(EMAIL1, PASSWORD, NAME1, DEVICE_TOKEN));
        String accessToken = userService.login(EMAIL1, PASSWORD, DEVICE_TOKEN);
        User mate = userRepository.save(User.createEmailUser(EMAIL2, PASSWORD, NAME2, DEVICE_TOKEN));
        Planet planet = planetRepository.save(new Planet(user, NAME1, IMAGE, INVITE_CODE));
        planet.join(mate);
        Notification notification = notificationRepository.save(new Notification(user, TITLE, BODY, NotificationTarget.COURSE, 1L));

        // WHEN
        ResultActions actions = mvc.perform(post("/notifications/{notificationId}", notification.getId())
                .header(ACCESS_TOKEN, accessToken));

        actions.andExpect(status().isOk());

        // THEN
        actions.andDo(restDocs.document(
                requestHeaders(
                        headerWithName(ACCESS_TOKEN)
                                .description("인증 토큰")
                ),
                pathParameters(
                        parameterWithName("notificationId")
                                .description("알림 id")
                                .attributes(key("type").value("Number"))
                )
        ));
    }

    @Test
    @DisplayName("알림 삭제")
    void remove() throws Exception {
        // GIVEN
        User user = userRepository.save(User.createEmailUser(EMAIL1, PASSWORD, NAME1, DEVICE_TOKEN));
        String accessToken = userService.login(EMAIL1, PASSWORD, DEVICE_TOKEN);
        User mate = userRepository.save(User.createEmailUser(EMAIL2, PASSWORD, NAME2, DEVICE_TOKEN));
        Planet planet = planetRepository.save(new Planet(user, NAME1, IMAGE, INVITE_CODE));
        planet.join(mate);
        Notification notification = notificationRepository.save(new Notification(user, TITLE, BODY, NotificationTarget.COURSE, 1L));

        // WHEN
        ResultActions actions = mvc.perform(delete("/notifications/{notificationId}", notification.getId())
                .header(ACCESS_TOKEN, accessToken));

        // THEN
        actions.andDo(restDocs.document(
                requestHeaders(
                        headerWithName(ACCESS_TOKEN)
                                .description("인증 토큰")
                ),
                pathParameters(
                        parameterWithName("notificationId")
                                .description("알림 id")
                                .attributes(key("type").value("Number"))
                )
        ));
    }

    @Test
    @DisplayName("알림 전체 삭제")
    void removeAll() throws Exception {
        // GIVEN
        User user = userRepository.save(User.createEmailUser(EMAIL1, PASSWORD, NAME1, DEVICE_TOKEN));
        String accessToken = userService.login(EMAIL1, PASSWORD, DEVICE_TOKEN);
        User mate = userRepository.save(User.createEmailUser(EMAIL2, PASSWORD, NAME2, DEVICE_TOKEN));
        Planet planet = planetRepository.save(new Planet(user, NAME1, IMAGE, INVITE_CODE));
        planet.join(mate);
        Notification notification = notificationRepository.save(new Notification(user, TITLE, BODY, NotificationTarget.COURSE, 1L));

        // WHEN
        ResultActions actions = mvc.perform(delete("/notifications")
                .header(ACCESS_TOKEN, accessToken));

        // THEN
        actions.andDo(restDocs.document(
                requestHeaders(
                        headerWithName(ACCESS_TOKEN)
                                .description("인증 토큰")
                )
        ));
    }
}
