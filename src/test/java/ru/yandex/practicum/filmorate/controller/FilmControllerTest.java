package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.film.FilmService;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for {@link FilmController} covering CRUD and like operations.
 * JSON payload includes "mpa" and an empty "genres" array.
 */
@WebMvcTest(FilmController.class)
class FilmControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FilmService filmService;

    @Test
    @DisplayName("CRUD operations and popular films endpoint")
    void shouldPerformCrudAndReturnPopular() throws Exception {
        Film sampleFilm = new Film(
                0L,
                "Title",
                "Desc",
                LocalDate.of(2000, 1, 1),
                100,
                new Mpa(1, "G"),
                Set.of()
        );

        when(filmService.addFilm(any(Film.class))).thenReturn(sampleFilm);
        when(filmService.getAllFilms()).thenReturn(List.of(sampleFilm));
        when(filmService.getFilmById(0)).thenReturn(sampleFilm);
        when(filmService.updateFilm(any(Film.class))).thenReturn(sampleFilm);
        when(filmService.getPopular(10)).thenReturn(List.of(sampleFilm));

        // CREATE
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                        {
                                          "id":0,
                                          "name":"Title",
                                          "description":"Desc",
                                          "releaseDate":"2000-01-01",
                                          "duration":100,
                                          "mpa":{"id":1},
                                          "genres":[]
                                        }
                                        """
                        )
                )
                .andExpect(status().isOk());

        // READ ALL
        mockMvc.perform(get("/films"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Title"));

        // READ BY ID
        mockMvc.perform(get("/films/0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Title"));

        // UPDATE
        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                        {
                                          "id":0,
                                          "name":"Title",
                                          "description":"Desc",
                                          "releaseDate":"2000-01-01",
                                          "duration":100,
                                          "mpa":{"id":1},
                                          "genres":[]
                                        }
                                        """
                        )
                )
                .andExpect(status().isOk());

        // POPULAR
        mockMvc.perform(get("/films/popular?count=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Title"));
    }

    @Test
    @DisplayName("Add and remove like endpoints")
    void shouldAddAndRemoveLike() throws Exception {
        doNothing().when(filmService).addLike(1, 2);
        doNothing().when(filmService).removeLike(1, 2);

        mockMvc.perform(put("/films/1/like/2"))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/films/1/like/2"))
                .andExpect(status().isOk());
    }
}
