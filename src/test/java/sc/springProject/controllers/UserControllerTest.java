package sc.springProject.controllers;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc

public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserController userController;

    @Test
    public void controllerInContext() throws Exception{
        assertThat(userController).isNotNull();
    }

    @Test
    public void getUserTest() throws Exception{
        this.mockMvc.perform(get("/get-users"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void findUserTest() throws Exception{
        this.mockMvc.perform(get("/find-user").param("name", "Kostya"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void deleteCorrectIdUserTest() throws Exception{
        this.mockMvc.perform(get("/delete-user").param("id", "2"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void deleteUncorrectIdUserTest() throws Exception{
        this.mockMvc.perform(get("/delete-user").param("id", "5"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}
