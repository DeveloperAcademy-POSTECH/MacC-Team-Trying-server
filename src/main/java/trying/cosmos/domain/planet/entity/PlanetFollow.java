package trying.cosmos.domain.planet.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import trying.cosmos.domain.user.entity.User;

import javax.persistence.*;

@ToString
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlanetFollow {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "planet_follow_id")
    private Long id;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "planet_id")
    private Planet planet;

    public PlanetFollow(User user, Planet planet) {
        this.user = user;
        this.planet = planet;
    }
}
