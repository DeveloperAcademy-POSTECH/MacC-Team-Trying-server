package trying.cosmos.domain.planet.dto.response;

import lombok.*;
import trying.cosmos.domain.planet.entity.Planet;
import trying.cosmos.domain.planet.entity.PlanetImageType;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PlanetFindContent {

    private Long planetId;
    private String name;
    private int dday;
    private PlanetImageType image;

    public PlanetFindContent(Planet planet) {
        this.planetId = planet.getId();
        this.name = planet.getName();
        this.dday = planet.getDday();
        this.image = planet.getImage();
    }
}
