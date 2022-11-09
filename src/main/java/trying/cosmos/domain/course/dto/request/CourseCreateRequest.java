package trying.cosmos.domain.course.dto.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CourseCreateRequest {

    @NotNull
    private Long planetId;

    @NotBlank
    private String title;

    @NotBlank
    private String date;

    @NotNull
    private List<CoursePlaceRequest> places;

    public CourseCreateRequest(Long planetId, String title, String date, List<CoursePlaceRequest> places) {
        this.planetId = planetId;
        this.title = title;
        this.date = date;
        this.places = places;
    }
}
