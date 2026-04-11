# PiLogger Architecture Document

This document describes the high-level architecture and design principles of the `pi-logger` library.

## 1. Introduction
`PiLogger` is a lightweight, asynchronous logging framework for Java. It is designed to provide high-performance logging by offloading I/O operations to a background thread while maintaining a simple, SLF4J-compatible interface.

## 2. System Overview
The library follows a producer-consumer model where the application (producer) submits log tasks, and a dedicated background thread (consumer) processes and writes them to various outputs.

### High-Level Architecture Diagram
```text
[ Application ] --( SLF4J API )--> [ PiLogger ]
                                      |
                               ( Task Submission )
                                      v
                             [ ExecutorService ] (Single Thread)
                                      |
                               ( Task Execution )
                                      v
                             [ Message Formatting ]
                                      |
                               ( Dispatching )
                                      v
                        +-------------+-------------+
                        |                           |
             [ ConsoleOutputWriter ]      [ FileOutputWriter ]
```

## 3. Core Components

### 3.1. PiLogger
The central class that implements `org.slf4j.Logger`. It handles:
- Capturing caller context (e.g., line numbers).
- Submitting logging tasks to the `ExecutorService`.
- Formatting log messages based on a shared template.

### 3.2. PiLoggerFactory
Implements `org.slf4j.ILoggerFactory`. It manages a registry of logger instances (using a `ConcurrentHashMap`) and provides access to the global default configuration.

### 3.3. LogConfigModel
A mutable configuration object that defines:
- Log level (TRACE, DEBUG, INFO, WARN, ERROR).
- Output settings (coloring, file output, timestamp patterns).
- Dynamic behavior (via `OnChangeConfig` listeners).

### 3.4. Output Writers (`IOutputWriter`)
Strategy-based components responsible for the final delivery of log messages.
- **ConsoleOutputWriter:** Directs logs to `System.out`.
- **FileOutputWriter:** Handles persistent storage, including directory creation and ANSI code stripping.

## 4. Logging Pipeline (Data Flow)

1.  **Call:** Application calls `logger.info("message")`.
2.  **Capture:** `PiLogger` captures the current thread and stack trace (if `displayLineNumber` is enabled).
3.  **Submit:** A task is wrapped and submitted to the `PiLogger-Thread` executor.
4.  **Filter:** On the background thread, the task checks the current `logLevel` priority.
5.  **Format:** The message is constructed using the template: `TIMESTAMP -- [LEVEL] -- [THREAD] -- [PID] -- ClassName:LineNumber : MESSAGE`.
6.  **Write:** The formatted message is dispatched to all active `IOutputWriter` implementations.

## 5. Configuration Management

### Dynamic Updates
`PiLogger` uses a reactive configuration approach. When `LogConfigModel` is modified (e.g., via `setLogLevel`), it triggers an `OnChangeConfig` event. The associated `PiLogger` instance listens for this event and immediately updates its internal state (template selection, output writer list), allowing for real-time configuration changes without a restart.

### Environment Variable Substitution
The `logFilePath` property supports environment variable placeholders (e.g., `%TEMP%/app.log`). These are resolved during configuration initialization in `LogConfigModel`.

## 6. Design Patterns
- **Singleton/Factory:** `PiLoggerFactory` manages the lifecycle of loggers.
- **Strategy:** `IOutputWriter` allows for interchangeable output destinations.
- **Observer:** `OnChangeConfig` facilitates dynamic configuration updates.
- **Template Method/Pattern:** Logging templates define the structural layout of log lines.

## 7. Concurrency Model
- **Single-Threaded Executor:** A dedicated daemon thread (`PiLogger-Thread`) handles all I/O. This prevents log contention and ensures that logging does not block the main application execution.
- **Atomic References:** Used for thread-safe access to volatile templates and configurations.
- **Synchronized Blocks:** Minimal synchronization is used in `PiLogger` during configuration updates to ensure consistency between writers and templates.

## 8. SLF4J Integration
The library integrates with SLF4J 2.0.x using the `SLF4JServiceProvider` mechanism.
- `PiLoggerServiceProvider`: Discovered via `META-INF/services/org.slf4j.spi.SLF4JServiceProvider`.
- Binds `LoggerFactory` calls directly to `PiLoggerFactory`.
