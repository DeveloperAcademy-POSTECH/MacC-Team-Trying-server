package trying.cosmos.domain.course.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import trying.cosmos.domain.course.entity.Course;
import trying.cosmos.domain.course.entity.CourseLike;
import trying.cosmos.domain.user.entity.User;

import java.util.Optional;

public interface CourseLikeRepository extends JpaRepository<CourseLike, Long> {

    boolean existsByUserIdAndCourseId(Long userId, Long courseId);

    Optional<CourseLike> findByUserIdAndCourseId(Long userId, Long courseId);

    int countByUser(User user);

    @Query("select cl.course from CourseLike cl where cl.user.id = :userId and cl.course.isDeleted = false")
    Slice<Course> searchCourseByUserId(Long userId, Pageable pageable);
}
