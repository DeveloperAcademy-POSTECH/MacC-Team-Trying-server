package trying.cosmos.test.review.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import trying.cosmos.domain.course.entity.Course;
import trying.cosmos.domain.course.repository.CourseRepository;
import trying.cosmos.domain.coursereview.entity.CourseReview;
import trying.cosmos.domain.coursereview.repository.CourseReviewRepository;
import trying.cosmos.domain.coursereview.service.CourseReviewService;
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
@DisplayName("코스 리뷰 생성")
public class CreateTest {

    @Autowired
    CourseReviewService reviewService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PlanetRepository planetRepository;

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    CourseReviewRepository courseReviewRepository;

    @Autowired
    EntityManager em;
    
    @Nested
    @DisplayName("실패")
    class fail {
        
        @Test
        @DisplayName("코스가 존재하지 않으면 NO_DATA 오류를 발생시킨다.")
        void no_course() throws Exception {
            // GIVEN
            User user = userRepository.save(User.createEmailUser(EMAIL1, PASSWORD, NAME1, DEVICE_TOKEN, true));
            User mate = userRepository.save(User.createEmailUser(EMAIL2, PASSWORD, NAME2, DEVICE_TOKEN, true));
            Planet planet = planetRepository.save(new Planet(user, NAME1, IMAGE, INVITE_CODE));
            planet.join(mate);
            
            // WHEN THEN
            assertThatThrownBy(() -> reviewService.create(user.getId(), NOT_EXIST, BODY, null))
                    .isInstanceOf(NoSuchElementException.class);
        }
        
        @Test
        @DisplayName("사용자 행성의 코스가 아니라면 NO_DATA 오류를 발생시킨다.")
        void others_course() throws Exception {
            // GIVEN
            User user = userRepository.save(User.createEmailUser(EMAIL1, PASSWORD, NAME1, DEVICE_TOKEN, true));
            User mate = userRepository.save(User.createEmailUser(EMAIL2, PASSWORD, NAME2, DEVICE_TOKEN, true));
            Planet planet = planetRepository.save(new Planet(user, NAME1, IMAGE, INVITE_CODE));
            planet.join(mate);

            User other = userRepository.save(User.createEmailUser(EMAIL3, PASSWORD, NAME3, DEVICE_TOKEN, true));
            Planet othersPlanet = planetRepository.save(new Planet(other, NAME2, IMAGE, INVITE_CODE));
            Course othersCourse = courseRepository.save(new Course(othersPlanet, TITLE, LocalDate.now()));
            
            // WHEN THEN
            assertThatThrownBy(() -> reviewService.create(user.getId(), othersCourse.getId(), BODY, null))
                    .isInstanceOf(NoSuchElementException.class);
        }
        
        @Test
        @DisplayName("리뷰가 존재한다면 DUPLICATED 오류를 발생시킨다.")
        void already_reviewed() throws Exception {
            // GIVEN
            User user = userRepository.save(User.createEmailUser(EMAIL1, PASSWORD, NAME1, DEVICE_TOKEN, true));
            User mate = userRepository.save(User.createEmailUser(EMAIL2, PASSWORD, NAME2, DEVICE_TOKEN, true));
            Planet planet = planetRepository.save(new Planet(user, NAME1, IMAGE, INVITE_CODE));
            planet.join(mate);

            Course course = courseRepository.save(new Course(planet, TITLE, LocalDate.now()));

            em.persist(new CourseReview(user, course, BODY));
            
            // WHEN THEN
            assertThatThrownBy(() -> reviewService.create(user.getId(), course.getId(), BODY, null))
                    .isInstanceOf(CustomException.class)
                    .hasMessage(ExceptionType.DUPLICATED.getMessage());
        }
    }
    
    @Nested
    @DisplayName("성공")
    class success {
        
        @Test
        @DisplayName("코스 리뷰를 생성한다")
        void create() throws Exception {
            // GIVEN
            User user = userRepository.save(User.createEmailUser(EMAIL1, PASSWORD, NAME1, DEVICE_TOKEN, true));
            User mate = userRepository.save(User.createEmailUser(EMAIL2, PASSWORD, NAME2, DEVICE_TOKEN, true));
            Planet planet = planetRepository.save(new Planet(user, NAME1, IMAGE, INVITE_CODE));
            planet.join(mate);

            Course course = courseRepository.save(new Course(planet, TITLE, LocalDate.now()));

            // WHEN
            reviewService.create(user.getId(), course.getId(), BODY, null);

            // THEN
            assertThat(course.getReview(user))
                    .isPresent();
        }
    }
}
