package trying.cosmos.domain.course.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import trying.cosmos.domain.course.Course;
import trying.cosmos.domain.planet.planet.PlanetFindResponse;
import trying.cosmos.global.utils.date.DateUtils;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CourseFindResponse {

    private String title;
    private String body;
    private String createdDate;
    private boolean liked;
    private PlanetFindResponse planet;
    private List<TagFindResponse> tags;

    public CourseFindResponse(Course course, boolean liked) {
        this.title = course.getTitle();
        this.body = course.getBody();
        this.createdDate = DateUtils.getFormattedDate(course.getCreatedDate());
        this.liked = liked;
        this.planet = new PlanetFindResponse(course.getPlanet());
        this.tags = course.getTags().stream().map(TagFindResponse::new).collect(Collectors.toList());
    }
}
