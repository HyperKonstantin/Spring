package sc.springProject.controllers;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
public class DepartmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @Order(1)
    public void getDepartments_returnsDepartments() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/department/get");

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @Order(2)
    public void FindUserByDepartmentId_validDepartmentId_returnsUserDtoList() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/department/find-users")
                .param("id", "1");

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
                                "averageDepartmentSalary": null ,
                                "department": "development"
                            }
                        ]
                        """));
    }

    @Test
    @Order(3)
    public void FindUserByDepartmentId_invalidDepartmentId_returnsErrorMessage() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/department/find-users")
                .param("id", "1000");

        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(new MediaType(MediaType.TEXT_PLAIN, StandardCharsets.UTF_8)))
                .andExpect(content().string("такого отдела нет"));
    }

    @Test
    @Order(4)
    public void addDepartment_returnNewDepartment() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/department/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "name": "Security"
                        }
                        """);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("""
                        {
                            "name" : "Security",
                            "users": null,
                            "averageSalary": null
                        }
                        """))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    @Order(5)
    public void swapDepartments_validUserId_returnsSuccessMessage() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/department/swap-user-departments")
                .param("firstUserId", "1")
                .param("secondUserId", "2");

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(new MediaType(MediaType.TEXT_PLAIN, StandardCharsets.UTF_8)))
                .andExpect(content().string("Отделы заменены!"));
    }

    @Test
    @Order(6)
    public void swapDepartments_invalidUserId_returnsErrorMessage() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/department/swap-user-departments")
                .param("firstUserId", "1000")
                .param("secondUserId", "2000");

        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(new MediaType(MediaType.TEXT_PLAIN, StandardCharsets.UTF_8)))
                .andExpect(content().string("Пользователя с таким id не существует!"));
    }
}
