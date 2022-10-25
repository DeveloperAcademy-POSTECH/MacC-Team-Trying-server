package trying.cosmos.domain.place.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@ToString
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Place {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "place_id")
    private Long placeId;

    private String name;

    @Embedded
    private Coordinate coordinate;

    // Constructor
    public Place(String name, double latitude, double longitude) {
        this.name = name;
        this.coordinate = new Coordinate(latitude, longitude);
    }
}
