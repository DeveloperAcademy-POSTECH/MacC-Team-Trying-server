package trying.cosmos.controller.response.planet;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import trying.cosmos.entity.Planet;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlanetCreateResponse {

    private Long id;
    private String code;

    public PlanetCreateResponse(Planet planet) {
        this.id = planet.getId();
        this.code = planet.getInviteCode();
    }
}
