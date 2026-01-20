# Test Planning Document (LO2)

## 1. Priority and Risk Analysis
Based on the system goals, the requirements are prioritised as follows:

*   **High Priority: REQ-SYS-02 (Region Check)**
    *   **Justification:** This is the foundational safety constraint. It is used not just for the `isInRegion` endpoint, but inherently to validate `nextPosition` moves. Failure here means the drone could illegally enter No-Fly Zones.
    *   **Testing Approach:** Rigorous **Partition Testing** (Inside/Outside/Boundary/Vertex) is required.
    *   **Risk:** Floating-point errors near boundaries (e.g., epsilon `1e-10` cases).
    *   **Mitigation:** The implementation uses a strict `CLOSE_DISTANCE_THRESHOLD` (0.00015). Tests must verify this exact threshold.

*   **Medium Priority: REQ-INT-02 (JSON Robustness)**
    *   **Justification:** Essential for service resilience but not safety-critical.
    *   **Testing Approach:** **Fault Injection** (malformed JSON, extra fields) using `MockMvc` integration tests.

## 2. Scaffolding and Instrumentation
To support this plan, I have implemented specific technical measures:

### Scaffolding (Test Helpers)
Constructing complex polygon JSONs manually is error-prone.
*   **Solution:** A `TestUtils` class.
*   **Function:** Generates valid/invalid `Region` objects and GeoJSON strings for testing.
*   **Evidence:** `src/test/java/uk/ac/ed/acp/cw1/lib/TestUtils.java`

### Instrumentation (Measurement & Tracing)
*   **Code Coverage (Jacoco):**
    *   **Purpose:** To verify that our "Partition Tests" actually execute all logic branches (especially the complex `isInRegion` ray-casting logic).
    *   **Implementation:** Added `jacoco-maven-plugin` to `pom.xml`.
    *   **Target:** >80% Branch Coverage.
*   **Request Logging:**
    *   **Purpose:** To trace request processing during integration tests, specifically to see why a 400 Bad Request occurs (e.g., "Missing field" vs "Invalid Geometry").
    *   **Implementation:** Added SLF4J logging to `ServiceController` endpoints.

## 3. Process Integration
*   **Lifecycle:** **Test-Driven Development (TDD) / Iterative**.
    *   **Workflow:** Tests are written alongside feature code. The `mvn test` lifecycle runs all unit and integration tests on every build.
    *   **Feedback Loop:** Immediate feedback via JUnit console output and Jacoco HTML reports (`target/site/jacoco/index.html`).
