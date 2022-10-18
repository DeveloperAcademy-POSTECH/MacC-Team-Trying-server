package trying.cosmos.global.utils.dev.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import trying.cosmos.domain.planet.Planet;
import trying.cosmos.domain.planet.PlanetImageType;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DevPlanetContent {

    private Long planetId;
    private String name;
    private int dday;
    private PlanetImageType image;

    public DevPlanetContent(Planet planet) {
        this.planetId = planet.getId();
        this.name = planet.getName();
        this.dday = planet.getDday();
        this.image = planet.getImage();
    }
}
