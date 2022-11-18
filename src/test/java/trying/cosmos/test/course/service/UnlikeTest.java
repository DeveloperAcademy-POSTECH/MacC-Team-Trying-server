package trying.cosmos.test.course.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import trying.cosmos.domain.course.entity.Course;
import trying.cosmos.domain.course.entity.CourseLike;
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
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static trying.cosmos.test.TestVariables.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@DisplayName("코스 좋아요 취소")
public class UnlikeTest {

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
        @DisplayName("코스가 존재하지 않으면 NO_DATA 오류를 발생시킨다.")
        void no_course() throws Exception {
            // GIVEN
            User user = userRepository.save(User.createEmailUser(EMAIL1, PASSWORD, NAME1, DEVICE_TOKEN));
            User mate = userRepository.save(User.createEmailUser(EMAIL2, PASSWORD, NAME2, DEVICE_TOKEN));
            Planet planet = planetRepository.save(new Planet(user, NAME1, IMAGE, INVITE_CODE));
            planet.join(mate);

            // WHEN THEN
            assertThatThrownBy(() -> courseService.unlike(user.getId(), NOT_EXIST))
                    .isInstanceOf(NoSuchElementException.class);
        }
        
        @Test
        @DisplayName("사용자 행성의 코스가 아니라면 NO_DATA 오류를 발생시킨다.")
        void others_course() throws Exception {
            // GIVEN
            User user = userRepository.save(User.createEmailUser(EMAIL1, PASSWORD, NAME1, DEVICE_TOKEN));
            User mate = userRepository.save(User.createEmailUser(EMAIL2, PASSWORD, NAME2, DEVICE_TOKEN));
            Planet planet = planetRepository.save(new Planet(user, NAME1, IMAGE, INVITE_CODE));
            planet.join(mate);

            User other = userRepository.save(User.createEmailUser(EMAIL3, PASSWORD, NAME3, DEVICE_TOKEN));
            Planet othersPlanet = planetRepository.save(new Planet(other, NAME2, IMAGE, INVITE_CODE));
            Course othersCourse = courseRepository.save(new Course(othersPlanet, TITLE, LocalDate.now()));
            em.persist(new CourseLike(other, othersCourse));

            // WHEN THEN
            assertThatThrownBy(() -> courseService.unlike(user.getId(), othersCourse.getId()))
                    .isInstanceOf(NoSuchElementException.class);
        }
        
        @Test
        @DisplayName("좋아요가 되어있지 않다면 NO_DATA 오류를 발생시킨다.")
        void not_liked() throws Exception {
            // GIVEN
            User user = userRepository.save(User.createEmailUser(EMAIL1, PASSWORD, NAME1, DEVICE_TOKEN));
            User mate = userRepository.save(User.createEmailUser(EMAIL2, PASSWORD, NAME2, DEVICE_TOKEN));
            Planet planet = planetRepository.save(new Planet(user, NAME1, IMAGE, INVITE_CODE));
            planet.join(mate);
            Course course = courseRepository.save(new Course(planet, TITLE, LocalDate.now()));

            // WHEN THEN
            assertThatThrownBy(() -> courseService.unlike(user.getId(), course.getId()))
                    .isInstanceOf(CustomException.class)
                    .hasMessage(ExceptionType.NO_DATA.getMessage());
        }
    }
    
    @Nested
    @DisplayName("성공")
    class success {
        
        @Test
        @DisplayName("코스 좋아요를 취소한다.")
        void unlike() throws Exception {
            // GIVEN
            User user = userRepository.save(User.createEmailUser(EMAIL1, PASSWORD, NAME1, DEVICE_TOKEN));
            User mate = userRepository.save(User.createEmailUser(EMAIL2, PASSWORD, NAME2, DEVICE_TOKEN));
            Planet planet = planetRepository.save(new Planet(user, NAME1, IMAGE, INVITE_CODE));
            planet.join(mate);
            Course course = courseRepository.save(new Course(planet, TITLE, LocalDate.now()));

            em.persist(new CourseLike(user, course));

            // WHEN
            courseService.unlike(user.getId(), course.getId());

            // THEN
            assertThat(courseService.find(user.getId(), course.getId()).isLiked()).isFalse();
        }
    }
}
