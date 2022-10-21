package trying.cosmos.domain.course;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import trying.cosmos.domain.planet.Planet;

import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long>, CourseRepositoryCustom {

    @Query("select c from Course c join fetch c.tags t join fetch t.place " +
            "where c.id = :courseId " +
            "and c.planet.isDeleted = false " +
            "and c.isDeleted = false")
    Optional<Course> searchById(Long courseId);

    @Query("select c from Course c " +
            "where (c.planet = :myPlanet or c.access = trying.cosmos.domain.course.Access.PUBLIC) " +
            "and c.title like :query " +
            "and c.planet.isDeleted = false " +
            "and c.isDeleted = false " +
            "order by c.createdDate desc")
    Slice<Course> searchByName(Planet myPlanet, String query, Pageable pageable);

    @Query("select c from Course c " +
            "where (c.planet = :myPlanet or c.access = trying.cosmos.domain.course.Access.PUBLIC) " +
            "and c.planet = :planet " +
            "and c.isDeleted = false ")
    Slice<Course> searchByPlanet(Planet myPlanet, Planet planet, Pageable pageable);

    @Modifying
    @Query("delete from Tag t where t.course = :course")
    void deleteCourseTag(Course course);

    @Modifying
    @Query("delete from CourseImage i where i.course = :course")
    void deleteCourseImage(Course course);
}
