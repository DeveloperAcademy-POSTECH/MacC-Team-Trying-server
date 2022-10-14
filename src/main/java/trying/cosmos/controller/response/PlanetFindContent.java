package trying.cosmos.controller.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import trying.cosmos.entity.Planet;
import trying.cosmos.entity.component.PlanetImageType;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PlanetFindContent {

    private String name;
    private int dday;
    private PlanetImageType imageType;

    public PlanetFindContent(Planet planet) {
        this.name = planet.getName();
        this.dday = planet.getDday();
        this.imageType = planet.getImageType();
    }
}
