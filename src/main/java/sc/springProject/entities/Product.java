package sc.springProject.entities;

import jakarta.persistence.*;
import lombok.*;

@ToString
@Entity
@Data
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    private int price;
}
