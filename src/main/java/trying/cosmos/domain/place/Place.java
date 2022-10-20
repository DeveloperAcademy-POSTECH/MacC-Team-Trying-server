package trying.cosmos.domain.place;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Place {

    @Id
    @Column(name = "place_id")
    private Long placeId;

    private String name;

    private double latitude;

    private double longitude;

    // Constructor
    public Place(Long placeId, String name, double latitude, double longitude) {
        this.placeId = placeId;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public boolean isSame(String name, double latitude, double longitude) {
        return this.name.equals(name) && this.latitude == latitude && this.longitude == longitude;
    }
}
