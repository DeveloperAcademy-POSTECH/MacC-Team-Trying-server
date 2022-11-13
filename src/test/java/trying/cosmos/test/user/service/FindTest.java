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
@DisplayName("사용자 조회")
public class FindTest {
    
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
    }
    
    @Nested
    @DisplayName("성공")
    class success {
        
        @Test
        @DisplayName("사용자 정보를 반환한다.")
        void find() throws Exception {
            // GIVEN
            
            // WHEN
            
            // THEN
        }
    }
}
