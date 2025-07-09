package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.Month;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
// Каждый тестовый метод стартует с «чистой» in-memory базы
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class FilmControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @Test
    @Order(1)
    @DisplayName("CRUD-фильма и популярное")
    void filmCrudAndPopular() throws Exception {
        // --- создаём фильм (без id)
        Film f = new Film();
        f.setName("Title");
        f.setDescription("Desc");
        f.setReleaseDate(LocalDate.of(2000, Month.JANUARY, 1));
        f.setDuration(100);

        // добавляем фильм и проверяем, что получили id = 1
        String filmJson = mapper.writeValueAsString(f);
        mvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(filmJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));

        // теперь указываем id в объекте для обновления
        f.setId(1);

        // создаём пользователя для лайка (id = 1)
        User u = new User();
        u.setEmail("a@b.com");
        u.setLogin("user1");
        u.setName("User One");
        u.setBirthday(LocalDate.of(1990, 1, 1));
        String userJson = mapper.writeValueAsString(u);
        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));

        // обновляем фильм
        f.setDescription("Desc Updated");
        mvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(f)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description", is("Desc Updated")));

        // получаем фильм по id
        mvc.perform(get("/films/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));

        // ставим лайк
        mvc.perform(put("/films/1/like/1"))
                .andExpect(status().isOk());

        // популярные фильмы (count = 1)
        mvc.perform(get("/films/popular?count=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)));
    }

    @Test
    @Order(2)
    @DisplayName("Удаление лайка")
    void removeLike() throws Exception {
        // Снова создаём фильм (id = 1) и пользователя (id = 1)
        Film f = new Film();
        f.setName("Title");
        f.setDescription("Desc");
        f.setReleaseDate(LocalDate.of(2000, Month.JANUARY, 1));
        f.setDuration(100);
        String filmJson = mapper.writeValueAsString(f);
        mvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(filmJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));

        User u = new User();
        u.setEmail("a@b.com");
        u.setLogin("user1");
        u.setName("User One");
        u.setBirthday(LocalDate.of(1990, 1, 1));
        String userJson = mapper.writeValueAsString(u);
        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));

        // Ставим лайк, чтобы его было что удалять.
        mvc.perform(put("/films/1/like/1"))
                .andExpect(status().isOk());

        // Собственно удаляем лайк
        mvc.perform(delete("/films/1/like/1"))
                .andExpect(status().isOk());

        // После удаления лайка фильм всё ещё возвращается в популярном списке
        mvc.perform(get("/films/popular?count=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)));
    }
}
