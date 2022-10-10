package trying.cosmos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import trying.cosmos.entity.Member;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);
}
