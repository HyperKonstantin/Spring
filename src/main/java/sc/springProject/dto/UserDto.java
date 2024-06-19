package sc.springProject.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDto {

    private long id;

    private String name;

    private int age;

    private int salary;

    private double averageDepartmentSalary;

    private String department;
}
