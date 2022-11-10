package trying.cosmos.domain.course.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import trying.cosmos.domain.course.entity.Course;
import trying.cosmos.domain.course.entity.CourseReviewLike;
import trying.cosmos.domain.user.entity.User;

import java.util.Optional;

public interface CourseReviewLikeRepository extends JpaRepository<CourseReviewLike, Long> {

    boolean existsByUserIdAndCourseId(Long userId, Long courseId);

    Optional<CourseReviewLike> findByUserIdAndCourseId(Long userId, Long courseId);

    int countByUser(User user);

    @Query("select cl.course from CourseReviewLike cl where cl.user.id = :userId and cl.course.isDeleted = false")
    Slice<Course> searchCourseByUserId(Long userId, Pageable pageable);
}
