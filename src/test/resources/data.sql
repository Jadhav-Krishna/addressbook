INSERT INTO address_book (id, name) VALUES (1, 'Personal');
INSERT INTO address_book (id, name) VALUES (2, 'Work');

INSERT INTO contact (id, address_book_id, first_name, last_name, address, city, state, zip, phone_number, email, date_added)
VALUES (101, 1, 'Ada', 'Lovelace', '1 Main St', 'Pune', 'MH', '411001', '9999999999', 'ada@example.com', '2026-03-01 10:00:00');

INSERT INTO contact (id, address_book_id, first_name, last_name, address, city, state, zip, phone_number, email, date_added)
VALUES (102, 2, 'Grace', 'Hopper', '2 Main St', 'Mumbai', 'MH', '400001', '8888888888', 'grace@example.com', '2026-03-05 15:30:00');
