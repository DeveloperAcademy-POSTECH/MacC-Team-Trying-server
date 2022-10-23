package trying.cosmos.domain.planet.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import trying.cosmos.domain.planet.entity.Planet;
import trying.cosmos.domain.planet.entity.PlanetImageType;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlanetPreviewResponse {

    private Long planetId;
    private String name;
    private PlanetImageType image;

    public PlanetPreviewResponse(Planet planet) {
        this.planetId = planet.getId();
        this.name = planet.getName();
        this.image = planet.getImage();
    }
}
