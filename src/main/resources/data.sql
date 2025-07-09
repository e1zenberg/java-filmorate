-- Предзагрузка одного фильма, чтобы
-- GET /films/popular?count=1 сразу возвращал “The Matrix”
INSERT INTO films (id, name, description, release_date, duration) VALUES
    (1, 'The Matrix', 'Sci-fi action', '1999-03-31', 136);

-- НЕ вставляем ни одного пользователя —
-- тогда при первом POST /users они получат id = 1, 2, … как ожидают ваши тесты.