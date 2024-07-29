package sc.springProject.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

@Data
@Entity
@Immutable
@NoArgsConstructor
@Table(name = "users_view")
public class UserView {

    @Id
    @SequenceGenerator(name = "users_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_seq")
    private long id;

    private String name;

    private int age;

    private int salary;

    private String department;

    public UserView(User user){
        id = user.getId();
        name = user.getName();
        age = user.getAge();
        salary = user.getSalary();
        department = user.getDepartment().getName();
    }
}

