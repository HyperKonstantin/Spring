package sc.springProject.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "Users")
@Getter
@Setter
@NoArgsConstructor
//@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String name;

    private int age;

    private String phone;

    //@JsonManagedReference
    @ManyToOne
    @JoinColumn(name = "department")
    private Department department;

    public User(String name, int age, String phone, Department department) {
        this.name = name;
        this.age = age;
        this.phone = phone;
        this.department = department;
    }


}
