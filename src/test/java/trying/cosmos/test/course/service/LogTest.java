package trying.cosmos.test.course.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import trying.cosmos.domain.course.dto.response.LogCourseFindResponse;
import trying.cosmos.domain.course.entity.Course;
import trying.cosmos.domain.course.repository.CourseRepository;
import trying.cosmos.domain.course.service.CourseService;
import trying.cosmos.domain.planet.entity.Planet;
import trying.cosmos.domain.planet.repository.PlanetRepository;
import trying.cosmos.domain.review.entity.Review;
import trying.cosmos.domain.user.entity.User;
import trying.cosmos.domain.user.repository.UserRepository;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static trying.cosmos.test.TestVariables.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@DisplayName("로그 조회")
public class LogTest {

    @Autowired
    CourseService courseService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PlanetRepository planetRepository;

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    EntityManager em;

    @Nested
    @DisplayName("성공")
    class success {

        @Test
        @DisplayName("리뷰가 있는 코스 정보를 최근 순서대로 반환한다.")
        void log() throws Exception {
            // GIVEN
            User user = userRepository.save(User.createEmailUser(EMAIL1, PASSWORD, NAME1, DEVICE_TOKEN));
            User mate = userRepository.save(User.createEmailUser(EMAIL2, PASSWORD, NAME2, DEVICE_TOKEN));
            Planet planet = planetRepository.save(new Planet(user, NAME1, IMAGE, INVITE_CODE));
            planet.join(mate);

            Course courseYesterday = courseRepository.save(new Course(planet, TITLE, LocalDate.now().minusDays(1)));
            em.persist(new Review(user, courseYesterday, BODY));
            Course courseToday = courseRepository.save(new Course(planet, TITLE, LocalDate.now()));
            em.persist(new Review(user, courseToday, BODY));
            Course courseTomorrow = courseRepository.save(new Course(planet, TITLE, LocalDate.now().plusDays(1)));

            // WHEN
            Slice<LogCourseFindResponse> courses = courseService.findLogs(user.getId(), pageable);

            // THEN
            List<Long> courseIds = courses.getContent().stream()
                    .map(LogCourseFindResponse::getCourseId)
                    .collect(Collectors.toList());
            assertThat(courseIds).containsExactly(courseToday.getId(), courseYesterday.getId());
        }
    }
}
