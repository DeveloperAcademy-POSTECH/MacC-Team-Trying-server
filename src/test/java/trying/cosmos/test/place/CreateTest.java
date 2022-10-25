package trying.cosmos.test.place;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import trying.cosmos.domain.place.dto.request.PlaceCreateRequest;
import trying.cosmos.domain.place.entity.Place;
import trying.cosmos.domain.place.repository.PlaceRepository;
import trying.cosmos.domain.place.service.PlaceService;

import static org.assertj.core.api.Assertions.assertThat;
import static trying.cosmos.test.component.TestVariables.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@DisplayName("(Place.Service) 장소 생성")
public class CreateTest {

    @Autowired
    PlaceService placeService;

    @Autowired
    PlaceRepository placeRepository;

    @Nested
    @DisplayName("성공")
    class success {

        @Test
        @DisplayName("장소 첫 사용시 장소 추가")
        void create() throws Exception {
            placeService.create(new PlaceCreateRequest(PLACE_NAME, LATITUDE, LONGITUDE));
            assertThat(placeRepository.count()).isEqualTo(1);
        }

        @Test
        @DisplayName("장소 재 사용시 이전 장소 이용")
        void find() throws Exception {
            Place place1 = placeService.create(new PlaceCreateRequest(PLACE_NAME, LATITUDE, LONGITUDE));
            Place place2 = placeService.create(new PlaceCreateRequest(PLACE_NAME, LATITUDE, LONGITUDE));
            assertThat(place1).isEqualTo(place2);
            assertThat(placeRepository.count()).isEqualTo(1);
        }
    }
}
