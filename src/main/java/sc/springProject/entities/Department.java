package sc.springProject.entities;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "Departments")
@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "name")
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "Id")
    private long id;

    private String name;

    //@JsonBackReference
    @OneToMany(mappedBy = "department", cascade=CascadeType.ALL, orphanRemoval=true, fetch = FetchType.LAZY)
    private List<User> users;

    @Column(name = "average_salary")
    private Double averageSalary;

    public Department(String name) {
        this.name = name;
    }
}
