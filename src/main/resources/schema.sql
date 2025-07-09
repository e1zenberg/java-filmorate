-- Удаляем старые таблицы (чтобы при перезапуске в памяти всё очищалось)
DROP TABLE IF EXISTS likes;
DROP TABLE IF EXISTS friends;
DROP TABLE IF EXISTS films;
DROP TABLE IF EXISTS users;

-- Таблица пользователей
CREATE TABLE users (
                       id INT PRIMARY KEY AUTO_INCREMENT,
                       email VARCHAR(255) NOT NULL,
                       login VARCHAR(50) NOT NULL,
                       name VARCHAR(255),
                       birthday DATE NOT NULL
);

-- Таблица фильмов
CREATE TABLE films (
                       id INT PRIMARY KEY AUTO_INCREMENT,
                       name VARCHAR(255) NOT NULL,
                       description VARCHAR(1000),
                       release_date DATE NOT NULL,
                       duration INT NOT NULL
);

-- Таблица дружбы (асимметричные связи)
CREATE TABLE friends (
                         user_id INT NOT NULL,
                         friend_id INT NOT NULL,
                         PRIMARY KEY (user_id, friend_id),
                         FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                         FOREIGN KEY (friend_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Таблица лайков фильмов
CREATE TABLE likes (
                       film_id INT NOT NULL,
                       user_id INT NOT NULL,
                       PRIMARY KEY (film_id, user_id),
                       FOREIGN KEY (film_id) REFERENCES films(id) ON DELETE CASCADE,
                       FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);