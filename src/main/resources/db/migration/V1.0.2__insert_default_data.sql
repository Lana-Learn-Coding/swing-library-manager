INSERT INTO category(name)
VALUES ('Uncategorized');

INSERT INTO storage(name)
VALUES ('On shelf'),
       ('In Storage');


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