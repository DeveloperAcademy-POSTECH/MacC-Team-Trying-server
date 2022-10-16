package trying.cosmos.controller.response.planet;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import trying.cosmos.entity.Planet;
import trying.cosmos.entity.component.PlanetImageType;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlanetFindResponse {

    private Long id;
    private String name;
    private PlanetImageType image;

    public PlanetFindResponse(Planet planet) {
        this.id = planet.getId();
        this.name = planet.getName();
        this.image = planet.getImage();
    }
}
