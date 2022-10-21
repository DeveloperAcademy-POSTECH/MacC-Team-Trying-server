package trying.cosmos.domain.planet.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PlanetUpdateRequest {

    @Pattern(regexp = "^[가-힣A-Za-z0-9]{2,8}", message = "행성 이름은 한글, 영어, 숫자로 이루어진 2~8자리 문자열입니다.")
    private String name;

    @Min(value = 1, message = "D-Day는 0 이상 입니다.")
    private int dday;
}
