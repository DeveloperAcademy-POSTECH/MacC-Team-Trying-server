package trying.cosmos.domain.course;

import trying.cosmos.domain.place.Coordinate;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

// Fixme: 코드 다듬기
public class StarSignGenerator {

    public static List<Star> generate(List<Coordinate> coordinates) {
        Double minLatitude = Collections.min(coordinates.stream().map(Coordinate::getLatitude).collect(Collectors.toList()));
        Double maxLatitude = Collections.max(coordinates.stream().map(Coordinate::getLatitude).collect(Collectors.toList()));
        Double minLongitude = Collections.min(coordinates.stream().map(Coordinate::getLongitude).collect(Collectors.toList()));
        Double maxLongitude = Collections.max(coordinates.stream().map(Coordinate::getLongitude).collect(Collectors.toList()));

        if (equal(maxLatitude, minLatitude) && equal(maxLongitude, minLongitude)) {
            return List.of(new Star(0.5, 0.5));
        }

        if (maxLatitude - minLatitude > maxLongitude - minLongitude) {
            double length = maxLatitude - minLatitude;
            double offset = ((maxLatitude - minLatitude) - (maxLongitude - minLongitude)) / 2;

            return coordinates.stream().map(coordinate ->
                    new Star(
                            (coordinate.getLongitude() - minLongitude + offset) / length,
                            1 - (coordinate.getLatitude() - minLatitude) / length
                    )
            ).collect(Collectors.toList());
        } else {
            double length = maxLongitude - minLongitude;
            double offset = ((maxLongitude - minLongitude) - (maxLatitude - minLatitude)) / 2;

            return coordinates.stream().map(coordinate ->
                    new Star(
                            (coordinate.getLongitude() - minLongitude) / length,
                            (coordinate.getLatitude() - minLatitude + offset) / length
                    )
            ).collect(Collectors.toList());
        }
    }

    private static boolean equal(Double maxLatitude, Double minLatitude) {
        return maxLatitude - minLatitude < 0.0000001;
    }
}
