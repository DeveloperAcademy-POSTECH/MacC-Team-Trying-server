package trying.cosmos.test.course.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import trying.cosmos.domain.course.dto.request.TagCreateRequest;
import trying.cosmos.domain.course.dto.response.CourseFindContent;
import trying.cosmos.domain.course.repository.CourseRepository;
import trying.cosmos.domain.course.service.CourseService;
import trying.cosmos.domain.place.dto.request.PlaceCreateRequest;
import trying.cosmos.domain.place.service.PlaceService;
import trying.cosmos.domain.planet.entity.Planet;
import trying.cosmos.domain.planet.repository.PlanetRepository;
import trying.cosmos.domain.planet.service.PlanetService;
import trying.cosmos.domain.user.entity.User;
import trying.cosmos.domain.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static trying.cosmos.domain.course.entity.Access.PRIVATE;
import static trying.cosmos.domain.course.entity.Access.PUBLIC;
import static trying.cosmos.domain.planet.entity.PlanetImageType.EARTH;
import static trying.cosmos.domain.user.entity.UserStatus.LOGIN;
import static trying.cosmos.global.auth.entity.Authority.USER;
import static trying.cosmos.test.component.TestVariables.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@DisplayName("(Course.Service) 피드 조회")
public class FeedTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PlanetRepository planetRepository;

    @Autowired
    PlanetService planetService;

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    CourseService courseService;

    @Autowired
    PlaceService placeService;

    private Long userId;
    private Pageable pageable = PageRequest.of(0, 5);

    @BeforeEach
    void setup() {
        User me = userRepository.save(new User("me@gmail.com", PASSWORD, "me", LOGIN, USER));
        this.userId = me.getId();
        User follow = userRepository.save(new User("follow@gmail.com", PASSWORD, "follow", LOGIN, USER));
        User notFollow = userRepository.save(new User("notfollow@gmail.com", PASSWORD, "notfollow", LOGIN, USER));

        Planet myPlanet = planetRepository.save(new Planet(me, PLANET_NAME, EARTH));
        Planet followPlanet = planetRepository.save(new Planet(follow, PLANET_NAME, EARTH));
        planetService.follow(me.getId(), followPlanet.getId());
        Planet notFollowPlanet = planetRepository.save(new Planet(notFollow, PLANET_NAME, EARTH));
        List<TagCreateRequest> tagRequest = List.of(new TagCreateRequest(new PlaceCreateRequest(1L, PLACE_NAME, LATITUDE, LONGITUDE), TAG_NAME));

        courseService.create(me.getId(), myPlanet.getId(), "title1", "body", PRIVATE, tagRequest, null);
        courseService.create(me.getId(), myPlanet.getId(), "title2", "body", PUBLIC, tagRequest, null);
        courseService.create(follow.getId(), followPlanet.getId(), "title3", "body", PRIVATE, tagRequest, null);
        courseService.create(follow.getId(), followPlanet.getId(), "title4", "body", PUBLIC, tagRequest, null);
        courseService.create(notFollow.getId(), notFollowPlanet.getId(), "title5", "body", PRIVATE, tagRequest, null);
        courseService.create(notFollow.getId(), notFollowPlanet.getId(), "title6", "body", PUBLIC, tagRequest, null);
    }

    @Nested
    @DisplayName("성공")
    class success {

        @Test
        @DisplayName("내 행성은 모든 코스, 팔로우한 행성은 공개코스만 최신순으로 조회")
        void feed() throws Exception {
            List<String> titles = courseService.getFeeds(userId, pageable).getContent().stream()
                    .map(CourseFindContent::getTitle)
                    .collect(Collectors.toList());
            assertThat(titles).containsExactly("title4", "title2", "title1");
        }
    }
}
