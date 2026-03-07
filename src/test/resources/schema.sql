CREATE TABLE address_book (
    id BIGINT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE contact (
    id BIGINT PRIMARY KEY,
    address_book_id BIGINT NOT NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    address VARCHAR(200),
    city VARCHAR(100),
    state VARCHAR(100),
    zip VARCHAR(20),
    phone_number VARCHAR(30),
    email VARCHAR(200),
    CONSTRAINT fk_contact_address_book FOREIGN KEY (address_book_id)
        REFERENCES address_book(id)
);
