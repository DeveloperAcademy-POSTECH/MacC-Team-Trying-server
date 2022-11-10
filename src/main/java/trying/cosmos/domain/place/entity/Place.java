package trying.cosmos.domain.place.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@ToString
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Place {

    @Id
    @Column(name = "place_id")
    private Long id;

    private String name;

    private String code;

    private String address;

    private String roadAddress;

    private Double latitude;

    private Double longitude;
}
