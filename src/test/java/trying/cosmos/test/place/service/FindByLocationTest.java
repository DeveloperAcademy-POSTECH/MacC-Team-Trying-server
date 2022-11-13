package trying.cosmos.test.place.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import trying.cosmos.domain.place.dto.response.PlaceDistanceProjection;
import trying.cosmos.domain.place.entity.Place;
import trying.cosmos.domain.place.repository.PlaceRepository;
import trying.cosmos.domain.place.service.PlaceService;

import java.util.List;
import java.util.stream.Collectors;

import static trying.cosmos.test.TestVariables.pageable;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@DisplayName("위치로 장소 조회")
public class FindByLocationTest {

    @Autowired
    PlaceService placeService;

    @Autowired
    PlaceRepository placeRepository;
    
    @Nested
    @DisplayName("성공")
    class success {
        
//        @Test
        @DisplayName("가까운 가게를 반환한다.")
        void find() throws Exception {
            // GIVEN
            Place place1 = placeRepository.save(
                    new Place(2892017L,
                            "CU",
                            "D03A01",
                            "강원도 속초시 교동 744-18",
                            "강원도 속초시 수복로 62, (교동)",
                            38.1982324022544,
                            128.577028954977)
            );

            Place place2 = placeRepository.save(
                    new Place(
                            18350994L,
                            "해담푸드",
                            "Q01A01",
                            "강원도 속초시 교동 826-1",
                            "강원도 속초시 수복로 63, (교동)",
                            38.1984679917022,
                            128.576756804917
                    )
            );

            Place place3 = placeRepository.save(
                    new Place(
                            2948297L,
                            "정연회수산",
                            "Q03A01",
                            "충청남도 홍성군 서부면 남당리 859-1",
                            "충청남도 홍성군 서부면 남당항로213번길 1, (남당리)",
                            36.5389280805721,
                            126.470318972256
                    )
            );

            double latitude = 38.1982324022543;
            double longitude = 128.577028954912;
            double distance = 0.5;  // 0.5km

            // WHEN
            Slice<PlaceDistanceProjection> places = placeService.findByPosition(latitude, longitude, distance, pageable);

            // THEN
            List<Long> placeIds = places.getContent().stream()
                    .map(PlaceDistanceProjection::getPlaceId)
                    .collect(Collectors.toList());
            Assertions.assertThat(placeIds)
                    .containsExactly(place1.getId(), place2.getId());
        }
    }
}
