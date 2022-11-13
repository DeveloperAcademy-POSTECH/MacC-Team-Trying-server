package trying.cosmos.domain.course.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import trying.cosmos.domain.course.entity.Course;
import trying.cosmos.domain.user.entity.User;

public interface CourseRepositoryCustom {

    Slice<Course> getLogs(User user, Pageable pageable);
}
