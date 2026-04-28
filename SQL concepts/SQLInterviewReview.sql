-- ============================================================
--  SECTION 1: CORE CONCEPTS
-- ============================================================

-- ------------------------------------------------------------
--  1. DDL vs DML vs DCL vs TCL
-- ------------------------------------------------------------
--  DDL (Data Definition Language)  — defines schema structure
--      CREATE   — creates a new table, index, view, or database
--      ALTER    — modifies an existing table (add/drop/rename columns, change data types)
--      DROP     — permanently deletes a table or database along with all its data
--      TRUNCATE — removes all rows from a table instantly but keeps the table structure; cannot be rolled back in most DBs
--
--  DML (Data Manipulation Language) — works with data
--      SELECT — retrieves rows from one or more tables based on specified conditions
--      INSERT — adds one or more new rows into a table
--      UPDATE — modifies existing rows in a table that match a condition
--      DELETE — removes rows from a table that match a condition; can be rolled back (unlike TRUNCATE)
--
--  DCL (Data Control Language)      — controls access
--      GRANT  — gives a user permission to perform an action (SELECT, INSERT, etc.) on an object
--      REVOKE — removes a previously granted permission from a user
--
--  TCL (Transaction Control Language) — manages transactions
--      COMMIT    — permanently saves all changes made in the current transaction
--      ROLLBACK  — undoes all changes made in the current transaction back to the last COMMIT or SAVEPOINT
--      SAVEPOINT — sets a named checkpoint within a transaction so ROLLBACK can partially undo work


-- ------------------------------------------------------------
--  2. PRIMARY KEY vs FOREIGN KEY vs UNIQUE
-- ------------------------------------------------------------
--  PRIMARY KEY  — uniquely identifies a row; NOT NULL + UNIQUE; only one per table
--  FOREIGN KEY  — references the PRIMARY KEY of another table; enforces referential integrity
--  UNIQUE       — enforces uniqueness but allows one NULL; a table can have many UNIQUE constraints


-- ------------------------------------------------------------
--  3. JOINS (most asked)
-- ------------------------------------------------------------
--
--   INNER JOIN   — rows that match in BOTH tables
--   LEFT JOIN    — all rows from the LEFT table + matching rows from right (NULL if no match)
--   RIGHT JOIN   — all rows from the RIGHT table + matching rows from left (NULL if no match)
--   FULL OUTER JOIN — all rows from BOTH tables (NULL where no match on either side)
--   CROSS JOIN   — Cartesian product of both tables (every row × every row)
--   SELF JOIN    — join a table to itself (common for hierarchy/manager queries)

--  Visual cheat sheet:
--
--   A ∩ B     → INNER JOIN
--   A         → LEFT JOIN
--   B         → RIGHT JOIN
--   A ∪ B     → FULL OUTER JOIN


-- ------------------------------------------------------------
--  4. AGGREGATE FUNCTIONS
-- ------------------------------------------------------------
--  COUNT(*), COUNT(col)  — number of rows / non-null values
--  SUM(col)              — total
--  AVG(col)              — mean
--  MIN(col), MAX(col)    — smallest / largest
--
--  Always pair with GROUP BY when used alongside non-aggregate columns.
--  Use HAVING to filter on aggregated results (WHERE filters BEFORE aggregation).


-- ------------------------------------------------------------
--  5. WHERE vs HAVING
-- ------------------------------------------------------------
--  WHERE   — filters individual rows BEFORE grouping
--  HAVING  — filters groups AFTER GROUP BY
--
--  Rule of thumb: if the condition references an aggregate (SUM, COUNT, etc.) → use HAVING

SELECT department, COUNT(*) AS emp_count
FROM employees
GROUP BY department
HAVING COUNT(*) > 5;        -- filters departments with more than 5 employees


-- ------------------------------------------------------------
--  6. SUBQUERIES vs CTEs
-- ------------------------------------------------------------
--  Subquery — query nested inside another query; runs once per outer row (correlated) or once (non-correlated)
--  CTE (Common Table Expression) — named temporary result set defined with WITH; improves readability;
--                                   can be referenced multiple times in the same query

-- Subquery example
SELECT name FROM employees
WHERE salary > (SELECT AVG(salary) FROM employees);

-- Equivalent CTE
WITH avg_sal AS (
    SELECT AVG(salary) AS avg FROM employees
)
SELECT name FROM employees, avg_sal
WHERE salary > avg_sal.avg;


-- ------------------------------------------------------------
--  7. INDEXES
-- ------------------------------------------------------------
--  Speed up reads by creating a sorted data structure on a column.
--  Trade-off: faster SELECT, slower INSERT/UPDATE/DELETE (index must be maintained).
--  Use on columns in WHERE, JOIN ON, and ORDER BY clauses.
--  Avoid on columns with very low cardinality (e.g., boolean flags).

CREATE INDEX idx_emp_department ON employees(department);




-- ------------------------------------------------------------
--  10. TRANSACTIONS & ACID
-- ------------------------------------------------------------
--  A — Atomicity    : all operations succeed or none do (all-or-nothing)
--  C — Consistency  : database moves from one valid state to another
--  I — Isolation    : concurrent transactions do not interfere with each other
--  D — Durability   : committed data persists even after a crash


-- ============================================================
--  SECTION 2: PRACTICE Q&A  (5 questions)
-- ============================================================

-- ------------------------------------------------------------
--  Tables used in the questions below:
--
--  employees(id, name, department, salary, manager_id)
--  departments(id, name, budget)
--  orders(id, customer_id, amount, order_date)
--  customers(id, name, city)
-- ------------------------------------------------------------


-- Q1. Find the second-highest salary in the employees table.
-- ---------------------------------------------------------------
-- Common wrong approach: LIMIT/OFFSET breaks on ties.
-- Better: use a subquery or DENSE_RANK so ties are handled correctly.

-- Approach A — subquery
SELECT MAX(salary) AS second_highest
FROM employees
WHERE salary < (SELECT MAX(salary) FROM employees);

-- Approach B — window function (handles ties, shows the employee name too)
SELECT name, salary
FROM (
    SELECT name, salary,
           DENSE_RANK() OVER (ORDER BY salary DESC) AS rnk
    FROM employees
) ranked
WHERE rnk = 2;


-- Q2. List departments that have more than 3 employees earning above $60,000.
-- ---------------------------------------------------------------
SELECT department, COUNT(*) AS high_earners
FROM employees
WHERE salary > 60000
GROUP BY department
HAVING COUNT(*) > 3;

-- Key concept: WHERE filters rows first (salary > 60000),
-- then GROUP BY buckets them, then HAVING filters the groups.


-- Q3. For each customer, show their most recent order amount.
--     Include customers who have never placed an order (show NULL).
-- ---------------------------------------------------------------
SELECT c.name,
       o.amount    AS latest_order_amount,
       o.order_date AS latest_order_date
FROM customers c
LEFT JOIN orders o
    ON o.customer_id = c.id
    AND o.order_date = (
        SELECT MAX(order_date)
        FROM orders
        WHERE customer_id = c.id
    );

-- Key concept: LEFT JOIN keeps every customer even if no order exists.
-- The correlated subquery finds that customer's latest date,
-- which is then matched in the ON clause to grab the exact row.


-- Q4. Find employees who earn more than the average salary of their own department.
-- ---------------------------------------------------------------
-- Approach A — correlated subquery
SELECT e1.name, e1.department, e1.salary
FROM employees e1
WHERE e1.salary > (
    SELECT AVG(e2.salary)
    FROM employees e2
    WHERE e2.department = e1.department   -- correlated: recalculated for each outer row
);

-- Approach B — window function (single table scan, often faster)
SELECT name, department, salary
FROM (
    SELECT name, department, salary,
           AVG(salary) OVER (PARTITION BY department) AS dept_avg
    FROM employees
) sub
WHERE salary > dept_avg;


-- Q5. Show each employee's name, their manager's name, and the manager's salary.
--     Employees without a manager should still appear.
-- ---------------------------------------------------------------
SELECT
    e.name          AS employee,
    m.name          AS manager,
    m.salary        AS manager_salary
FROM employees e
LEFT JOIN employees m ON e.manager_id = m.id;   -- SELF JOIN

-- Key concept: self join — the same table is aliased twice.
-- LEFT JOIN ensures employees with no manager (manager_id IS NULL) still show up.


-- ============================================================
--  QUICK CHEAT SHEET — ORDER OF CLAUSES
-- ============================================================
--
--  SELECT   — specifies which columns (or expressions) to return in the result set
--  FROM     — identifies the table(s) to query data from
--  JOIN     — combines rows from two tables based on a related column between them
--  WHERE    — filters individual rows before any grouping happens
--  GROUP BY — groups rows that share the same value in the specified column(s) for aggregation
--  HAVING   — filters groups produced by GROUP BY based on an aggregate condition
--  ORDER BY — sorts the final result set ASC (default) or DESC by one or more columns
--  LIMIT    — restricts the number of rows returned; useful for pagination and top-N queries
--
--  Execution order (different from write order!):
--  FROM → JOIN → WHERE → GROUP BY → HAVING → SELECT → ORDER BY → LIMIT
