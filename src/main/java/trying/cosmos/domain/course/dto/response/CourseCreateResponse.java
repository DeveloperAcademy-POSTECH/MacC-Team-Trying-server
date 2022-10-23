package trying.cosmos.domain.course.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import trying.cosmos.domain.course.entity.Course;
import trying.cosmos.domain.course.entity.Star;
import trying.cosmos.domain.course.service.StarSignGenerator;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CourseCreateResponse {

    private Long courseId;

    private List<Star> stars;

    public CourseCreateResponse(Course course) {
        this.courseId = course.getId();
        this.stars = StarSignGenerator.generate(course.getTags().stream()
                .map(tag -> tag.getPlace().getCoordinate())
                .collect(Collectors.toList()));
    }
}
