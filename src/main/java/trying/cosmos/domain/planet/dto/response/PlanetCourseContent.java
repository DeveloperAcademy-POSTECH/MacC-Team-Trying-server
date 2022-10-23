package trying.cosmos.domain.planet.dto.response;

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
public class PlanetCourseContent {

    private String title;

    private List<Star> stars;

    public PlanetCourseContent(Course course) {
        this.title = course.getTitle();
        this.stars = StarSignGenerator.generate(course.getTags().stream()
                .map(tag -> tag.getPlace().getCoordinate())
                .collect(Collectors.toList()));
    }
}
