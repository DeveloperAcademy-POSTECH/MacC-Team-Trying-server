package trying.cosmos.domain.place.dto.response;

public interface PlaceDistanceProjection {

    Long getPlaceId();
    String getName();
    String getCode();
    String getAddress();
    String getRoadAddress();
    Double getLatitude();
    Double getLongitude();
    Double getDistance();
}
