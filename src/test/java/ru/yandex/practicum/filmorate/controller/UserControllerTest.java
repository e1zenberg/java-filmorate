package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
// Сбрасываем контекст перед каждым методом для чистоты InMemory-хранилищ
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @Test
    @Order(1)
    @DisplayName("Создание, получение и обновление пользователя")
    void userCrudFlow() throws Exception {
        User u = new User();
        u.setEmail("a@b.com");
        u.setLogin("user1");
        u.setName("User One");
        u.setBirthday(LocalDate.of(1990, 1, 1));

        // CREATE
        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(u)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.email", is("a@b.com")));

        // GET ALL
        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        // GET BY ID
        mvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login", is("user1")));

        // UPDATE
        u.setId(1);
        u.setName("User 1 Updated");
        mvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(u)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("User 1 Updated")));
    }

    @Test
    @Order(2)
    @DisplayName("Дружба: добавить, получить, удалить")
    void friendsFlow() throws Exception {
        User u1 = new User();
        u1.setEmail("u1@b.com");
        u1.setLogin("u1");
        u1.setBirthday(LocalDate.of(2000, 1, 1));

        User u2 = new User();
        u2.setEmail("u2@b.com");
        u2.setLogin("u2");
        u2.setBirthday(LocalDate.of(2000, 2, 2));

        mvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(u1)));
        mvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(u2)));

        // добавить в друзья u1 → u2
        mvc.perform(put("/users/1/friends/2"))
                .andExpect(status().isOk());

        // список друзей u1
        mvc.perform(get("/users/1/friends"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(2)));

        // общие друзья (пока только они сами)
        mvc.perform(get("/users/1/friends/common/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        // удалить друга
        mvc.perform(delete("/users/1/friends/2"))
                .andExpect(status().isOk());

        // после удаления список пуст
        mvc.perform(get("/users/1/friends"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}
