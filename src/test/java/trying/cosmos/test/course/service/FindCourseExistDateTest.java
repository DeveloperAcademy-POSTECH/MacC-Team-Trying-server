package trying.cosmos.test.course.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import trying.cosmos.domain.course.dto.response.CourseDateResponse;
import trying.cosmos.domain.course.entity.Course;
import trying.cosmos.domain.course.repository.CourseRepository;
import trying.cosmos.domain.course.service.CourseService;
import trying.cosmos.domain.planet.entity.Planet;
import trying.cosmos.domain.planet.repository.PlanetRepository;
import trying.cosmos.domain.user.entity.User;
import trying.cosmos.domain.user.repository.UserRepository;
import trying.cosmos.global.utils.DateUtils;

import javax.persistence.EntityManager;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static trying.cosmos.test.TestVariables.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@DisplayName("코스가 존재하는 날짜 조회")
public class FindCourseExistDateTest {

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

    @BeforeEach
    void setup() {
        em.persist(place1);
    }
    
    @Nested
    @DisplayName("성공")
    class success {
        
        @Test
        @DisplayName("시작 날짜와 끝 날짜 사이에 코스가 존재하는 날짜를 반환한다.")
        void find() throws Exception {
            // GIVEN
            User user = userRepository.save(User.createEmailUser(EMAIL1, PASSWORD, NAME1, DEVICE_TOKEN, true));
            User mate = userRepository.save(User.createEmailUser(EMAIL2, PASSWORD, NAME2, DEVICE_TOKEN, true));
            Planet planet = planetRepository.save(new Planet(user, NAME1, IMAGE, INVITE_CODE));
            planet.join(mate);

            Course courseToday = courseRepository.save(new Course(planet, TITLE, LocalDate.now()));
            Course courseTomorrow = courseRepository.save(new Course(planet, TITLE, LocalDate.now().plusDays(1)));

            // WHEN
            CourseDateResponse dates = courseService.findCourseDates(user.getId(), LocalDate.now(), LocalDate.now().plusDays(1));

            // THEN
            assertThat(dates.getDates()).containsExactly(DateUtils.getFormattedDate(LocalDate.now()));
        }
    }
}
