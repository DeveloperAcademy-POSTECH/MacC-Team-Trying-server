package trying.cosmos.controller.response.course;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import trying.cosmos.controller.response.planet.PlanetFindResponse;
import trying.cosmos.entity.Course;
import trying.cosmos.utils.date.DateUtils;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CourseFindContent {

    private Long id;
    private PlanetFindResponse planet;
    private String title;
    private String createdDate;

    public CourseFindContent(Course course) {
        this.id = course.getId();
        this.planet = new PlanetFindResponse(course.getPlanet());
        this.title = course.getTitle();
        this.createdDate = DateUtils.getFormattedDate(course.getCreatedDate());
    }
}
