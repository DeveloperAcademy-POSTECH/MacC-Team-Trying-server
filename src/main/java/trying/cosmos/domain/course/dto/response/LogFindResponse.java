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
public class LogFindResponse {

    private List<LogCourseFindResponse> courses;
    private int size;
    private boolean hasNext;

    public LogFindResponse(Slice<LogCourseFindResponse> courseSlice) {
        this.courses = courseSlice.getContent();
        this.size = courseSlice.getNumberOfElements();
        this.hasNext = courseSlice.hasNext();
    }
}
