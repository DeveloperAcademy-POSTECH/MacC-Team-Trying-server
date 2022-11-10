package trying.cosmos.domain.course.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import trying.cosmos.domain.course.entity.Course;
import trying.cosmos.global.utils.date.DateUtils;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CourseFindResponse {

    private String title;
    private String date;
    private String createdDate;
    private boolean liked;

    public CourseFindResponse(Course course, boolean liked) {
        this.title = course.getTitle();
        this.date = DateUtils.getFormattedDate(course.getDate());
        this.createdDate = DateUtils.getFormattedDate(course.getCreatedDate());
        this.liked = liked;
    }
}
