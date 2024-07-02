package sc.springProject.services;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import sc.springProject.dto.UserDto;
import sc.springProject.entities.Department;
import sc.springProject.entities.User;
import sc.springProject.repositories.UserRepository;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Slf4j
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private DtoMapper dtoMapper;

    @InjectMocks
    private UserService userService;

    @Test
    @Order(1)
    public void getAllUsers_returnsValidUsersDto() throws JsonProcessingException {
        Department department = new Department("debug");
        List<User> users = new ArrayList<>();
        users.add(new User("Vlad", 22, 1440, department));
        users.add(new User("Egor", 63, 3500, department));
        Mockito.when(userRepository.findAll()).thenReturn(users);

        UserDto firstUserDto = getTestUserDto(users.get(0));
        UserDto secondUserDto = getTestUserDto(users.get(1));
        Mockito.doReturn(firstUserDto).when(dtoMapper).mapToUserDto(Mockito.eq(users.get(0)));
        Mockito.doReturn(secondUserDto).when(dtoMapper).mapToUserDto(Mockito.eq(users.get(1)));

        List<UserDto> usersDto = userService.getAllUsers();

        Assertions.assertNotNull(usersDto);
        Assertions.assertEquals(2, usersDto.size());
        Assertions.assertEquals(firstUserDto, usersDto.get(0));
        Assertions.assertEquals(secondUserDto, usersDto.get(1));
    }

    private UserDto getTestUserDto(User user){
        return UserDto.builder()
                .name(user.getName())
                .age(user.getAge())
                .salary(user.getSalary())
                .department(user.getDepartment().getName())
                .averageDepartmentSalary(user.getAverageDepartmentSalary())
                .build();
    }
}