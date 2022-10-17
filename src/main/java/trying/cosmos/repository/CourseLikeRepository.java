package trying.cosmos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import trying.cosmos.entity.component.CourseLike;

import java.util.Optional;

public interface CourseLikeRepository extends JpaRepository<CourseLike, Long> {

    boolean existsByUserIdAndCourseId(Long userId, Long courseId);

    Optional<CourseLike> findByUserIdAndCourseId(Long userId, Long courseId);
}
