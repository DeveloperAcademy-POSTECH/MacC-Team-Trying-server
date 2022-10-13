package trying.cosmos.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FollowPlanet {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "follow_planet_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "planet_id")
    private Planet planet;

    // Constructor
    public FollowPlanet(User user, Planet planet) {
        this.user = user;
        this.planet = planet;
    }
}
