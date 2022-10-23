package trying.cosmos.domain.planet.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import trying.cosmos.domain.planet.entity.Planet;
import trying.cosmos.domain.planet.entity.PlanetImageType;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PlanetFindContent {

    private String name;
    private int dday;
    private PlanetImageType image;

    public PlanetFindContent(Planet planet) {
        this.name = planet.getName();
        this.dday = planet.getDday();
        this.image = planet.getImage();
    }
}