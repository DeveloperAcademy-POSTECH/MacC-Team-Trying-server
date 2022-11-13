package trying.cosmos.test.user.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@DisplayName("활동 조회")
public class FindActivityTest {
    
    @Nested
    @DisplayName("실패")
    class fail {
    
        @Test
        @DisplayName("사용자가 존재하지 않는다면 NO_DATA 오류를 발생시킨다.")
        void no_user() throws Exception {
            // GIVEN

            // WHEN

            // THEN
        }

        @Test
        @DisplayName("사용자의 행성이 존재하지 않는다면 NO_PLANET 오류를 발생시킨다.")
        void no_planet() throws Exception {
            // GIVEN

            // WHEN

            // THEN
        }

        @Test
        @DisplayName("사용자의 메이트가 존재하지 않는다면 NO_MATE 오류를 발생시킨다.")
        void no_mate() throws Exception {
            // GIVEN

            // WHEN

            // THEN
        }
    }
    
    @Nested
    @DisplayName("성공")
    class success {
        
        @Test
        @DisplayName("사용자 활동을 반환한다.")
        void find_activity() throws Exception {
            // GIVEN
            
            // WHEN
            
            // THEN
        }
    }
}
