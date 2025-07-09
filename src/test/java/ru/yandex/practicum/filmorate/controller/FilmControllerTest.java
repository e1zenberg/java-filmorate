package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.Month;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class FilmControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @Test
    @DisplayName("CRUD-фильма и популярное")
    void filmCrudAndPopular() throws Exception {
        // создаём фильм
        Film f = new Film();
        f.setName("Title");
        f.setDescription("Desc");
        f.setReleaseDate(LocalDate.of(2000, Month.JANUARY, 1));
        f.setDuration(120);

        mvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(f)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));

        // получение фильма по ID
        mvc.perform(get("/films/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Title")));

        // Обновляем фильм: меняем его название на Title2
        f.setId(1);
        f.setName("Title2");
        mvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(f)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Title2")));

        // создаём пользователя для поставки лайка
        User u = new User();
        u.setEmail("x@y.com"); u.setLogin("x"); u.setBirthday(LocalDate.of(1990,1,1));
        mvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(u)));

        // ставим лайк
        mvc.perform(put("/films/1/like/1"))
                .andExpect(status().isOk());

        // популярное (count=1)
        mvc.perform(get("/films/popular?count=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)));
    }

    @Test
    @DisplayName("Удаление лайка")
    void removeLike() throws Exception {
        // setup: фильм 1 и пользователь 1 уже созданы в предыдущем тесте
        mvc.perform(delete("/films/1/like/1"))
                .andExpect(status().isOk());

        // популярное после удаления — количество лайков = 0, но фильм всё ещё возвращается
        mvc.perform(get("/films/popular?count=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)));
    }
}
