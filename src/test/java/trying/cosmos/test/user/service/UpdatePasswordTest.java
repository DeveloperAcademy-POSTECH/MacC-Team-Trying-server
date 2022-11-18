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
import trying.cosmos.global.utils.BCryptUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static trying.cosmos.test.TestVariables.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@DisplayName("사용자 비밀번호 변경")
public class UpdatePasswordTest {

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;
    
    @Nested
    @DisplayName("성공")
    class success {
        
        @Test
        @DisplayName("사용자 비밀번호를 변경한다.")
        void update_password() throws Exception {
            // GIVEN
            User user = userRepository.save(User.createEmailUser(EMAIL1, PASSWORD, NAME1, DEVICE_TOKEN));

            // WHEN
            userService.updatePassword(user.getId(), PASSWORD, "UPDATED");
            
            // THEN
            assertThat(BCryptUtils.isMatch("UPDATED", user.getPassword()))
                    .isTrue();
        }
    }
}
