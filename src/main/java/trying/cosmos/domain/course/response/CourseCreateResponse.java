package trying.cosmos.domain.course.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import trying.cosmos.domain.course.Course;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CourseCreateResponse {

    private Long courseId;

    public CourseCreateResponse(Course course) {
        this.courseId = course.getId();
    }
}
