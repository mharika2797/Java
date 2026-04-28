# Spring Core Concepts — Interview Guide

---

## 1. What is Spring?

Spring is an open-source **Java application framework** that provides comprehensive infrastructure support for building enterprise Java applications.

**Core idea:** Spring manages the lifecycle and wiring of your application objects (beans), so you focus on business logic instead of plumbing.

**Key pillars:**
- **IoC (Inversion of Control):** The framework controls object creation and lifecycle, not your code.
- **DI (Dependency Injection):** Dependencies are injected into a class rather than the class creating them itself.
- **AOP (Aspect-Oriented Programming):** Cross-cutting concerns (logging, security, transactions) are separated from business logic.

**Spring Modules:**
| Module | Purpose |
|---|---|
| Spring Core | IoC container, DI |
| Spring MVC | Web layer (REST/MVC) |
| Spring Data | Database access abstraction |
| Spring Security | Authentication & Authorization |
| Spring AOP | Aspect-oriented programming |
| Spring Boot | Auto-configuration, rapid setup |

---

## 2. What is Spring Boot?

Spring Boot is an **opinionated extension of Spring** that eliminates boilerplate configuration and lets you build production-ready applications quickly.

**Key features:**
- **Auto-configuration:** Automatically configures Spring beans based on dependencies on the classpath.
- **Embedded server:** Ships with embedded Tomcat/Jetty — no need to deploy a WAR.
- **Starter dependencies:** `spring-boot-starter-web`, `spring-boot-starter-data-jpa`, etc. bundle related dependencies.
- **Actuator:** Built-in endpoints for health checks, metrics, and monitoring.
- **No XML config:** Convention over configuration — use `application.properties` or `application.yml`.

**Spring vs Spring Boot:**
| Spring | Spring Boot |
|---|---|
| Requires manual configuration | Auto-configures based on classpath |
| Need to set up a server | Embedded server included |
| More flexible/verbose | Opinionated, less boilerplate |
| XML or Java config | Minimal config via properties file |

**Entry point:**
```java
@SpringBootApplication  // combines @Configuration + @EnableAutoConfiguration + @ComponentScan
public class MyApp {
    public static void main(String[] args) {
        SpringApplication.run(MyApp.class, args);
    }
}
```

---

## 3. What is Hibernate?

Hibernate is an **ORM (Object-Relational Mapping) framework** for Java. It maps Java objects (POJOs) to database tables, so you work with objects instead of writing raw SQL.

**What it solves:**
- Writing repetitive JDBC boilerplate (open connection, prepare statement, close connection)
- Manual mapping of ResultSet rows to Java objects
- Database portability (switch DB with minimal code change)

**How it works:**
```
Java Object (Entity)  <-->  Hibernate  <-->  Database Table
```

**Core concepts:**
| Concept | Description |
|---|---|
| Session | Unit of work; used to perform DB operations |
| SessionFactory | Creates Session instances; one per application |
| Transaction | Groups operations into an atomic unit |
| HQL | Hibernate Query Language — object-oriented SQL |
| Criteria API | Programmatic, type-safe query building |

**Entity example:**
```java
@Entity
@Table(name = "employees")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name")
    private String name;
}
```

---

## 4. What is JPA?

JPA (Java Persistence API) is a **specification** (a set of interfaces/rules) that defines how Java objects should be persisted to relational databases.

**JPA is not an implementation** — it's a standard. Hibernate is the most popular *implementation* of JPA.

```
JPA (specification/interface)
    └── Hibernate (implementation)  ← most common
    └── EclipseLink
    └── OpenJPA
```

**Key JPA interfaces:**
| Interface | Role |
|---|---|
| `EntityManager` | Manages entity lifecycle; replaces Hibernate's Session |
| `EntityManagerFactory` | Creates EntityManager instances |
| `EntityTransaction` | Manages transactions |
| `JPQL` | JPA Query Language — similar to HQL |

**Hibernate vs JPA:**
| JPA | Hibernate |
|---|---|
| Specification (javax.persistence) | Implementation of JPA |
| Portable across providers | Hibernate-specific features available |
| EntityManager | Session (Hibernate-specific) |

**Spring Data JPA** sits on top of JPA to give you repository abstractions:
```java
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    List<Employee> findByName(String name); // auto-generated query
}
```

---

## 5. Spring Annotations

### Core / DI Annotations

| Annotation | Description |
|---|---|
| `@Component` | Generic Spring-managed bean |
| `@Service` | Marks a service layer class |
| `@Repository` | Marks a DAO/data layer class; also translates DB exceptions |
| `@Controller` | Marks an MVC controller (returns views) |
| `@RestController` | `@Controller` + `@ResponseBody`; returns JSON/XML |
| `@Bean` | Declares a method as a bean producer in a `@Configuration` class |
| `@Configuration` | Marks a class as a source of bean definitions |
| `@Autowired` | Injects a dependency by type |
| `@Qualifier("name")` | Used with `@Autowired` to resolve ambiguity when multiple beans exist |
| `@Primary` | Marks the preferred bean when multiple candidates exist |
| `@Lazy` | Bean is initialized only when first requested |
| `@Scope("prototype")` | Controls bean scope (singleton by default) |

### Spring Boot Annotations

| Annotation | Description |
|---|---|
| `@SpringBootApplication` | Entry point; combines 3 annotations |
| `@EnableAutoConfiguration` | Triggers Spring Boot's auto-config |
| `@ComponentScan` | Tells Spring where to scan for components |
| `@Value("${property}")` | Injects a value from `application.properties` |
| `@ConfigurationProperties` | Binds a group of properties to a POJO |
| `@Profile("dev")` | Activates bean only for a specific profile |

### Web / REST Annotations

| Annotation | Description |
|---|---|
| `@RequestMapping` | Maps HTTP requests to a handler method/class |
| `@GetMapping` | Shortcut for `@RequestMapping(method = GET)` |
| `@PostMapping` | Shortcut for POST |
| `@PutMapping` | Shortcut for PUT |
| `@DeleteMapping` | Shortcut for DELETE |
| `@PathVariable` | Extracts value from the URI path |
| `@RequestParam` | Extracts query parameter from URL |
| `@RequestBody` | Deserializes request body (JSON) into an object |
| `@ResponseBody` | Serializes return value into response body |
| `@ResponseStatus` | Sets the HTTP status code of the response |
| `@ExceptionHandler` | Handles exceptions in a controller |
| `@ControllerAdvice` | Global exception handling across all controllers |

### JPA / Hibernate Annotations

| Annotation | Description |
|---|---|
| `@Entity` | Marks a class as a JPA entity (mapped to a DB table) |
| `@Table(name="")` | Specifies the table name |
| `@Id` | Marks the primary key field |
| `@GeneratedValue` | Defines primary key generation strategy |
| `@Column` | Customizes column mapping (name, nullable, length) |
| `@OneToOne` | One-to-one relationship |
| `@OneToMany` | One-to-many relationship |
| `@ManyToOne` | Many-to-one relationship |
| `@ManyToMany` | Many-to-many relationship |
| `@JoinColumn` | Specifies the FK column |
| `@Transient` | Field is not persisted to the DB |
| `@Lob` | Maps to a large object (CLOB/BLOB) |

### Transaction Annotations

| Annotation | Description |
|---|---|
| `@Transactional` | Wraps a method in a DB transaction; rolls back on exception |
| `@EnableTransactionManagement` | Enables annotation-driven transaction management |

---

## 6. Most Asked Spring Interview Questions

### Spring Core

**Q: What is IoC and DI?**
- **IoC:** Control of object creation is transferred from the programmer to the Spring container.
- **DI:** The container injects required dependencies into a bean (via constructor, setter, or field injection).

**Q: What are the types of Dependency Injection?**
1. **Constructor Injection** — preferred; makes dependencies mandatory and immutable.
2. **Setter Injection** — for optional dependencies.
3. **Field Injection** (`@Autowired` on field) — convenient but harder to test; avoid in production code.

**Q: What is a Spring Bean?**
An object managed by the Spring IoC container. Defined via `@Component`, `@Bean`, or XML.

**Q: What are Bean Scopes?**
| Scope | Description |
|---|---|
| `singleton` | One instance per Spring context (default) |
| `prototype` | New instance every time it's requested |
| `request` | One per HTTP request (web only) |
| `session` | One per HTTP session (web only) |

**Q: What is the Bean lifecycle?**
```
Instantiation → Populate properties → BeanNameAware → BeanFactoryAware →
ApplicationContextAware → @PostConstruct / afterPropertiesSet() →
Bean is ready → @PreDestroy / destroy()
```

**Q: Difference between `@Component`, `@Service`, `@Repository`, `@Controller`?**
All are specializations of `@Component`. The difference is semantic and tooling-related:
- `@Repository` also enables Spring's persistence exception translation.
- `@Controller` is used by Spring MVC to identify web controllers.
- `@Service` signals business logic layer.

**Q: What is `@Autowired` and how does it work?**
Spring resolves and injects the matching bean by type. If multiple beans of the same type exist, use `@Qualifier` to specify which one.

---

### Spring Boot

**Q: What does `@SpringBootApplication` do?**
It combines:
- `@Configuration` — class is a config source
- `@EnableAutoConfiguration` — enable auto-config
- `@ComponentScan` — scan current package and sub-packages for beans

**Q: What is auto-configuration?**
Spring Boot inspects the classpath and conditionally configures beans. For example, if `spring-data-jpa` is on the classpath, it auto-configures a `DataSource` and `EntityManagerFactory`.

**Q: What is `application.properties` / `application.yml`?**
External configuration file for defining datasource URL, server port, logging levels, custom properties, etc.

**Q: What is Spring Actuator?**
Provides production-ready endpoints: `/actuator/health`, `/actuator/metrics`, `/actuator/env`, etc.

---

### Spring MVC / REST

**Q: Difference between `@Controller` and `@RestController`?**
- `@Controller` returns a view name (for server-side rendering).
- `@RestController` = `@Controller` + `@ResponseBody` — writes return value directly to the HTTP response body as JSON/XML.

**Q: What is `@PathVariable` vs `@RequestParam`?**
```java
// @PathVariable — part of the URL path
@GetMapping("/users/{id}")
public User getUser(@PathVariable Long id) { ... }

// @RequestParam — query parameter
@GetMapping("/users")
public List<User> search(@RequestParam String name) { ... }
// URL: /users?name=John
```

**Q: What is `@ControllerAdvice`?**
A global exception handler that applies across all controllers. Used with `@ExceptionHandler` to handle exceptions centrally.

---

### JPA / Hibernate

**Q: What is the difference between `save()` and `saveAndFlush()` in Spring Data JPA?**
- `save()` — persists the entity; flush happens at transaction commit.
- `saveAndFlush()` — immediately flushes changes to the DB within the transaction.

**Q: What is lazy vs eager loading?**
- **Lazy:** Related entities are loaded only when accessed (`FetchType.LAZY`) — default for `@OneToMany`.
- **Eager:** Related entities are loaded immediately with the parent (`FetchType.EAGER`) — default for `@ManyToOne`.

**Q: What is the N+1 problem?**
When fetching N parent entities triggers N additional queries for child entities. Fix: use `JOIN FETCH` in JPQL or `@EntityGraph`.

**Q: What is `@Transactional`?**
Wraps a method in a database transaction. If an unchecked exception is thrown, the transaction is rolled back automatically.

**Q: What is the difference between `persist()`, `merge()`, `detach()`, `remove()`?**
| Method | Description |
|---|---|
| `persist()` | Makes a transient entity managed (INSERT) |
| `merge()` | Merges a detached entity back into persistence context |
| `detach()` | Removes entity from persistence context |
| `remove()` | Deletes the entity from DB |

**Q: What is a persistence context?**
A first-level cache managed by `EntityManager`. Within a transaction, each entity is cached so repeated lookups don't hit the DB again.

---

## Quick Reference Cheat Sheet

```
Spring      = Framework (IoC, DI, AOP, MVC, Security, Data...)
Spring Boot = Spring + Auto-config + Embedded server + Starters
JPA         = Specification for ORM (interfaces only)
Hibernate   = JPA Implementation (most popular)
Spring Data JPA = Abstraction over JPA (Repository pattern)
```
