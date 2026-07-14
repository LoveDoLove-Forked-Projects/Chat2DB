select a, b, c as C
from test;


CREATE TABLE test
(
    id   INT,
    name VARCHAR(255)
);

INSERT INTO test (id, name)
VALUES (1, 'Alice');

UPDATE test
SET name = 'Bob'
WHERE id = 1;

DELETE
FROM test
WHERE id = 1;

CREATE VIEW test_view AS
SELECT *
FROM test;

CREATE FUNCTION test_func(a INT,  b INT) RETURNS INT
BEGIN
    RETURN a + b;
END;

CREATE PROCEDURE test_proc()
BEGIN
    INSERT INTO test (id, name) VALUES (2, 'Charlie');
END;

SELECT *
FROM test;

SELECT *
FROM test t;

SELECT *
FROM dbo.test t;

CREATE TABLE logs
(
    id         INT AUTO_INCREMENT PRIMARY KEY,
    message    VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE
    EVENT IF NOT EXISTS insert_log_event
    ON SCHEDULE EVERY 1 MINUTE
    DO
    INSERT INTO logs (message)
    VALUES ('This is a log entry');

(SELECT customer_id, total_spent
 FROM (SELECT customer_id, total_spent
       FROM (SELECT customer_id, SUM(amount) AS total_spent
             FROM orders
             GROUP BY customer_id) AS subquery1
       WHERE total_spent > 500) AS subquery2
 WHERE total_spent > 1000);


(SELECT customer_id, SUM(amount) AS total_spent
 FROM orders
 WHERE order_date BETWEEN '2023-01-01' AND '2023-06-30'
 GROUP BY customer_id)
UNION ALL
(SELECT customer_id, SUM(amount) AS total_spent
 FROM orders
 WHERE order_date BETWEEN '2023-07-01' AND '2023-12-31'
 GROUP BY customer_id)
ORDER BY total_spent DESC
LIMIT 10;

select *
from test.a t


