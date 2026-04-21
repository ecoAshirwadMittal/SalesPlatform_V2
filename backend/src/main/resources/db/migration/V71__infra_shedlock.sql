-- ShedLock JDBC provider table — column shape required by net.javacrumbs.shedlock 5.x
CREATE TABLE infra.shedlock (
    name        VARCHAR(64)  PRIMARY KEY,
    lock_until  TIMESTAMP    NOT NULL,
    locked_at   TIMESTAMP    NOT NULL,
    locked_by   VARCHAR(255) NOT NULL
);
