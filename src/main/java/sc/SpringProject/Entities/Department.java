package sc.SpringProject.Entities;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "Departments")
@Getter
@Setter
@NoArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "department")
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "departmentId")
    private long departmentId;

    private String department;

    //@JsonBackReference
    @OneToMany(mappedBy = "department", cascade=CascadeType.ALL, orphanRemoval=true)
    private List<User> users;

    public Department(String name) {
        department = name;
    }
}
