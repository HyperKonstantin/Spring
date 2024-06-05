package test.SpringProject.Entities;

import jakarta.persistence.*;

@Entity
@Table(name = "Users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String name;
    private int age;
    private String phone;

    @ManyToOne
    @JoinColumn(name = "departmentId")
    private Department department;

    public User() {
    }

    public User(String name, int age, String phone, Department department) {
        this.name = name;
        this.age = age;
        this.phone = phone;
        this.department = department;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() { return age; }

    public void setAge(int age) {
        this.age = age;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Department getDepartment() { return department; }

    public void setDepartment(Department department) {
        this.department = department;
    }

}
