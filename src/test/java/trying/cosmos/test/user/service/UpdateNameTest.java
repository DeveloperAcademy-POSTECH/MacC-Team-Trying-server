package trying.cosmos.test.user.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import trying.cosmos.domain.user.entity.User;
import trying.cosmos.domain.user.repository.UserRepository;
import trying.cosmos.domain.user.service.UserService;

import static org.assertj.core.api.Assertions.assertThat;
import static trying.cosmos.test.TestVariables.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@DisplayName("사용자 닉네임 변경")
public class UpdateNameTest {

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;
    
    @Nested
    @DisplayName("성공")
    class success {
        
        @Test
        @DisplayName("사용자 닉네임을 변경한다.")
        void update_name() throws Exception {
            // GIVEN
            User user = userRepository.save(User.createEmailUser(EMAIL1, PASSWORD, NAME1, DEVICE_TOKEN));

            // WHEN
            userService.updateName(user.getId(), "UPDATED");
            
            // THEN
            assertThat(user.getName())
                    .isEqualTo("UPDATED");
        }
    }
}
