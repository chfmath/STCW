package uk.ac.ed.acp.cw1.lib;

import uk.ac.ed.acp.cw1.dto.Position;
import uk.ac.ed.acp.cw1.dto.Region;

/**
 * Scaffolding class to generate test data (LO2).
 */
public class TestUtils {

    /**
     * Generates a simple square region for testing.
     * @param name Name of the region
     * @param size Length of side
     * @return A valid closed Region object
     */
    public static Region createSquareRegion(String name, double size) {
        Position[] vertices = {
            new Position(0.0, 0.0),
            new Position(size, 0.0),
            new Position(size, size),
            new Position(0.0, size),
            new Position(0.0, 0.0) // Closed
        };
        return new Region(name, vertices);
    }

    /**
     * Generates a region with an unclosed polygon (invalid).
     */
    public static Region createOpenPolygon(String name) {
        Position[] vertices = {
            new Position(0.0, 0.0),
            new Position(1.0, 0.0),
            new Position(1.0, 1.0)
            // Missing closure to (0,0)
        };
        return new Region(name, vertices);
    }
}
