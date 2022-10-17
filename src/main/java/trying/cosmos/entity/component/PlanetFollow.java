package trying.cosmos.entity.component;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import trying.cosmos.entity.Planet;
import trying.cosmos.entity.User;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlanetFollow {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "planet_follow_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "planet_id")
    private Planet planet;

    public PlanetFollow(User user, Planet planet) {
        this.user = user;
        this.planet = planet;
    }
}
