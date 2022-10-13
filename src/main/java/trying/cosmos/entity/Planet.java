package trying.cosmos.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import trying.cosmos.entity.component.CreatedDateEntity;
import trying.cosmos.entity.component.PlanetType;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Planet extends CreatedDateEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "planet_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    private User host;

    @OneToOne(fetch = FetchType.LAZY)
    private User guest;

    private String name;

    @Enumerated(EnumType.STRING)
    private PlanetType type;

    // Constructor
    public Planet(User host, String name, PlanetType type) {
        this.host = host;
        this.name = name;
        this.type = type;
    }

    // Convenience Method
    public void invite(User mate) {
        this.guest = mate;

    }
}
