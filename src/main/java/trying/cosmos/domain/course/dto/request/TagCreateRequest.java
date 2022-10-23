package trying.cosmos.domain.course.dto.request;

import lombok.*;
import trying.cosmos.domain.place.dto.request.PlaceCreateRequest;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class TagCreateRequest {

    @NotNull
    private PlaceCreateRequest place;

    @NotBlank
    @Pattern(regexp = "^[가-힣A-Za-z0-9]{2,8}", message = "태그는 한글, 영어, 숫자로 이루어진 2~8자리 문자열입니다.")
    private String name;
}
