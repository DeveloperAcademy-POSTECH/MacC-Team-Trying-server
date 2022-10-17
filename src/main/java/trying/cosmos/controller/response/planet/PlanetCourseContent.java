package trying.cosmos.controller.response.planet;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import trying.cosmos.entity.Course;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlanetCourseContent {

    private String title;

    public PlanetCourseContent(Course course) {
        this.title = course.getTitle();
    }
}
