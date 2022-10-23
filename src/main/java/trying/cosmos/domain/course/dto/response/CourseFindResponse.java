package trying.cosmos.domain.course.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import trying.cosmos.domain.course.entity.Course;
import trying.cosmos.domain.course.entity.CourseImage;
import trying.cosmos.domain.planet.dto.response.PlanetFindResponse;
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

    private List<String> images;

    public CourseFindResponse(Course course, boolean liked) {
        this.title = course.getTitle();
        this.body = course.getBody();
        this.createdDate = DateUtils.getFormattedDate(course.getCreatedDate());
        this.liked = liked;
        this.planet = new PlanetFindResponse(course.getPlanet());
        this.tags = course.getTags().stream().map(TagFindResponse::new).collect(Collectors.toList());
        this.images = course.getImages().stream().map(CourseImage::getName).collect(Collectors.toList());
    }
}
