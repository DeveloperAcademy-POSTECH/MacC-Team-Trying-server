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
    private Long id;

    private Long identifier;

    private String name;

    private String category;

    private Double longitude;

    private Double latitude;

    public Place(Long identifier, String name, String category, Double longitude, Double latitude) {
        this.identifier = identifier;
        this.name = name;
        this.category = category;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public boolean isSame(String name, String category, Double longitude, Double latitude) {
        return this.name.equals(name)
                && this.category.equals(category)
                && this.longitude.equals(longitude)
                && this.latitude.equals(latitude);
    }
}
