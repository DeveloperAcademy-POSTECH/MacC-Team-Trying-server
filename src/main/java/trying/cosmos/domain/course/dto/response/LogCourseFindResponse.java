package trying.cosmos.domain.course.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import trying.cosmos.domain.course.entity.Course;
import trying.cosmos.global.utils.DateUtils;

import java.util.List;
import java.util.stream.Collectors;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LogCourseFindResponse {

    private Long courseId;
    private String title;
    private String date;
    private boolean liked;
    private List<LogPlaceResponse> places;

    public LogCourseFindResponse(Course course, boolean liked) {
        this.courseId = course.getId();
        this.title = course.getTitle();
        this.date = DateUtils.getFormattedDate(course.getDate());
        this.liked = liked;
        this.places = course.getPlaces().stream()
                .map(LogPlaceResponse::new)
                .collect(Collectors.toList());
    }
}
