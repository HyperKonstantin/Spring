package sc.SpringProject.Entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Departments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "departmentId")
    private long departmentId;

    private String department;
}
