# Test Planning Document (LO2)

This document outlines the test plan, focusing on the key requirements identified in the requirements analysis (LO1).

## 1. Priority and Risk Analysis

* **REQ-SYS-02 (Region Check) R1:**
High priority. This is a critical property ensuring drones do not fly into restricted zones. This has both safety (hazards in areas) and compliance (no-fly-zones) implication
    *   **Technique:** Category-partition testing. The input space divides into three partitions: inside, outside, and boundary.
    *   **Requirements:** We must generate complex polygons to test these partitions reliably, as manual JSON construction is error-prone.
    *   **Verification:** The ray-casting algorithm must be verified in isolation before integration.

* **REQ-INT-02 (JSON Robustness) R2:**
Medium priority. Essential for service resilience but not safety-critical as with REQ-SYS-02.
    *   **Technique:** Negative testing (system level). We will send malformed JSON payloads to ensure the system rejects them gracefully with an HTTP 400 status.
    *   **Requirements:** Requires a running Spring context (`MockMvc`).

## 2. Scaffolding and Instrumentation

To support the testing priorities, specific technical infrastructure has been implemented:

* **Scaffolding (`TestUtils`):**
A factory class (`src/test/java/uk/ac/ed/acp/cw1/lib/TestUtils.java`) has been created to generate valid and invalid `Region` objects. This mitigates the risk of inconsistencies in tests that comes from manually typing complex coordinate arrays for each test case.

* **Instrumentation (Jacoco):**
Code coverage analysis has been added to the build pipeline with `jacoco`. This is used to verify that our partition tests actually execute all branches of the ray-casting logic. If coverage is < 80%, the partitions are deemed insufficient.

* **Instrumentation (Diagnostic Logging):**
SLF4J logging has been added to the `ServiceController` for the method under test. Since `MockMvc` tests are "black-box," this logging allows us to trace the specific `lat/lng` values of failed requests, providing visibility into boundary edge case handling.

## 3. Process and Lifecycle

* **Phase 1 (Unit Logic):**
`LocationService` logic is developed using TDD. The scaffolding must be built before the complex boundary tests to ensure test data validity.

* **Phase 2 (Integration):**
Once the geometric logic is proven, `ServiceController` tests verify the HTTP layer and JSON parsing.

* **Known Risks:**
    *   **Floating Point Precision:** The `Region` check relies on `double`. There is a risk that "boundary" points might technically fall "outside" due to precision errors. We mitigate this by using a strict `CLOSE_DISTANCE_THRESHOLD` constant (0.00015) rather than a magic number.
    *   **DTO Testing Gap:** The plan prioritizes `LocationService` logic over trivial getter/setter testing. We accept the risk that some DTO annotations may be under-tested to focus effort on safety-critical logic.
