package trying.cosmos.domain.planet.dto.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlanetUpdateRequest {

    @Pattern(regexp = "^[가-힣A-Za-z0-9]{2,8}", message = "행성 이름은 한글, 영어, 숫자로 이루어진 2~8자리 문자열입니다.")
    private String name;

    private String date;

    @NotBlank
    private String image;

    public PlanetUpdateRequest(String name, String date, String image) {
        this.name = name;
        this.date = date;
        this.image = image;
    }
}
