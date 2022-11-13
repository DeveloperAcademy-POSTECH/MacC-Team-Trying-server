package trying.cosmos.global.utils;

import trying.cosmos.domain.place.entity.Coordinate;

import static java.lang.Math.*;

public interface DistanceUtils {

    double radius = 6371; // 지구 반지름(km)
    double toRadian = PI / 180;

    static double getDistance(Coordinate p1, Coordinate p2) {
        double deltaLatitude = abs(p1.getLatitude() - p2.getLatitude()) * toRadian;
        double deltaLongitude = abs(p1.getLongitude() - p2.getLongitude()) * toRadian;

        double sinDeltaLat = sin(deltaLatitude / 2);
        double sinDeltaLng = sin(deltaLongitude / 2);
        double squareRoot = sqrt(
                sinDeltaLat * sinDeltaLat +
                        cos(p1.getLatitude() * toRadian)
                                * cos(p2.getLatitude() * toRadian)
                                * sinDeltaLng
                                * sinDeltaLng);

        double distance = 2 * radius * asin(squareRoot);
        return format(distance);
    }

    static Double format(double value) {
        double d = pow(10, 3);
        return Math.round(value * d) / d;
    }
}
