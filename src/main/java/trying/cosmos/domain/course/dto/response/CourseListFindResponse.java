package trying.cosmos.domain.course.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.domain.Slice;

import java.util.List;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CourseListFindResponse {

    private List<CourseFindResponse> courses;
    private int size;
    private boolean hasNext;

    public CourseListFindResponse(Slice<CourseFindResponse> courseSlice) {
        this.courses = courseSlice.getContent();
        this.size = courseSlice.getNumberOfElements();
        this.hasNext = courseSlice.hasNext();
    }
}
