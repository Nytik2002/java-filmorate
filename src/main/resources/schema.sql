CREATE TABLE IF NOT EXISTS users(
    id INTEGER AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    login VARCHAR(255) NOT NULL,
    name VARCHAR(255),
    birthday DATE
);

CREATE TABLE IF NOT EXISTS films(
    id INTEGER AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(200),
    release_date DATE,
    duration INTEGER,
    mpa_id INTEGER
);

CREATE TABLE IF NOT EXISTS friendship(
    user_id INTEGER,
    friend_id INTEGER,
    PRIMARY KEY(user_id, friend_id)
);

CREATE TABLE IF NOT EXISTS likes (
    film_id INTEGER NOT NULL,
    user_id INTEGER NOT NULL,

    FOREIGN KEY (film_id) REFERENCES films(id),
    FOREIGN KEY (user_id) REFERENCES users(id),

    PRIMARY KEY (film_id, user_id)
);

CREATE TABLE IF NOT EXISTS genres (
    id INTEGER PRIMARY KEY,
    name VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS mpa (
    id INTEGER PRIMARY KEY,
    name VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS film_genres(
    film_id INTEGER,
    genre_id INTEGER,

    PRIMARY KEY(film_id,genre_id),

    FOREIGN KEY(film_id) REFERENCES films(id),
    FOREIGN KEY(genre_id) REFERENCES genres(id)

);