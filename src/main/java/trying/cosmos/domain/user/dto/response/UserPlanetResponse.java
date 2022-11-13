package trying.cosmos.domain.user.dto.response;

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
public class UserPlanetResponse {

    private String name;
    private int dday;
    private String image;
    private String code;

    public UserPlanetResponse(Planet planet, boolean hasMate) {
        this.name = planet.getName();
        this.dday = planet.getDday();
        this.image = planet.getImage();
        this.code = hasMate ? null : planet.getInviteCode();
    }
}
