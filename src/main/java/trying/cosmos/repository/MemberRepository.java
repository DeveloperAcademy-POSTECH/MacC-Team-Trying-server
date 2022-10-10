package trying.cosmos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import trying.cosmos.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
