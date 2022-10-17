package trying.cosmos.controller.response.course;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import trying.cosmos.entity.Course;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CourseCreateResponse {

    private Long id;

    public CourseCreateResponse(Course course) {
        this.id = course.getId();
    }
}
