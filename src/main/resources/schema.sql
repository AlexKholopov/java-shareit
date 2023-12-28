DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS items CASCADE;
DROP TABLE IF EXISTS bookings CASCADE;
DROP TABLE IF EXISTS comments CASCADE;
DROP TABLE IF EXISTS requests CASCADE;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
    email varchar(40) NOT NULL UNIQUE,
    name varchar(40) NOT NULL
);

CREATE TABLE IF NOT EXISTS requests (
    id bigint GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
    owner_id bigint REFERENCES users(id),
    name varchar(40),
    description varchar(200)
);

CREATE TABLE IF NOT EXISTS items (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
    owner_id bigint REFERENCES users(id),
    name varchar(40) NOT NULL,
    description varchar(200) NOT NULL,
    available boolean NOT NULL DEFAULT(false),
    request_id bigint REFERENCES requests(id)
);

CREATE TABLE IF NOT EXISTS bookings (
    id bigint GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
    booker_id bigint REFERENCES users(id),
    item_id bigint REFERENCES items(id),
    start_time varchar(40),
    end_time varchar(40),
    status varchar(10)
);

CREATE TABLE IF NOT EXISTS comments (
    id bigint GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
    item_id bigint REFERENCES items(id),
    user_id bigint REFERENCES users(id),
    text varchar(1000),
    created varchar(40)
);
