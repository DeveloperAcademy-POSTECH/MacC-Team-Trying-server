package trying.cosmos.domain.planet.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import trying.cosmos.domain.course.entity.Course;
import trying.cosmos.domain.course.entity.Star;
import trying.cosmos.domain.course.service.StarSignGenerator;
import trying.cosmos.global.utils.date.DateUtils;

import java.util.List;
import java.util.stream.Collectors;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlanetCourseContent {

    private Long courseId;
    private String createdDate;
    private String title;
    private List<Star> stars;

    public PlanetCourseContent(Course course) {
        this.courseId = course.getId();
        this.createdDate = DateUtils.getFormattedDate(course.getCreatedDate());
        this.title = course.getTitle();
        this.stars = StarSignGenerator.generate(course.getTags().stream()
                .map(tag -> tag.getPlace().getCoordinate())
                .collect(Collectors.toList()));
    }
}
