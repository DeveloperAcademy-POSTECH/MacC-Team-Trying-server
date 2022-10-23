package trying.cosmos.test.user.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import trying.cosmos.domain.user.repository.UserRepository;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static trying.cosmos.test.component.TestVariables.EMAIL;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@DisplayName("(User.Repository) 사용자가 존재하지 않는 경우")
public class NotExistTest {

    @Autowired
    UserRepository userRepository;

    @Nested
    @DisplayName("실패")
    public class success {

        @Test
        @DisplayName("해당 id의 사용자가 존재하지 않는 경우")
        void no_id() throws Exception {
            assertThatThrownBy(() -> userRepository.findById(-1L).orElseThrow())
                    .isInstanceOf(NoSuchElementException.class);
        }

        @Test
        @DisplayName("해당 email의 사용자가 존재하지 않는 경우")
        void no_email() throws Exception {
            assertThatThrownBy(() -> userRepository.findByEmail(EMAIL).orElseThrow())
                    .isInstanceOf(NoSuchElementException.class);
        }
    }
}
