-- 1) Справочник MPA
CREATE TABLE IF NOT EXISTS mpa (
                                   mpa_id   INT          PRIMARY KEY,
                                   name     VARCHAR(255) NOT NULL
);

-- 2) Справочник жанров
CREATE TABLE IF NOT EXISTS genres (
                                      genre_id INT          PRIMARY KEY,
                                      name     VARCHAR(255) NOT NULL
);

-- 3) Пользователи
CREATE TABLE IF NOT EXISTS users (
                                     user_id    BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     email      VARCHAR(255) NOT NULL,
                                     login      VARCHAR(255) NOT NULL,
                                     name       VARCHAR(255),
                                     birthday   DATE NOT NULL
);

-- 4) Фильмы
CREATE TABLE IF NOT EXISTS films (
                                     film_id      BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     name         VARCHAR(255)     NOT NULL,
                                     description  VARCHAR(1000),
                                     release_date DATE            NOT NULL,
                                     duration     INT             NOT NULL,
                                     mpa_id       INT             NOT NULL,
                                     FOREIGN KEY (mpa_id) REFERENCES mpa(mpa_id)
);

-- 5) Связь фильм–жанр (singular)
CREATE TABLE IF NOT EXISTS film_genre (
                                          film_id   BIGINT NOT NULL,
                                          genre_id  INT    NOT NULL,
                                          PRIMARY KEY (film_id, genre_id),
                                          FOREIGN KEY (film_id)  REFERENCES films(film_id),
                                          FOREIGN KEY (genre_id) REFERENCES genres(genre_id)
);

-- 6) Друзья пользователей (с полем status)
CREATE TABLE IF NOT EXISTS friends (
                                       user_id    BIGINT NOT NULL,
                                       friend_id  BIGINT NOT NULL,
                                       status     VARCHAR(20) NOT NULL,
                                       PRIMARY KEY (user_id, friend_id),
                                       FOREIGN KEY (user_id)   REFERENCES users(user_id),
                                       FOREIGN KEY (friend_id) REFERENCES users(user_id)
);

-- 7) Лайки фильмов (без FK на users)
CREATE TABLE IF NOT EXISTS likes (
                                     film_id BIGINT NOT NULL,
                                     user_id BIGINT NOT NULL,
                                     PRIMARY KEY (film_id, user_id),
                                     FOREIGN KEY (film_id) REFERENCES films(film_id)
);
