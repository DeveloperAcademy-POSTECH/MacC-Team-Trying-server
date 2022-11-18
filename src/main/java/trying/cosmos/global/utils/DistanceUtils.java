package trying.cosmos.global.utils;

import trying.cosmos.domain.place.entity.Coordinate;

public interface DistanceUtils {

    double radius = 6371; // 지구 반지름(km)
    double toRadian = Math.PI / 180;

    static double getDistance(Coordinate p1, Coordinate p2) {
        double deltaLatitude = Math.abs(p1.getLatitude() - p2.getLatitude()) * toRadian;
        double deltaLongitude = Math.abs(p1.getLongitude() - p2.getLongitude()) * toRadian;

        double sinDeltaLat = Math.sin(deltaLatitude / 2);
        double sinDeltaLng = Math.sin(deltaLongitude / 2);
        double squareRoot = Math.sqrt(
                sinDeltaLat * sinDeltaLat +
                        Math.cos(p1.getLatitude() * toRadian)
                                * Math.cos(p2.getLatitude() * toRadian)
                                * sinDeltaLng
                                * sinDeltaLng);

        double distance = 2 * radius * Math.asin(squareRoot);
        return format(distance);
    }

    static Double format(double value) {
        double d = Math.pow(10, 3);
        return Math.round(value * d) / d;
    }
}
