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
public class PlanetFindResponse {

    private String name;
    private int dday;
    private String image;

    public PlanetFindResponse(Planet planet) {
        this.name = planet.getName();
        this.dday = planet.getDday();
        this.image = planet.getImage();
    }
}
