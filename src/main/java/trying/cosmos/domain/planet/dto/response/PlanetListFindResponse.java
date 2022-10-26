package trying.cosmos.domain.planet.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.domain.Slice;

import java.util.ArrayList;
import java.util.List;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlanetListFindResponse {

    private List<PlanetListFindContent> planets = new ArrayList<>();
    private int size;
    private boolean hasNext;

    public PlanetListFindResponse(Slice<PlanetListFindContent> planetSlice) {
        this.planets = planetSlice.getContent();
        this.size = planetSlice.getNumberOfElements();
        this.hasNext = planetSlice.hasNext();
    }
}
