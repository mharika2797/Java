# Maven (pom.xml) vs Gradle — Build Tools Comparison

## Overview

| Feature | Maven | Gradle |
|---|---|---|
| Config file | `pom.xml` | `build.gradle` (Groovy) or `build.gradle.kts` (Kotlin) |
| Language | XML | Groovy DSL or Kotlin DSL |
| Performance | Slower (no incremental builds by default) | Faster (incremental builds + build cache) |
| Learning curve | Moderate (verbose XML) | Steeper initially, but more flexible |
| Convention | Strong convention over configuration | Flexible, less opinionated |
| Dependency management | `<dependencies>` block in XML | `dependencies {}` block in Groovy/Kotlin |

---

## Maven — pom.xml

Maven uses a Project Object Model (POM) defined in `pom.xml`. It follows a strict lifecycle and is highly conventional.

### Sample pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
             http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.example</groupId>
    <artifactId>my-spring-app</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
    </parent>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

### Maven Build Lifecycle Phases

Maven has a fixed lifecycle. Each phase runs all prior phases automatically.

```
validate → compile → test → package → verify → install → deploy
```

### Common Maven Commands

| Command | Description |
|---|---|
| `mvn clean` | Deletes the `target/` directory |
| `mvn compile` | Compiles source code |
| `mvn test` | Runs unit tests |
| `mvn package` | Packages compiled code into a JAR/WAR |
| `mvn install` | Installs the artifact into the local Maven repo (`~/.m2`) |
| `mvn deploy` | Deploys the artifact to a remote repository |
| `mvn clean install` | Clean then build and install — most common full build |
| `mvn clean package` | Clean then package into JAR/WAR |
| `mvn spring-boot:run` | Run a Spring Boot application |
| `mvn dependency:tree` | Print the full dependency tree |
| `mvn -DskipTests package` | Package without running tests |
| `mvn versions:display-dependency-updates` | Show available dependency version upgrades |

---

## Gradle — build.gradle

Gradle uses a Groovy or Kotlin DSL script instead of XML. It supports incremental builds and a build cache, making it significantly faster for large projects.

### Sample build.gradle (Groovy DSL)

```groovy
plugins {
    id 'org.springframework.boot' version '3.2.0'
    id 'io.spring.dependency-management' version '1.1.4'
    id 'java'
}

group = 'com.example'
version = '1.0.0'
sourceCompatibility = '17'

repositories {
    mavenCentral()
}

dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-web'
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
}

test {
    useJUnitPlatform()
}
```

### Sample build.gradle.kts (Kotlin DSL)

```kotlin
plugins {
    id("org.springframework.boot") version "3.2.0"
    id("io.spring.dependency-management") version "1.1.4"
    kotlin("jvm") version "1.9.22"
    kotlin("plugin.spring") version "1.9.22"
}

group = "com.example"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
```

### Gradle Dependency Configurations

| Configuration | Maven Equivalent | Description |
|---|---|---|
| `implementation` | `compile` | Compile + runtime, not exposed to consumers |
| `api` | `compile` (with exposure) | Exposed to dependents (library projects) |
| `compileOnly` | `provided` | Compile time only, not packaged |
| `runtimeOnly` | `runtime` | Runtime only, not compile time |
| `testImplementation` | `test` scope | Test compile + runtime |

### Common Gradle Commands

| Command | Description |
|---|---|
| `./gradlew clean` | Deletes the `build/` directory |
| `./gradlew build` | Compiles, tests, and packages the project |
| `./gradlew test` | Runs unit tests |
| `./gradlew bootRun` | Run a Spring Boot application |
| `./gradlew bootJar` | Build an executable Spring Boot JAR |
| `./gradlew dependencies` | Print the full dependency tree |
| `./gradlew tasks` | List all available tasks |
| `./gradlew build -x test` | Build without running tests |
| `./gradlew clean build` | Clean then full build |
| `./gradlew dependencyUpdates` | Check for dependency version updates (requires plugin) |

> Note: Use `./gradlew` (the Gradle wrapper) instead of `gradle` to ensure the correct Gradle version is used.

---

## Key Differences at a Glance

### Config Style

**Maven** — verbose XML, every tag must be closed:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

**Gradle** — concise DSL, reads more like code:
```groovy
implementation 'org.springframework.boot:spring-boot-starter-web'
```

### Output Directory

| Tool | Output folder |
|---|---|
| Maven | `target/` |
| Gradle | `build/` |

### Performance

- **Maven** rebuilds everything unless you use specific flags.
- **Gradle** tracks what changed and only rebuilds affected parts (incremental builds). Also supports a **build cache** that can skip tasks whose inputs/outputs haven't changed across machines.

### Extensibility

- **Maven** uses plugins with a fixed lifecycle — you plug into `validate`, `compile`, `test`, etc.
- **Gradle** lets you write arbitrary task logic directly in the build script, making it much more flexible.

### Multi-module Projects

Both support multi-module setups:
- Maven uses `<modules>` in the parent `pom.xml`
- Gradle uses `settings.gradle` with `include ':module-name'`

---

## When to Use Which

| Situation | Recommendation |
|---|---|
| Standard Spring Boot project | Either works well; Spring Initializr supports both |
| Android development | Gradle (Android Studio default) |
| Large monorepo with many modules | Gradle (faster incremental builds) |
| Team familiar with XML/Maven conventions | Maven |
| Need scripting/custom build logic | Gradle |
| Enterprise projects with strict conventions | Maven |

---

## Spring Initializr

Both tools are supported at [start.spring.io](https://start.spring.io). When generating a project, you choose **Maven** or **Gradle** and either the Groovy or Kotlin DSL. The generated project includes a wrapper script (`mvnw` / `gradlew`) so no local installation is required.
