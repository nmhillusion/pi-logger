# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Test Commands

```bash
# Build, test, and package (default Maven goal)
mvn

# Run all tests
mvn test

# Run a single test class
mvn test -Dtest=PiLoggerFactoryTest

# Compile only (skip tests and packaging)
mvn compile

# Package (skip tests)
mvn package -DskipTests

# Run a single test method
mvn test -Dtest=PiLoggerFactoryTest#testLogLevel
```

Java 17 is required. No linting tool is configured.

## Testing Notes

- Tests use JUnit Jupiter 6.0.3
- Surefire includes `**Test.java` patterns
- `PenLogTest` contains stress tests (1M messages, multi-threaded logging) — can be slow; skip with `-Dtest=!PenLogTest` if needed

## References

- **[ARCHITECTURE.md](ARCHITECTURE.md)** — High-level architecture, data flow pipeline, design patterns, concurrency model, and SLF4J integration details
- **[PROJECT_CONTEXT.md](PROJECT_CONTEXT.md)** — Core architecture summary, key components, features, and technical standards