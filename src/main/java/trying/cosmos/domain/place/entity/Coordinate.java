package trying.cosmos.domain.place.entity;

import lombok.*;

import javax.persistence.Embeddable;

@ToString
@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Coordinate {

    private double latitude; // y
    private double longitude; // x
}
