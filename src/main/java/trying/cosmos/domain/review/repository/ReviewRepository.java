package trying.cosmos.domain.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import trying.cosmos.domain.review.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}
