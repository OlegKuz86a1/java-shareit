DROP TABLE IF EXISTS statuses CASCADE;
DROP TABLE IF EXISTS booking_status ;
DROP TABLE IF EXISTS bookings ;
DROP TABLE IF EXISTS item_requests CASCADE;
DROP TABLE IF EXISTS comments CASCADE;
DROP TABLE IF EXISTS items ;
DROP TABLE IF EXISTS users CASCADE;


CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    name VARCHAR(256),
    email VARCHAR(256)
);

CREATE TABLE IF NOT EXISTS item_requests (
    id SERIAL PRIMARY KEY,
    requestor_id BIGINT,
    description VARCHAR(256),
    created TIMESTAMP,
    FOREIGN KEY (requestor_id)  REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS items (
    id SERIAL PRIMARY KEY,
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
    id SERIAL PRIMARY KEY,
    item_id BIGINT,
    start_booking TIMESTAMP,
    end_booking TIMESTAMP,
    booker_id BIGINT,
    FOREIGN KEY (item_id)  REFERENCES items (id) ON DELETE CASCADE,
    FOREIGN KEY (booker_id)  REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS statuses(
    id SERIAL PRIMARY KEY,
    name VARCHAR(64) NOT NULL DEFAULT 'WAITING'
);

CREATE TABLE IF NOT EXISTS booking_status(
    status_id INT NOT NULL,
    booking_id BIGINT NOT NULL,
    FOREIGN KEY (status_id) REFERENCES statuses (id) ON DELETE CASCADE,
    FOREIGN KEY (booking_id) REFERENCES bookings (id) ON DELETE CASCADE,
    PRIMARY KEY (status_id, booking_id)
);

CREATE TABLE IF NOT EXISTS comments(
    id SERIAL PRIMARY KEY,
    item_id BIGINT NOT NULL,
    "created" TIMESTAMP NOT NULL,
    author_id BIGINT NOT NULL,
    text VARCHAR(256) NOT NULL,
    FOREIGN KEY (author_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (item_id) REFERENCES items (id) ON DELETE CASCADE
);