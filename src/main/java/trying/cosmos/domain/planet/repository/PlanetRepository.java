package trying.cosmos.domain.planet.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import trying.cosmos.domain.planet.entity.Planet;

import java.util.Optional;

public interface PlanetRepository extends JpaRepository<Planet, Long> {

    // searchById: soft delete된 planet은 제외하고 표시
    @Query("select p from Planet p where p.id = :planetId and p.isDeleted = false")
    Optional<Planet> searchById(Long planetId);

    @Query("select p from Planet p where p.inviteCode = :code and p.isDeleted = false")
    Optional<Planet> searchByInviteCode(String code);

    @Query("select p from Planet p where p.name like :query and p.isDeleted = false")
    Slice<Planet> searchByName(String query, Pageable pageable);

    @Query("select pf.planet from PlanetFollow pf where pf.user.id = :userId and pf.planet.isDeleted = false")
    Slice<Planet> getFollowPlanets(Long userId, Pageable pageable);
}
