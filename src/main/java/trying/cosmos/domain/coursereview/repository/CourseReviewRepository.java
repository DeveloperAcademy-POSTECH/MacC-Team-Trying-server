package trying.cosmos.domain.coursereview.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import trying.cosmos.domain.coursereview.entity.CourseReview;

public interface CourseReviewRepository extends JpaRepository<CourseReview, Long> {
}
