CREATE TABLE reader
(
    id           SERIAL PRIMARY KEY,
    email        VARCHAR(128) UNIQUE,
    phone_number VARCHAR(32)  NOT NULL UNIQUE,
    name         VARCHAR(128) NOT NULL,
    address      VARCHAR(256),
    avatar       VARCHAR(128),
    gender       BOOLEAN      NOT NULL DEFAULT TRUE,
    borrow_limit INT          NOT NULL DEFAULT 5,
    birth        DATE         NOT NULL,
    created_at   TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE category
(
    id         SERIAL PRIMARY KEY,
    name       VARCHAR(128) NOT NULL UNIQUE,
    created_at TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE series
(
    id         SERIAL PRIMARY KEY,
    name       VARCHAR(128) NOT NULL UNIQUE,
    created_at TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE book_meta
(
    id          SERIAL PRIMARY KEY,
    title       VARCHAR(256) NOT NULL,
    image       VARCHAR(128),
    author      VARCHAR(128),
    publisher   VARCHAR(128),
    is_deleted  BOOLEAN      NOT NULL DEFAULT FALSE,
    year        INT          NOT NULL,
    category_id INT          REFERENCES category (id) ON DELETE SET NULL,
    series_id   INT          REFERENCES series (id) ON DELETE SET NULL,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE storage
(
    id         SERIAL PRIMARY KEY,
    name       VARCHAR(128) NOT NULL UNIQUE,
    created_at TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE borrow_ticket
(
    id            SERIAL PRIMARY KEY,
    borrowed_date DATE      NOT NULL DEFAULT NOW(),
    due_date      DATE      NOT NULL,
    returned_date DATE,
    is_returned   BOOLEAN   NOT NULL DEFAULT FALSE,
    borrower_id   INT       NOT NULL REFERENCES reader (id) ON DELETE CASCADE,
    note          TEXT,
    created_at    TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE book
(
    id         SERIAL PRIMARY KEY,
    condition  INT       NOT NULL DEFAULT 10,
    image      VARCHAR(128),
    note       TEXT,
    position   VARCHAR(256),
    is_deleted BOOLEAN   NOT NULL DEFAULT FALSE,
    meta_id    INT       NOT NULL REFERENCES book_meta (id) ON DELETE SET NULL,
    storage_id INT       NOT NULL REFERENCES storage (id) ON DELETE RESTRICT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE book_borrow_ticket
(
    id               SERIAL PRIMARY KEY,
    borrow_ticket_id INT NOT NULL REFERENCES borrow_ticket (id) ON DELETE CASCADE,
    book_id          INT NOT NULL REFERENCES book (id)
)