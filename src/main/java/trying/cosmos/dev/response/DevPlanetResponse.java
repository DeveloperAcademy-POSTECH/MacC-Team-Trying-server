package trying.cosmos.dev.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import trying.cosmos.entity.Planet;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DevPlanetResponse {

    private DevCreateUserResponse owner;
    private DevPlanetContent planet;

    public DevPlanetResponse(Planet planet) {
        this.owner = new DevCreateUserResponse(planet.getOwners().get(0));
        this.planet = new DevPlanetContent(planet);
    }
}
