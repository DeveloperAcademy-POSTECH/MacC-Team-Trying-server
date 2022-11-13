package trying.cosmos.test.course.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import trying.cosmos.domain.course.entity.Course;
import trying.cosmos.domain.course.repository.CourseRepository;
import trying.cosmos.domain.course.service.CourseService;
import trying.cosmos.domain.planet.entity.Planet;
import trying.cosmos.domain.planet.repository.PlanetRepository;
import trying.cosmos.domain.user.entity.User;
import trying.cosmos.domain.user.repository.UserRepository;

import javax.persistence.EntityManager;
import java.util.NoSuchElementException;

import static java.time.LocalDate.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static trying.cosmos.test.TestVariables.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@DisplayName("코스 삭제")
public class DeleteTest {

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
    @DisplayName("실패")
    class fail {

        @Test
        @DisplayName("코스가 존재하지 않으면 NO_DATA 오류를 발생시킨다.")
        void no_course() throws Exception {
            // GIVEN
            User user = userRepository.save(User.createEmailUser(EMAIL1, PASSWORD, NAME1, DEVICE_TOKEN));

            // WHEN THEN
            assertThatThrownBy(() -> courseService.delete(user.getId(), NOT_EXIST))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @Test
        @DisplayName("사용자 행성의 코스가 아니면 NO_DATA 오류를 발생시킨다.")
        void others_course() throws Exception {
            // GIVEN
            User user = userRepository.save(User.createEmailUser(EMAIL1, PASSWORD, NAME1, DEVICE_TOKEN));
            Planet planet = planetRepository.save(new Planet(user, NAME1, IMAGE, INVITE_CODE));
            Course course = courseRepository.save(new Course(planet, TITLE, now()));

            User guest = userRepository.save(User.createEmailUser(EMAIL2, PASSWORD, NAME2, DEVICE_TOKEN));

            // WHEN THEN
            assertThatThrownBy(() -> courseService.delete(guest.getId(), course.getId()))
                    .isInstanceOf(NoSuchElementException.class);
        }
    }
    
    @Nested
    @DisplayName("성공")
    class success {
        
        @Test
        @DisplayName("코스를 삭제한다")
        void delete() throws Exception {
            // GIVEN
            User user = userRepository.save(User.createEmailUser(EMAIL1, PASSWORD, NAME1, DEVICE_TOKEN));
            User mate = userRepository.save(User.createEmailUser(EMAIL2, PASSWORD, NAME2, DEVICE_TOKEN));
            Planet planet = planetRepository.save(new Planet(user, NAME1, IMAGE, INVITE_CODE));
            planet.join(mate);
            Course course = courseService.create(user.getId(), TITLE, now(), course_place_request1);

            // WHEN
            courseService.delete(user.getId(), course.getId());

            // THEN
            assertThat(courseRepository.searchById(planet, course.getId()))
                    .isEmpty();
        }
    }
}
