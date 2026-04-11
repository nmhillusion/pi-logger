# PiLogger Project Context

Lightweight, asynchronous Java logging library implementing the SLF4J 2.0.x API.

## Core Architecture
- **Asynchronous Execution:** Logging operations are offloaded to a dedicated single-threaded `ExecutorService` (`PiLogger-Thread`) to ensure non-blocking performance.
- **SLF4J Integration:** Implements `org.slf4j.Logger`, `ILoggerFactory`, and provides a `SLF4JServiceProvider` for automatic discovery via Service Loader.
- **Dynamic Configuration:** Uses `LogConfigModel` with a listener pattern to allow real-time updates (e.g., log level, coloring) without restarting the application.

## Key Components
- `PiLogger`: Main implementation handling message formatting and asynchronous dispatch.
- `PiLoggerFactory`: Manages logger instances and global default configuration.
- `LogConfigModel`: Holds state for coloring, timestamp patterns, log levels, and output destinations.
- `IOutputWriter`: Interface for log destinations. Currently supports:
    - `ConsoleOutputWriter`: Standard output with ANSI color support.
    - `FileOutputWriter`: Persistent logging with automatic ANSI color stripping.

## Features
- **ANSI Color Support:** Configurable colored console logs based on severity.
- **Line Number Tracking:** Optional display of the caller's line number in logs.
- **Environment Variable Support:** Resolves `%ENV_VAR%` placeholders in log file paths.
- **Minimal Dependencies:** Only requires the SLF4J API.

## Technical Standards
- **Runtime:** Java 17+.
- **Build System:** Maven.
- **Testing:** JUnit 6.
- **Pipeline:** Log Call -> Asynchronous Task -> Template Formatting -> Output Dispatch.
