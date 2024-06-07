package sc.SpringProject.Entities;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
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
    private int id;

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
