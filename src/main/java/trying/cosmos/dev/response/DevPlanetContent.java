package trying.cosmos.dev.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import trying.cosmos.entity.Planet;
import trying.cosmos.entity.component.PlanetImageType;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DevPlanetContent {

    private Long id;
    private String name;
    private int dday;
    private PlanetImageType image;

    public DevPlanetContent(Planet planet) {
        this.id = planet.getId();
        this.name = planet.getName();
        this.dday = planet.getDday();
        this.image = planet.getImage();
    }
}
