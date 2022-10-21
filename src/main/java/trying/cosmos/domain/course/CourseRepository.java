package trying.cosmos.domain.course;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import trying.cosmos.domain.planet.Planet;

import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {

    @Query("select c from Course c join fetch c.tags t join fetch t.place where c.id = :courseId and c.isDeleted = false")
    Optional<Course> searchByIdWithTagPlace(Long courseId);

    @Query("select c from Course c where c.planet = :planet and c.planet.isDeleted = false and c.isDeleted = false")
    Slice<Course> searchAllByPlanet(Planet planet, Pageable pageable);

    @Query("select c from Course c " +
            "where c.planet = :planet " +
            "and c.access = trying.cosmos.domain.course.Access.PUBLIC " +
            "and c.planet.isDeleted = false " +
            "and c.isDeleted = false")
    Slice<Course> searchPublicByPlanet(Planet planet, Pageable pageable);

    // 모든 코스 조회, 내 행성의 코스인 경우 모든 코스, 다른 행성의 코스인 경우 PUBLIC 코스만
    @Query("select c from Course c " +
            "where (c.planet = :planet or c.access = trying.cosmos.domain.course.Access.PUBLIC)" +
            "and c.planet.isDeleted = false and c.isDeleted = false")
    Slice<Course> searchAll(Planet planet, Pageable pageable);

    @Modifying
    @Query("delete from Tag t where t.course = :course")
    void deleteCourseTag(Course course);

    @Modifying
    @Query("delete from CourseImage i where i.course = :course")
    void deleteCourseImage(Course course);
}
