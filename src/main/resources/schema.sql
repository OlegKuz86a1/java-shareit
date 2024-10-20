DROP TABLE IF EXISTS statuses CASCADE;
DROP TABLE IF EXISTS booking_status ;
DROP TABLE IF EXISTS bookings ;
DROP TABLE IF EXISTS item_requests CASCADE;
DROP TABLE IF EXISTS items ;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS comments CASCADE;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(256),
    email VARCHAR(256)
);

CREATE TABLE IF NOT EXISTS item_requests (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    requestor_id BIGINT,
    description VARCHAR(256),
    created DATETIME,
    FOREIGN KEY (requestor_id)  REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    owner_id BIGINT,
    name VARCHAR(256),
    description VARCHAR(256),
    available BOOLEAN,
    request_id BIGINT,
    comment VARCHAR(256),
    FOREIGN KEY (owner_id)  REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (request_id)  REFERENCES item_requests (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS bookings (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    item_id BIGINT,
    start_booking DATETIME,
    end_booking DATETIME,
    booker_id BIGINT,
    FOREIGN KEY (item_id)  REFERENCES items (id) ON DELETE CASCADE,
    FOREIGN KEY (booker_id)  REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS statuses(
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(64) NOT NULL DEFAULT 'WAITING'
);

CREATE TABLE IF NOT EXISTS booking_status(
    status_id INT,
    booking_id BIGINT,
    FOREIGN KEY (status_id) REFERENCES statuses (id) ON DELETE CASCADE,
    FOREIGN KEY (booking_id) REFERENCES bookings (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS comments(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    item_id BIGINT,
    created DATETIME,
    author_id BIGINT,
    text VARCHAR(256),
    FOREIGN KEY (author_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (item_id) REFERENCES items (id) ON DELETE CASCADE
);