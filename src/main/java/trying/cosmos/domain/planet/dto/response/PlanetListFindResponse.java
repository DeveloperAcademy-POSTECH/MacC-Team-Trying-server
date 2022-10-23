package trying.cosmos.domain.planet.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Slice;
import trying.cosmos.domain.planet.entity.Planet;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlanetListFindResponse {

    private List<PlanetListFindContent> planets = new ArrayList<>();
    private int size;
    private boolean hasNext;

    public PlanetListFindResponse(Slice<Planet> planetSlice) {
        this.planets = planetSlice.getContent().stream()
                .map(PlanetListFindContent::new)
                .collect(Collectors.toList());
        this.size = planetSlice.getNumberOfElements();
        this.hasNext = planetSlice.hasNext();
    }
}
