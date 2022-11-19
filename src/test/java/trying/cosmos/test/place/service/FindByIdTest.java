package trying.cosmos.test.place.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import trying.cosmos.domain.place.entity.Place;
import trying.cosmos.domain.place.repository.PlaceRepository;
import trying.cosmos.domain.place.service.PlaceService;

import javax.persistence.EntityManager;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static trying.cosmos.test.TestVariables.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@DisplayName("아이디로 장소 조회")
public class FindByIdTest {

    @Autowired
    PlaceService placeService;

    @Autowired
    PlaceRepository placeRepository;

    @Autowired
    EntityManager em;
    
    @Nested
    @DisplayName("실패")
    class fail {
    
        @Test
        @DisplayName("장소가 존재하지 않는다면 NO_DATA 오류를 발생시킨다.")
        void no_place() throws Exception {
            // WHEN THEN
            assertThatThrownBy(() -> placeService.find(NOT_EXIST))
                    .isInstanceOf(NoSuchElementException.class);
        }
    }
    
    @Nested
    @DisplayName("성공")
    class success {
        
        @Test
        @DisplayName("장소를 반환한다.")
        void find() throws Exception {
            // WHEN
            Place place1 = placeService.create(PLACE_IDENTIFIER1, NAME1, CATEGORY1, ADDRESS, 0.0, 0.1);
            Place place = placeService.find(place1.getId());

            // THEN
            assertThat(place)
                    .isEqualTo(place1);
        }
    }
}
