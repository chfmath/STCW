package uk.ac.ed.acp.cw1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URL;

import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.ed.acp.cw1.dto.*;
import uk.ac.ed.acp.cw1.service.LocationService;

/**
 * Controller class that handles various HTTP endpoints for the application.
 * Provides functionality for serving the index page, retrieving a static UUID,
 * and managing key-value pairs through POST requests.
 */
@RestController
@RequestMapping("/api/v1")
public class ServiceController {

    private static final Logger logger = LoggerFactory.getLogger(ServiceController.class);

    @Value("${ilp.service.url}")
    public URL serviceUrl;

    private final LocationService locationService;

    // Enables DI, basically lets service controller use location service without having to create
    @Autowired
    public ServiceController(LocationService locationService) {
        this.locationService = locationService;
    }

    @GetMapping("/")
    public String index() {
        return "<html><body>" +
                "<h1>Welcome from ILP</h1>" +
                "<h4>ILP-REST-Service-URL:</h4> <a href=\"" + serviceUrl +
                "\" target=\"_blank\"> " + serviceUrl + " </a>" +
                "</body></html>";
    }

    @GetMapping("/uid")
    public String uid() {
        return "s2574468";  // Replace with your student ID
    }

    @GetMapping("/demo")
    public String demo() {
        return "demo";
    }

    // @RequestBody maps the incoming JSON to the DTO class through Jackson lib
    @PostMapping("/distanceTo")
    public ResponseEntity<Double> distanceTo(@Valid @RequestBody DistanceRequest request) {
        double distance = locationService.calculateDistance(request.getPosition1(),
                request.getPosition2());
        return ResponseEntity.ok(distance);
    }

    @PostMapping("/isCloseTo")
    public ResponseEntity<Boolean> isCloseTo(@Valid @RequestBody DistanceRequest request) {
        boolean close = locationService.isCloseTo(request.getPosition1(), request.getPosition2());
        return ResponseEntity.ok(close);
    }

    @PostMapping("/nextPosition")
    public ResponseEntity<Position> nextPosition(@Valid @RequestBody NextPositionRequest request) {
        Position nextPos = locationService.nextPosition(request.getStart(), request.getAngle());
        return ResponseEntity.ok(nextPos);
    }

    @PostMapping("/isInRegion")
    public ResponseEntity<Boolean> isInRegion(@Valid @RequestBody RegionRequest request) {
        logger.info("Checking region: {} for point: {}", 
            request.getRegion().getName(), request.getPosition());
        boolean inside = locationService.isInRegion(request.getPosition(), request.getRegion().getVertices());
        return ResponseEntity.ok(inside);
    }
}
