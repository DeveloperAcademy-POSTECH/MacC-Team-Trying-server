package trying.cosmos.domain.planet.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import trying.cosmos.domain.planet.entity.Planet;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlanetListFindContent {

    private Long planetId;
    private String name;
    private String image;
    private Boolean followed;

    public PlanetListFindContent(Planet planet, Boolean followed) {
        this.planetId = planet.getId();
        this.name = planet.getName();
        this.image = planet.getImage();
        this.followed = followed;
    }
}
