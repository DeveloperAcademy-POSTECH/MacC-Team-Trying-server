package trying.cosmos.domain.place;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Place {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "place_id")
    private Long id;

    private Long placeNumber;

    private String name;

    private double latitude;

    private double longitude;

    // Constructor
    public Place(Long placeNumber, String name, double latitude, double longitude) {
        this.placeNumber = placeNumber;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public boolean isSame(String name, double latitude, double longitude) {
        return this.name.equals(name) && this.latitude == latitude && this.longitude == longitude;
    }
}
