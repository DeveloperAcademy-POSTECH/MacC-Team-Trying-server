package trying.cosmos.domain.course.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import trying.cosmos.domain.course.entity.Course;
import trying.cosmos.domain.course.entity.CoursePlace;
import trying.cosmos.global.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CourseFindResponse {

    private Long courseId;
    private String title;
    private String date;
    private boolean liked;
    private List<CoursePlaceResponse> places;

    public CourseFindResponse(Course course, boolean liked) {
        this.courseId = course.getId();
        this.title = course.getTitle();
        this.date = DateUtils.getFormattedDate(course.getDate());
        this.liked = liked;
        this.places = getPlaceWithDistance(course.getPlaces());
    }

    private List<CoursePlaceResponse> getPlaceWithDistance(List<CoursePlace> places) {
        int length = places.size();
        if (length == 0) {
            return new ArrayList<>();
        }

        List<CoursePlaceResponse> response = new ArrayList<>();
        for (int i = 0; i < length - 1; i++) {
            response.add(new CoursePlaceResponse(places.get(i), places.get(i + 1)));
        }
        response.add(new CoursePlaceResponse(places.get(length - 1), null));
        return response;
    }
}
