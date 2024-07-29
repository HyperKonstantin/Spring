package sc.springProject.controllers;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional // for rollback after each test
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @Order(1)
    public void getUsers_returnsUsers() throws Exception {
        RequestBuilder requestBuilder = get("/user/get");

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @Order(2)
    public void addUser_validDepartmentId_returnsCreatedUser() throws Exception {
        RequestBuilder requestBuilder = post("/user/add/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "name":"Egor",
                            "age":44,
                            "salary":1300
                        }
                        """);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("""
                        {
                            "name":"Egor",
                            "age":44,
                            "salary":1300,
                            "department":"development"
                        }
                        """))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    @Order(3)
    public void addUser_invalidDepartmentId_returnsErrorMessage() throws Exception {
        RequestBuilder requestBuilder = post("/user/add/1000")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "name":"Egor",
                            "age":44,
                            "salary":1300
                        }
                        """);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(new MediaType(MediaType.TEXT_PLAIN, StandardCharsets.UTF_8)))
                .andExpect(content().string("указанный отдел отсутствует"));
    }

    @Test
    @Order(4)
    public void findUser_correctUserName_returnsUser() throws Exception {
        RequestBuilder requestBuilder = get("/user/find")
                .param("name", "Kostya");

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("""
                        [
                            {
                                "id": 1,
                                "name": "Kostya",
                                "age": 19,
                                "salary": 1000,
                                "department": "development"
                            }
                        ]
                        """));
    }

    @Test
    @Order(5)
    public void findUser_incorrectUserName_returnsEmptyList() throws Exception {
        RequestBuilder requestBuilder = get("/user/find")
                .param("name", " ");

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("[]"));
    }

    @Test
    @Order(8)
    public void deleteUser_validId_returnsUserDto() throws Exception {
        RequestBuilder requestBuilder = get("/user/delete")
                        .param("id", "2");

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("""
                        {
                            "id": 2,
                            "name": "Anton",
                            "age": 20,
                            "salary": 500,
                            "department": "testing"
                        }
                        """));
    }

    @Test
    @Order(9)
    public void deleteUser_invalidId_returnsErrorMessage() throws Exception {
        RequestBuilder requestBuilder = get("/user/delete")
                .param("id", "1000");

        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(new MediaType(MediaType.TEXT_PLAIN, StandardCharsets.UTF_8)))
                .andExpect(content().string("Такого пользователя нет"));
    }

    @Rollback
    @Test
    @Order(6)
    public void changeUserName_validUserId_returnsUpdatedUser() throws Exception {
        RequestBuilder requestBuilder = get("/user/change-name")
                .param("id", "2")
                .param("name", "Toha");

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("""
                        {
                            "id": 2,
                            "name": "Toha",
                            "age": 20,
                            "salary": 500,
                            "department": "testing"
                        }
                        """));
    }

    @Test
    @Order(7)
    public void changeUserName_invalidUserId_returnsErrorMessage() throws Exception {
        RequestBuilder requestBuilder = get("/user/change-name")
                .param("id", "1000")
                .param("name", "Toha");

        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(new MediaType(MediaType.TEXT_PLAIN, StandardCharsets.UTF_8)))
                .andExpect(content().string("Пользователя с таким id нет!"));
    }

    @Test
    @Order(10)
    public void sendUserId_validId_returnsStatusOk() throws Exception {
        RequestBuilder requestBuilder = get("/user/send")
                .param("id", "1");

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(new MediaType(MediaType.TEXT_PLAIN, StandardCharsets.UTF_8)))
                .andExpect(content().string("Пользователь Kostya отправлен!"));
    }

    @Test
    @Order(11)
    public void sendUserId_invalidId_returnsErrorMessage() throws Exception {
        RequestBuilder requestBuilder = get("/user/send")
                .param("id", "1000");

        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(new MediaType(MediaType.TEXT_PLAIN, StandardCharsets.UTF_8)))
                .andExpect(content().string("Пользователя с таким id не существует!"));
    }

    @Test
    @Order(12)
    public void sendTransactionalUserId_validId_returnsStatusOk() throws Exception {
        RequestBuilder requestBuilder = get("/user/send-tx")
                .param("id", "1");

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(new MediaType(MediaType.TEXT_PLAIN, StandardCharsets.UTF_8)))
                .andExpect(content().string("Пользователь Kostya отправлен!"));
    }

    @Test
    @Order(13)
    public void sendTransactionalUserId_invalidId_returnsErrorMessage() throws Exception {
        RequestBuilder requestBuilder = get("/user/send-tx")
                .param("id", "1000");

        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(new MediaType(MediaType.TEXT_PLAIN, StandardCharsets.UTF_8)))
                .andExpect(content().string("Пользователя с таким id не существует!"));
    }

    @Test
    @Order(14)
    public void sendBatchUsers_returnsStatusOk() throws Exception {
        RequestBuilder requestBuilder = get("/user/send-all");

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(new MediaType(MediaType.TEXT_PLAIN, StandardCharsets.UTF_8)))
                .andExpect(content().string("Пользователи отправлены"));
    }
}
