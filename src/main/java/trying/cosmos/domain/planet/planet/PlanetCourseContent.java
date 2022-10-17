package trying.cosmos.domain.planet.planet;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import trying.cosmos.domain.course.Course;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlanetCourseContent {

    private String title;

    public PlanetCourseContent(Course course) {
        this.title = course.getTitle();
    }
}
