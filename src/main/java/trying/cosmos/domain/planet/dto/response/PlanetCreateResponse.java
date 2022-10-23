package trying.cosmos.domain.planet.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import trying.cosmos.domain.planet.entity.Planet;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlanetCreateResponse {

    private Long planetId;
    private String code;

    public PlanetCreateResponse(Planet planet) {
        this.planetId = planet.getId();
        this.code = planet.getInviteCode();
    }
}
