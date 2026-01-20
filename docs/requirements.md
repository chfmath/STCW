# Software Requirements Analysis (ILPCW1)

## 1. System Level Requirements

* **REQ-SYS-01 (Distance Calculation):**
The system MUST accept two geographic coordinates (latitude/longitude) via the `/apiv1/distanceTo` endpoint and return the Euclidean distance between them as a floating-point value.
* **REQ-SYS-02 (Region Check):**
The system MUST verify if a specific point lies strictly inside or on the boundary of a provided polygonal region via the `/apiv1/isInRegion` endpoint.
* **REQ-SYS-03 (Next Position Calculation):**
Given a starting point and a valid angle, the system MUST calculate the correct coordinates of the next position via the `/apiv1/nextPosition` endpoint.
* **REQ-SYS-04 (Closeness Check):**
The system MUST treat two points as effectively identical if they are within a distance of 0.00015 degrees. The `/apiv1/isCloseTo` endpoint must return `true` for points within this threshold and `false` otherwise.

## 2. Integration Level Requirements

* **REQ-INT-01 (HTTP Status Contracts):**
The system MUST correctly map internal validation results to HTTP status codes. Valid requests must return `200 OK`, while requests with missing data, malformed JSON, or logically invalid data (e.g., open polygons) must return `400 Bad Request`.
* **REQ-INT-02 (JSON Robustness/Mapping):**
The Controller layer MUST correctly map JSON inputs to service objects, ensuring that valid payloads containing unexpected fields are accepted (ignoring the extra data) rather than rejected.
* **REQ-INT-03 (Endpoint Routing):**
All functional endpoints MUST be reachable strictly under the `/apiv1/` prefix, while the health check MUST be reachable at `/actuator/health` (without the prefix), verifying the routing configuration is correct.

## 3. Unit Level Requirements

* **REQ-UNIT-01 (Polygon Closure Validation):**
The validation logic MUST ensure that the list of vertices provided for a region forms a closed polygon (the first and last vertices are identical).
* **REQ-UNIT-02 (Threshold Logic):**
The internal comparison logic MUST correctly classify points separated by less than or equal to 0.00015 degrees as "close" to account for minor precision loss.


## 4. Non-Functional Requirements (Quality Attributes)

* **REQ-NFR-01 (Availability/Health):**
The system MUST provide a dedicated `/actuator/health` endpoint that returns a status of "UP" to confirm service availability.
* **REQ-NFR-02 (Performance/Latency):**
The system MUST respond to valid geometric calculation requests within 500ms under normal operating conditions.
