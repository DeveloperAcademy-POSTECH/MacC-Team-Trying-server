package trying.cosmos.test.review.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@DisplayName("메이트 리뷰 조회")
public class FindMateReviewTest {
    
    @Nested
    @DisplayName("실패")
    class fail {

        @Test
        @DisplayName("코스가 존재하지 않으면 NO_DATA 오류를 발생시킨다.")
        void no_course() throws Exception {
            // GIVEN

            // WHEN

            // THEN
        }

        @Test
        @DisplayName("사용자 행성의 코스가 아니라면 NO_DATA 오류를 발생시킨다.")
        void others_course() throws Exception {
            // GIVEN

            // WHEN

            // THEN
        }

        @Test
        @DisplayName("리뷰가 존재하지 않는다면 NO_DATA 오류를 발생시킨다.")
        void not_reviewed() throws Exception {
            // GIVEN

            // WHEN

            // THEN
        }
    }
    
    @Nested
    @DisplayName("성공")
    class success {
        
        @Test
        @DisplayName("메이트의 코스 리뷰를 반환한다.")
        void find_mate_review() throws Exception {
            // GIVEN

            // WHEN

            // THEN
        }
    }
}
