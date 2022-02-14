# pi-logger

PiLogger

- a simple logger by Java

---

# Usage

TDTR;

```java

PiLoggerHelper.getLog(this).info("hello, logger!!!");

```

## Level of logger

- PiLogger have 5 level of logging with increasing priority.

| priority | level name | example of usage |
| -------- | ---------- | ---------------- |
| 0        | `TRACE`    | logger.trace()   |
| 1        | `DEBUG`    | logger.debug()   |
| 2        | `INFO`     | logger.info()    |
| 3        | `WARN`     | logger.warn()    |
| 4        | `ERROR`    | logger.error()   |

## Config of logger

You can config logger via these properties:

| propertyType | propertyName      | default                   | note                                                              |
| ------------ | ----------------- | ------------------------- | ----------------------------------------------------------------- |
| boolean      | coloring          | false                     | add ansi-color to log                                             |
| String       | timestampPattern  | yyyy-MM-dd'T'HH:mm:ss.SSS | timestamp pattern of log                                          |
| boolean      | displayLineNumber | true                      | display line number of file of log was used                       |
| LogLevel     | logLevel          | LogLevel.INFO             | level to write log                                                |
| boolean      | outputToFile      | true                      | will write log to file or not                                     |
| String       | logFilePath       | "output.log"              | name or path of log file to write, able to use with env variables |

```java

PiLoggerFactory.getLogConfig()
        .setColoring(true)
        .setDisplayLineNumber(true)
        .setTimestampPattern("yyyy-MM-dd HH:mm:ss.SSS")
        .setLogLevel(LogLevel.ERROR)
        ;

```
