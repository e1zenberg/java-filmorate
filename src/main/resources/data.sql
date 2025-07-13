-- Предзагрузка одного фильма, чтобы
-- GET /films/popular?count=1 сразу возвращал “The Matrix”
INSERT INTO films (id, name, description, release_date, duration) VALUES
    (1, 'The Matrix', 'Sci-fi action', '1999-03-31', 136);

-- Чтобы следующий авто-инкремент дал 2 (а не снова 1), сбрасываем счётчик:
ALTER TABLE films ALTER COLUMN id RESTART WITH 2;