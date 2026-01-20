# Software Requirements Analysis (ILPCW1)

## 1. Functional Requirements (System Level)

* **REQ-SYS-01 (Distance Calculation):**
The system MUST accept two geographic coordinates (latitude/longitude) via the `/apiv1/distanceTo` endpoint and return the Euclidean distance between them as a floating-point value.
* **REQ-SYS-02 (Region Check):**
The system MUST verify if a specific point lies strictly inside or on the boundary of a provided polygonal region via the `/apiv1/isInRegion` endpoint.
* **REQ-SYS-03 (Next Position Calculation):**
Given a starting point and a valid angle, the system MUST calculate the correct coordinates of the next position via the `/apiv1/nextPosition` endpoint.
* **REQ-SYS-04 (Closeness Check):**
The system MUST return `true` via `/apiv1/isCloseTo` if two points are within a close distance threshold of 0.00015 degrees, and `false` otherwise.


## 2. Functional Requirements (Unit Level \& Robustness)

* **REQ-UNIT-01 (Polygon Closure Validation):**
The system MUST validate that the list of vertices provided for a region forms a closed polygon (i.e., the first and last vertices must be identical). If the polygon is "open", the system must treat the data as invalid.
* **REQ-UNIT-02 (JSON Parsing Robustness):**
The system MUST accept valid JSON payloads that contain extraneous/unexpected fields. It must ignore these extra fields and process the valid data.
* **REQ-UNIT-03 (Syntactic Validation):**
The system MUST distinguish between semantically valid and invalid requests. If data is missing, malformed, or logically invalid (e.g. open polygon), the system must return an HTTP 400 (Bad Request) status code instead of a 500 Internal Server Error.


## 3. Non-Functional Requirements (Quality Attributes)

* **REQ-NFR-01 (Interface Compliance):**
The service MUST adhere to endpoint naming convention, including the `/apiv1/` prefix for all operational endpoints and the exact JSON structure for responses as provided in the specification.
* **REQ-NFR-02 (Livliness/Health):**
The system MUST provide a dedicated `/actuator/health` endpoint that returns a status of "UP" to confirm service availability.