CREATE TABLE backend_user
(
    id           SERIAL PRIMARY KEY,
    name         VARCHAR(128) NOT NULL,
    username     VARCHAR(128) NOT NULL,
    password     VARCHAR(256) NOT NULL,
    email        VARCHAR(128) UNIQUE,
    phone_number VARCHAR(32) UNIQUE,
    created_at   TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE permission
(
    id   SERIAL PRIMARY KEY,
    name VARCHAR(128) NOT NULL UNIQUE
);

CREATE TABLE backend_user_permission
(
    backend_user_id INT REFERENCES backend_user (id) NOT NULL,
    permission_id   INT REFERENCES permission (id)   NOT NULL,
    PRIMARY KEY (backend_user_id, permission_id)
);

INSERT INTO permission(name)
VALUES ('BOOK_MANAGE'),
       ('READER_MANAGE'),
       ('BORROWING_MANAGE'),
       ('USER_MANAGE');

INSERT INTO backend_user(name, username, password)
VALUES ('Admin User', 'admin', '$2y$12$1Pl4vhHnYCHFYrD3WQjCc.7J2NsR8ZtFtQgbQT3RBmlemDfIked1W');

INSERT INTO backend_user_permission(backend_user_id, permission_id)
VALUES (1, 1),
       (1, 2),
       (1, 3),
       (1, 4);
