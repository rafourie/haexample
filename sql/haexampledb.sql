DROP TABLE IF EXISTS counters;

BEGIN;

CREATE TABLE counters (id serial PRIMARY KEY, name varchar, current_value integer);

INSERT INTO counters (name, current_value) VALUES ('globalSingleton', 0);

COMMIT;