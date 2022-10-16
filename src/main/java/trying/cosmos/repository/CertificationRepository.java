package trying.cosmos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import trying.cosmos.entity.Certification;

import java.time.LocalDateTime;
import java.util.Optional;

public interface CertificationRepository extends JpaRepository<Certification, Long> {

    Optional<Certification> findByEmail(String email);

    boolean existsByEmail(String email);

    @Modifying
    @Query("delete from Certification c where c.expiredDate < :now and c.isCertified = false")
    void clearCertifications(LocalDateTime now);
}
