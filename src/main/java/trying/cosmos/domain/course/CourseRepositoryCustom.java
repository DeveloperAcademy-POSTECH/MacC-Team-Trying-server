package trying.cosmos.domain.course;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import trying.cosmos.domain.user.User;

public interface CourseRepositoryCustom {

    Slice<Course> getFeed(User user, Pageable pageable);
}
