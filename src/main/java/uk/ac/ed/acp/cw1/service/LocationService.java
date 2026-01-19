package uk.ac.ed.acp.cw1.service;

import org.springframework.stereotype.Service;
import uk.ac.ed.acp.cw1.dto.Position;

@Service
public class LocationService {

    private static final double CLOSE_DISTANCE_THRESHOLD = 0.00015;

    /**
     * Calculate the straight-line (Euclidean) distance between two positions.
     *
     * <p>The method treats the {@code lat} and {@code lng} values of {@code Position}
     * as coordinates and returns the Euclidean distance between them. The curvature of the earth
     * is not considered.
     *
     * @param p1 the first position (must not be {@code null})
     * @param p2 the second position (must not be {@code null})
     * @return the Euclidean distance between {@code p1} and {@code p2}
     */
    public double calculateDistance(Position p1, Position p2) {
        double lngDiff = p2.getLng() - p1.getLng();
        double latDiff = p2.getLat() - p1.getLat();
        return Math.sqrt(lngDiff * lngDiff + latDiff * latDiff);
    }

    /**
     * Determine whether two positions are considered close to each other.
     *
     * <p>Two positions are "close" if their Euclidean distance is less than or
     * equal to the configured {@link #CLOSE_DISTANCE_THRESHOLD}.
     *
     * @param p1 the first position (must not be {@code null})
     * @param p2 the second position (must not be {@code null})
     * @return {@code true} if the positions are within the close-distance threshold,
     *         {@code false} otherwise
     */
    public boolean isCloseTo(Position p1, Position p2) {
        return calculateDistance(p1, p2) <= CLOSE_DISTANCE_THRESHOLD;
    }

    /**
     * Compute the next position when moving from a starting position by a fixed
     * step equal to {@link #CLOSE_DISTANCE_THRESHOLD} in the direction specified
     * by {@code angleDegrees}.
     *
     * <p>Angle convention: 0 degrees corresponds to movement along the positive
     * longitude axis (east), 90 degrees corresponds to movement along the positive
     * latitude axis (north) etc.
     *
     * @param start the starting position (must not be {@code null})
     * @param angleDegrees movement angle in degrees where 0 = east, 90 = north etc.
     * @return a new {@code Position} representing the computed next location
     */
    public Position nextPosition(Position start, double angleDegrees) {
        double distance = CLOSE_DISTANCE_THRESHOLD;
        double angleRadians = Math.toRadians(angleDegrees);

        double newLng = start.getLng() + distance * Math.cos(angleRadians);
        double newLat = start.getLat() + distance * Math.sin(angleRadians);

        return new Position(newLng, newLat);
    }

    /**
     * Determine whether a given point lies inside a polygon defined by an array
     * of vertices. Points on the polygon boundary are considered inside.
     *
     * <p>Uses the even-odd algorithm to determine whether the point is inside the polygon.
     * Points on or within {@link #CLOSE_DISTANCE_THRESHOLD} of the polygon boundary
     * are considered inside. This is due to possible uncertainty through
     * usage of Double rather than BigDecimal.
     *
     * @param point the point to test (must not be {@code null})
     * @param polygonVertices array of polygon vertices in order (must not be {@code null} or empty)
     * @return {@code true} if {@code point} is inside the polygon or on its boundary,
     *         {@code false} otherwise
     */
    public boolean isInRegion(Position point, Position[] polygonVertices) {
        if (isPointOnBoundary(point, polygonVertices)) {
            return true; // Boundary is inside (as on spec)
        }

        int crossingCount = 0;
        int vertexCount = polygonVertices.length;
        double pointX = point.getLng();
        double pointY = point.getLat();

        for (int current = 0, previous = vertexCount - 1; current < vertexCount; previous = current++) {
            double currentX = polygonVertices[current].getLng();
            double currentY = polygonVertices[current].getLat();
            double previousX = polygonVertices[previous].getLng();
            double previousY = polygonVertices[previous].getLat();

            boolean edgeCrossed = ((currentY > pointY) != (previousY > pointY)) &&
                    (pointX < (previousX - currentX) * (pointY - currentY) /
                            (previousY - currentY) + currentX);

            if (edgeCrossed) {
                crossingCount++;
            }
        }

        return (crossingCount % 2) == 1;
    }

    private boolean isPointOnBoundary(Position point, Position[] polygonVertices) {
        for (Position vertex : polygonVertices) {
            if (isCloseTo(point, vertex)) {
                return true;
            }
        }

        int vertexCount = polygonVertices.length;
        for (int startIdx = 0, endIdx = vertexCount - 1; startIdx < vertexCount; endIdx = startIdx++) {
            Position start = polygonVertices[startIdx];
            Position end = polygonVertices[endIdx];

            double pointX = point.getLng();
            double pointY = point.getLat();
            double startX = start.getLng();
            double startY = start.getLat();
            double endX = end.getLng();
            double endY = end.getLat();

            if (!(pointY >= Math.min(startY, endY) &&
                    pointY <= Math.max(startY, endY) &&
                    pointX >= Math.min(startX, endX) &&
                    pointX <= Math.max(startX, endX))) {
                continue;
            }

            double lineLength = calculateDistance(start, end);
            if (lineLength < CLOSE_DISTANCE_THRESHOLD) {
                continue;
            }

            double distance = Math.abs((endY - startY) * pointX -
                    (endX - startX) * pointY +
                    endX * startY - endY * startX) / lineLength;

            if (distance < CLOSE_DISTANCE_THRESHOLD) {
                return true;
            }
        }

        return false;
    }

}
