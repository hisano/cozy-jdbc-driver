# Cozy JDBC Driver for MySQL under Apache License

This is an experimental project to study JDBC and Kotlin.

## Features (under development)

* Supports SELECT command with ResultSet
* Supports Statement/PreparedStatement
* Supports auto commit switching
* Implemented in Kotlin
* Available under Apache License
* Tested with MySQL 8.0, 5.7, 5.6 and 5.5

## How to build

```sh
$ ./gradlew shadowJar
```

## How to test with MySQL version

```sh
$ ./gradlew test -DTARGET_VERSION=8.0.32
```
