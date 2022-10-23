package trying.cosmos.test.course.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import trying.cosmos.domain.course.repository.CourseRepository;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@DisplayName("(Course.Repository) 코스가 존재하지 않는 경우")
public class NotExistTest {

    @Autowired
    CourseRepository courseRepository;

    @Nested
    @DisplayName("실패")
    class fail {

        @Test
        @DisplayName("코스 id가 존재하지 않는 경우")
        void no_id() throws Exception {
            assertThat(courseRepository.findById(-1L)).isEmpty();
        }
    }
}
