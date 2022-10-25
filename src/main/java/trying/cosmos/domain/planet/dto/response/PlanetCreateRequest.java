package trying.cosmos.domain.planet.dto.response;

import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PlanetCreateRequest {

    @Pattern(regexp = "^[가-힣A-Za-z0-9]{2,8}", message = "행성 이름은 한글, 영어, 숫자로 이루어진 2~8자리 문자열입니다.")
    private String name;

    @NotNull
    private String image;
}
