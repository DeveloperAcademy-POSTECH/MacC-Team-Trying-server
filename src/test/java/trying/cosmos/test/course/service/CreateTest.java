package trying.cosmos.test.course.service;

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
import trying.cosmos.global.exception.CustomException;
import trying.cosmos.global.exception.ExceptionType;

import javax.persistence.EntityManager;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static trying.cosmos.test.TestVariables.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@DisplayName("코스 생성")
public class CreateTest {

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
    @DisplayName("실패")
    class fail {

        @Test
        @DisplayName("행성이 존재하지 않으면 NO_PLANET 오류를 발생시킨다.")
        void no_planet() throws Exception {
            // GIVEN
            User user = userRepository.save(User.createEmailUser(EMAIL1, PASSWORD, NAME1, DEVICE_TOKEN, true));

            // WHEN THEN
            assertThatThrownBy(() -> courseService.create(user.getId(), TITLE, LocalDate.now(), course_place_request1))
                    .isInstanceOf(CustomException.class)
                    .hasMessage(ExceptionType.NO_PLANET.getMessage());
        }

//        @Test
//        @DisplayName("메이트가 존재하지 않으면 PLANET_CREATE_FAILED 오류를 발생시킨다.")
//        void no_mate() throws Exception {
//            // GIVEN
//            User user = userRepository.save(User.createEmailUser(EMAIL1, PASSWORD, NAME1, DEVICE_TOKEN, true));
//            Planet planet = planetRepository.save(new Planet(user, NAME1, IMAGE, INVITE_CODE));
//
//            // WHEN THEN
//            assertThatThrownBy(() -> courseService.create(user.getId(), TITLE, LocalDate.now(), course_place_request1))
//                    .isInstanceOf(CustomException.class)
//                    .hasMessage(ExceptionType.PLANET_CREATE_FAILED.getMessage());
//        }

        @Test
        @DisplayName("해당 날짜에 코스가 존재한다면 DUPLICATED 오류를 발생시킨다.")
        void date_duplicated() throws Exception {
            // GIVEN
            User user = userRepository.save(User.createEmailUser(EMAIL1, PASSWORD, NAME1, DEVICE_TOKEN, true));
            User mate = userRepository.save(User.createEmailUser(EMAIL2, PASSWORD, NAME2, DEVICE_TOKEN, true));
            Planet planet = planetRepository.save(new Planet(user, NAME1, IMAGE, INVITE_CODE));
            planet.join(mate);
            Course course = courseRepository.save(new Course(planet, TITLE, LocalDate.now()));

            // WHEN THEN
            assertThatThrownBy(() -> courseService.create(user.getId(), TITLE, LocalDate.now(), course_place_request1))
                    .isInstanceOf(CustomException.class)
                    .hasMessage(ExceptionType.DUPLICATED.getMessage());
        }
    }
    
    @Nested
    @DisplayName("성공")
    class success {
        
        @Test
        @DisplayName("코스를 저장한다.")
        void create() throws Exception {
            // GIVEN
            User user = userRepository.save(User.createEmailUser(EMAIL1, PASSWORD, NAME1, DEVICE_TOKEN, true));
            User mate = userRepository.save(User.createEmailUser(EMAIL2, PASSWORD, NAME2, DEVICE_TOKEN, true));
            Planet planet = planetRepository.save(new Planet(user, NAME1, IMAGE, INVITE_CODE));
            planet.join(mate);

            // WHEN
            Course course = courseService.create(user.getId(), TITLE, LocalDate.now(), course_place_request1);

            // THEN
            assertThat(courseRepository.searchByPlanet(planet, pageable))
                    .containsExactly(course);
        }
    }
}
