# Tool_Shop_API_Automation

Overview
- A hands-on practice project for API test automation of a Tool Shop application.
- The goal is to learn and practice writing automated API tests using common Java tools and running them with Maven/TestNG.

Technologies
- Language: Java
- Build & Dependency Management: Maven (`pom.xml`)
- Test Framework: TestNG (`testng.xml`)
- Expected libraries: Rest-Assured (or any HTTP testing library), JSON mapper (Jackson/Gson), logging libraries, and Maven plugins for reports.

Repository structure (as present in this repository)
- [`pom.xml`](https://github.com/esraaatya557/Tool_Shop_API_Automation/blob/master/pom.xml) — Maven project configuration and dependencies.
- [`testng.xml`](https://github.com/esraaatya557/Tool_Shop_API_Automation/blob/master/testng.xml) — TestNG suite configuration.
- `src/` — Source and test code (expected `src/test/java` and/or `src/main/java`).
- `config/` — Configuration files (e.g., `config.properties` or environment files).
- `bin/`, `.idea/`, and `.gitignore` — IDE and project auxiliary files.

Prerequisites
- JDK 11+ .
- Maven installed and available in PATH.
- Internet access to download dependencies on first run.
- (Optional) IDE such as IntelliJ IDEA or Eclipse for running TestNG suites from the IDE.

Configuration
- Place environment-specific settings or credentials in a configuration file inside `config/` (for example, `config/config.properties`) or use environment variables.
- Typical configuration values:
  - BASE_URL (https://api-with-bugs.practicesoftwaretesting.com)
- Update code/config files as needed to point to the correct API endpoints and credentials.

How to run
- From the project root, run all tests with Maven:
  - mvn clean test
  - If Maven Surefire (or Failsafe) is configured to use `testng.xml`, it will run the TestNG suite automatically. Otherwise run `testng.xml` from your IDE.
- Running TestNG from an IDE:
  - Open `testng.xml` and run it using your IDE's TestNG integration.
- To run specific tests or suites, adjust `testng.xml` or use TestNG/Maven options as configured in `pom.xml`.

Practical notes
- Check `pom.xml` for the actual project dependencies (add Rest-Assured, TestNG, logging, and reporting libs if missing).
- Store test data and JSON samples in `src/test/resources`.
- Consider adding reporting tools (Allure, Surefire reports) for better result visualization.

Contributing
- Open an issue or create a pull request describing your change or enhancement.
- Follow existing project structure and naming conventions when adding tests or helper classes.

Author
- Esraa Atya(https://www.linkedin.com/in/esraa-atya)
