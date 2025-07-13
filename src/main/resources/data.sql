-- Предзагрузка справочника MPA
INSERT INTO mpa (mpa_id, name) VALUES
                                   (1, 'G'),
                                   (2, 'PG'),
                                   (3, 'PG-13'),
                                   (4, 'R'),
                                   (5, 'NC-17');

-- Предзагрузка справочника жанров
INSERT INTO genres (genre_id, name) VALUES
                                        (1, 'Комедия'),
                                        (2, 'Драма'),
                                        (3, 'Мультфильм'),
                                        (4, 'Триллер'),
                                        (5, 'Документальный'),
                                        (6, 'Боевик');

-- Один тестовый фильм
INSERT INTO films (film_id, name, description, release_date, duration, mpa_id) VALUES
    (1, 'The Matrix', 'Sci-fi action', '1999-03-31', 136, 1);

-- Сброс автоинкремента, чтобы следующий фильм получил ID=2
ALTER TABLE films ALTER COLUMN film_id RESTART WITH 2;
