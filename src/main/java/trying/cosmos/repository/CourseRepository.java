package trying.cosmos.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import trying.cosmos.entity.Course;
import trying.cosmos.entity.Planet;

import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long>{

    @Query("select c from Course c join fetch c.tags t join fetch t.place where c.id = :id")
    Optional<Course> findByIdWithTagPlace(Long id);

    Slice<Course> findAllByPlanet(Planet planet, Pageable pageable);

    @Query("select c from Course c where c.planet = :planet and c.access = trying.cosmos.entity.component.Access.PUBLIC")
    Slice<Course> findPublicByPlanet(Planet planet, Pageable pageable);
}
