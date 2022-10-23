package trying.cosmos.domain.planet.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import trying.cosmos.domain.planet.entity.Planet;
import trying.cosmos.domain.planet.entity.PlanetImageType;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlanetFindResponse {

    private Long planetId;
    private String name;
    private PlanetImageType image;
    private int dday;

    public PlanetFindResponse(Planet planet) {
        this.planetId = planet.getId();
        this.name = planet.getName();
        this.image = planet.getImage();
        this.dday = planet.getDday();
    }
}
