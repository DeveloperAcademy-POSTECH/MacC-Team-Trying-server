package trying.cosmos.domain.course.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import trying.cosmos.domain.course.entity.Course;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CourseCreateResponse {

    private Long courseId;

//    추후 사용
//    private List<Star> stars;

    public CourseCreateResponse(Course course) {
        this.courseId = course.getId();
//        this.stars = StarSignGenerator.generate(course.getTags().stream()
//                .map(tag -> tag.getPlace().getCoordinate())
//                .collect(Collectors.toList()));
    }
}
