package trying.cosmos.domain.course.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Slice;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CourseListFindResponse {

    private List<CourseFindContent> courses;
    private int size;
    private boolean hasNext;

    public CourseListFindResponse(Slice<CourseFindContent> courseSlice) {
        this.courses = courseSlice.getContent();
        this.size = courseSlice.getNumberOfElements();
        this.hasNext = courseSlice.hasNext();
    }
}
