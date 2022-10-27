package trying.cosmos.domain.planet.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import trying.cosmos.domain.course.entity.Course;
import trying.cosmos.domain.place.entity.Coordinate;
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
    private List<Coordinate> coordinates ;

//    앱에서 위도, 경도를 이용해 변환
//    private List<Star> stars;

    public PlanetCourseContent(Course course) {
        this.courseId = course.getId();
        this.createdDate = DateUtils.getFormattedDate(course.getCreatedDate());
        this.title = course.getTitle();
        this.coordinates = course.getTags().stream()
                .map(tag -> tag.getPlace().getCoordinate())
                .collect(Collectors.toList());
//        this.stars = StarSignGenerator.generate(course.getTags().stream()
//                .map(tag -> tag.getPlace().getCoordinate())
//                .collect(Collectors.toList()));
    }
}
