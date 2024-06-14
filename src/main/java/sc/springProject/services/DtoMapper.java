package sc.springProject.services;

import org.springframework.stereotype.Service;
import sc.springProject.dto.UserDto;
import sc.springProject.entities.User;

@Service
public class DtoMapper {

    public UserDto mapToUserDto(User user){
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .age(user.getAge())
                .salary(user.getSalary())
                .department(user.getDepartment().getName())
                .build();
    }
}
