package trying.cosmos.domain.place.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;

@ToString
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Place {

    @Id
    @Column(name = "place_id")
    private Long placeId;

    private String name;

    @Embedded
    private Coordinate coordinate;

    // Constructor
    public Place(Long placeId, String name, double latitude, double longitude) {
        this.placeId = placeId;
        this.name = name;
        this.coordinate = new Coordinate(latitude, longitude);
    }
}
