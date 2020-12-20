CREATE TABLE reader
(
    id           SERIAL PRIMARY KEY,
    email        VARCHAR(128) UNIQUE,
    phone_number VARCHAR(32)  NOT NULL UNIQUE,
    name         VARCHAR(128) NOT NULL,
    address      VARCHAR(256),
    "limit"      INT          NOT NULL DEFAULT 5,
    birth        DATE         NOT NULL,
    created_at   TIMESTAMP    NOT NULL DEFAULT now(),
    updated_at   TIMESTAMP    NOT NULL DEFAULT now()
);

CREATE TABLE category
(
    id         SERIAL PRIMARY KEY,
    name       VARCHAR(128) NOT NULL UNIQUE,
    created_at TIMESTAMP    NOT NULL DEFAULT now(),
    updated_at TIMESTAMP    NOT NULL DEFAULT now()
);

CREATE TABLE series
(
    id         SERIAL PRIMARY KEY,
    name       VARCHAR(128) NOT NULL UNIQUE,
    created_at TIMESTAMP    NOT NULL DEFAULT now(),
    updated_at TIMESTAMP    NOT NULL DEFAULT now()
);

CREATE TABLE book_meta
(
    id          SERIAL PRIMARY KEY,
    title       VARCHAR(256) NOT NULL,
    image       VARCHAR(128),
    author      VARCHAR(128),
    publisher   VARCHAR(128),
    year        INT          NOT NULL,
    category_id INT          REFERENCES category (id) ON DELETE SET NULL,
    series_id   INT          REFERENCES series (id) ON DELETE SET NULL,
    created_at  TIMESTAMP    NOT NULL DEFAULT now(),
    updated_at  TIMESTAMP    NOT NULL DEFAULT now()
);

CREATE TABLE storage
(
    id         SERIAL PRIMARY KEY,
    name       VARCHAR(128) NOT NULL UNIQUE,
    created_at TIMESTAMP    NOT NULL DEFAULT now(),
    updated_at TIMESTAMP    NOT NULL DEFAULT now()
);

CREATE TABLE book_borrowing
(
    id            SERIAL PRIMARY KEY,
    borrowed_date DATE      NOT NULL DEFAULT now(),
    due_date      DATE      NOT NULL,
    borrower_id   INT       NOT NULL REFERENCES reader (id) ON DELETE RESTRICT,
    note          VARCHAR(256),
    created_at    TIMESTAMP NOT NULL DEFAULT now(),
    updated_at    TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE book
(
    id                SERIAL PRIMARY KEY,
    condition         INT       NOT NULL DEFAULT 10,
    image             VARCHAR(128),
    note              VARCHAR(256),
    meta_id           INT       REFERENCES book_meta (id) ON DELETE SET NULL,
    storage_id        INT       NOT NULL REFERENCES storage (id) ON DELETE RESTRICT,
    book_borrowing_id INT       REFERENCES book_borrowing (id) ON DELETE SET NULL,
    created_at        TIMESTAMP NOT NULL DEFAULT now(),
    updated_at        TIMESTAMP NOT NULL DEFAULT now()
)