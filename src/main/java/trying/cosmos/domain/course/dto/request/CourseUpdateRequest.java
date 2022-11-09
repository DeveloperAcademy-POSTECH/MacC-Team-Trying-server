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
public class CourseUpdateRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String date;

    @NotNull
    private List<CoursePlaceRequest> places;

    public CourseUpdateRequest(String title, String date, List<CoursePlaceRequest> places) {
        this.title = title;
        this.date = date;
        this.places = places;
    }
}
